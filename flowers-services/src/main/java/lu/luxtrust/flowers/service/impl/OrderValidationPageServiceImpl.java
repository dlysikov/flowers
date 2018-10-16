package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.OrderUserValidatePage;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.RequestorConfiguration;
import lu.luxtrust.flowers.model.SMS;
import lu.luxtrust.flowers.repository.OrderUserValidatePageRepository;
import lu.luxtrust.flowers.repository.RequestorRepository;
import lu.luxtrust.flowers.service.OrderRandomsGeneratorGenerator;
import lu.luxtrust.flowers.service.OrderValidationPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class OrderValidationPageServiceImpl implements OrderValidationPageService {

    private static final int MOBILE_CODE_LENGTH = 7;

    private static final Logger LOG = LoggerFactory.getLogger(OrderValidationPageServiceImpl.class);

    private final OrderUserValidatePageRepository orderUserValidatePageRepository;
    private final OrderRandomsGeneratorGenerator orderRandomsGeneratorGenerator;
    private final RequestorRepository RequestorRepository;

    @Autowired
    public OrderValidationPageServiceImpl(OrderUserValidatePageRepository orderUserValidatePageRepository,
                                          RequestorRepository RequestorRepository,
                                          OrderRandomsGeneratorGenerator orderRandomsGeneratorGenerator) {
        this.orderUserValidatePageRepository = orderUserValidatePageRepository;
        this.orderRandomsGeneratorGenerator = orderRandomsGeneratorGenerator;
        this.RequestorRepository = RequestorRepository;
    }

    @Override
    public OrderUserValidatePage generateValidationPage(CertificateOrder order) {
        return generateValidationPages(Collections.singletonList(order)).get(0);
    }

    @Override
    public List<OrderUserValidatePage> generateValidationPages(Collection<CertificateOrder> orders) {
        LOG.info("Generating user validation pages for {} orders", orders.size());

        Map<Long, String> orderId2pageHash = new HashMap<>(orders.size() * 2);
        List<OrderUserValidatePage> validationPages = new ArrayList<>(orders.size());

        orders.forEach((order) -> {
            OrderUserValidatePage page = new OrderUserValidatePage();
            page.setPageHash(orderRandomsGeneratorGenerator.hash());
            page.setOrder(order);
            page.setMobileValidationCode(orderRandomsGeneratorGenerator.codeWithDigits(MOBILE_CODE_LENGTH));
            page.setExpirationTime(generateExpirationTime(order.getUnit().getRequestor()));

            validationPages.add(page);
            orderId2pageHash.put(order.getId(), page.getPageHash());
        });
        List<OrderUserValidatePage> saved = orderUserValidatePageRepository.save(validationPages);

        LOG.info("User validation pages have been generated order_id->page_hash {}", orderId2pageHash);

        return saved;
    }

    private Date generateExpirationTime(Requestor requestor) {
        RequestorConfiguration config = RequestorRepository.findConfiguration(requestor.getId());
        Date currentTime = new Date();
        return new Date(currentTime.getTime() + config.getOrderValidationPageTTL());
    }

    @Override
    public SMS generateMobileValidationSMS(OrderUserValidatePage page) {
        String content =
                "Code: " + page.getMobileValidationCode() +"\n" +
                        "Click on the link you have received by E-mail\n" +
                        "and enter this code to validate your form.\n" +
                        "Info? (+352) 24 550 550";

        return new SMS(content, page.getOrder().getHolder().getPhoneNumber(), "---Flowers Entry Code---");
    }
}
