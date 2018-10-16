package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.CompanyIdentifier;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.model.ImportResult;
import lu.luxtrust.flowers.repository.UnitRepository;
import lu.luxtrust.flowers.service.OrderBatchService;
import lu.luxtrust.flowers.service.CertificateOrderService;
import lu.luxtrust.flowers.service.StaticDataService;
import lu.luxtrust.flowers.service.xml.ChunkXMLReader;
import lu.luxtrust.flowers.service.xml.ChunkXMLReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderBatchServiceImpl implements OrderBatchService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderBatchServiceImpl.class);

    private final int xmlOrdersChunk;
    private final ChunkXMLReaderFactory<CertificateOrder> xmlReaderFactory;
    private final StaticDataService staticDataService;
    private final CertificateOrderService certificateOrderService;
    private final UnitRepository unitRepository;

    @Autowired
    public OrderBatchServiceImpl(@Value("${orders.xml.chunk-size:30}") int xmlOrdersChunk,
                                 ChunkXMLReaderFactory<CertificateOrder> xmlReaderFactory,
                                 CertificateOrderService certificateOrderService,
                                 UnitRepository unitRepository,
                                 StaticDataService staticDataService) {
        this.staticDataService = staticDataService;
        this.xmlOrdersChunk = xmlOrdersChunk;
        this.xmlReaderFactory = xmlReaderFactory;
        this.certificateOrderService = certificateOrderService;
        this.unitRepository = unitRepository;
    }

    @Override
    public ImportResult<CertificateOrder> processOrdersXML(InputStream inputStream, Long issuerId) {
        LOG.info("Starting import orders data from file");

        ImportResult<CertificateOrder> result = new ImportResult<>();
        try (ChunkXMLReader<CertificateOrder> reader = xmlReaderFactory.newReader(inputStream, CertificateOrder.class, "orders")) {

            List<CertificateOrder> ordersChunk;
            while (!(ordersChunk = reader.readChunk(xmlOrdersChunk)).isEmpty()) {
                Map<OrderIdXML, CertificateOrder> chunk = new HashMap<>();

                for (CertificateOrder orderDTO : ordersChunk) {
                    OrderIdXML key = new OrderIdXML(orderDTO);
                    if (chunk.containsKey(key)) {
                        result.incFailed();
                        result.addFailedDetails(orderDTO);
                    } else {
                        User user = new User();
                        user.setId(issuerId);
                        orderDTO.setIssuer(user);
                        orderDTO.setRequestDate(new Date());
                        chunk.put(key, orderDTO);
                    }
                }

                int currentChunkSize = chunk.size();
                LOG.info("Chunk with size {} parsed", currentChunkSize);

                certificateOrderService.findDuplicates(chunk.values()).forEach(duplicate -> {
                    chunk.remove(new OrderIdXML(duplicate));
                    result.incFailed();
                    result.addFailedDetails(duplicate);
                });

                LOG.info("{} duplicates found", currentChunkSize - chunk.size());

                if (!chunk.isEmpty()) {
                    enrich(chunk.values());
                    certificateOrderService.saveAll(chunk.values());
                    result.incSuccessFul(chunk.size());

                    LOG.info("Chunk with size {} persisted", currentChunkSize);
                }
            }
            LOG.info("Orders xml parserd with the following result: {} successful, {} duplicates", result.getSuccessful(), result.getFailed());
            return result;
        } catch (Exception e) {
            throw new FlowersException(e, result);
        }
    }

    private void enrich(Collection<CertificateOrder> orders) {
        this.staticDataService.enrichWithCountries(orders.stream().filter(o -> o.getAddress() != null).map(CertificateOrder::getAddress).collect(Collectors.toList()));
        this.staticDataService.enrichWithNationalities(orders.stream().filter(o -> o.getHolder() != null).map(CertificateOrder::getHolder).collect(Collectors.toList()));

        MultiValueMap<CompanyIdentifier, CertificateOrder> unitIdentifier2order = new LinkedMultiValueMap<>();

        for (CertificateOrder order : orders) {
            if (order.getUnit() != null && order.getUnit().getIdentifier() != null) {
                unitIdentifier2order.add(order.getUnit().getIdentifier(), order);
                order.setUnit(null);
            }
        }

        if (!unitIdentifier2order.isEmpty()) {
            List<Unit> units = unitRepository.findAllByIdentifierIn(unitIdentifier2order.keySet());
            units.forEach(u -> {
                unitIdentifier2order.get(u.getIdentifier()).forEach(o -> {
                    o.setUnit(u);
                });
            });
        }
    }

    private static class OrderIdXML {
        final String firstName;
        final String surName;
        final String email;

        OrderIdXML(CertificateOrder order) {
            this.firstName = order.getHolder().getFirstName();
            this.surName = order.getHolder().getSurName();
            this.email = order.getHolder().getNotifyEmail();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OrderIdXML that = (OrderIdXML) o;
            return Objects.equals(firstName, that.firstName) &&
                    Objects.equals(surName, that.surName) &&
                    Objects.equals(email, that.email);
        }

        @Override
        public int hashCode() {

            return Objects.hash(firstName, surName, email);
        }
    }
}
