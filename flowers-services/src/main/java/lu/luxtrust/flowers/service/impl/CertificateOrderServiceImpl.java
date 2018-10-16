package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.entity.enrollment.*;
import lu.luxtrust.flowers.entity.system.ResponseAPI;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.fsm.FsmExtendedStateVariables;
import lu.luxtrust.flowers.fsm.OrderEvent;
import lu.luxtrust.flowers.fsm.OrderStateMachineFactory;
import lu.luxtrust.flowers.model.PageParams;
import lu.luxtrust.flowers.model.PageResponse;
import lu.luxtrust.flowers.repository.DocumentRepository;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import lu.luxtrust.flowers.repository.OrderUserValidatePageRepository;
import lu.luxtrust.flowers.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.http.ResponseEntity;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static lu.luxtrust.flowers.service.impl.OrderSpecifications.sameAs;

@DependsOn("responseAPIServiceImpl")
@Service
@Transactional
public class CertificateOrderServiceImpl extends OrderServiceImpl<CertificateOrder> implements CertificateOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CertificateOrderServiceImpl.class);
    private static final List<OrderStatus> ORDER_STATUSES_TO_NOTIFY_USER = Arrays.asList(OrderStatus.SENT_TO_LRS, OrderStatus.LRS_ONGOING);

    private final OrderUserValidatePageRepository orderUserValidatePageRepository;
    private final CertificateOrderRepository orderRepository;
    private final DocumentRepository documentRepository;
    private final OrderStateMachineFactory orderStateMachineFactory;
    private final ResponseAPIService responseAPIService;
    private final NotificationService notificationService;

    @Autowired
    public CertificateOrderServiceImpl(CertificateOrderRepository orderRepository,
                                       DocumentRepository documentRepository,
                                       OrderStateMachineFactory orderStateMachineFactory,
                                       OrderUserValidatePageRepository orderUserValidatePageRepository,
                                       LrsService lrsService,
                                       ResponseAPIService responseAPIService, NotificationService notificationService) {
        super(orderRepository, lrsService);
        this.orderRepository = orderRepository;
        this.orderUserValidatePageRepository = orderUserValidatePageRepository;
        this.documentRepository = documentRepository;
        this.orderStateMachineFactory = orderStateMachineFactory;
        this.responseAPIService = responseAPIService;
        this.notificationService = notificationService;
    }

    @Override
    public CertificateOrder findById(Long id) {
        CertificateOrder order = orderRepository.findOne(id);
        if (order != null && order.getHolder().getDocuments() != null) {
            order.getHolder().getDocuments().size();
        }
        return order;
    }

    @Override
    public CertificateOrder saveOrder(CertificateOrder order) {
        return orderRepository.save(prepareToSave(order));
    }

    private CertificateOrder prepareToSave(CertificateOrder orderEntity) {
        orderEntity.setStatus(orderStateMachineFactory.getInitialState());
        return orderEntity;
    }

    @Override
    public CertificateOrder notifyOrderHolder(CertificateOrder orderEntity) throws Exception {
        return sendEventToFSM(orderEntity, OrderEvent.MOVE_TO_USER_DRAFT);
    }

    @Override
    public CertificateOrder createAndNotifyOrderHolder(CertificateOrder order) throws Exception {
        return notifyOrderHolder(saveOrder(order));
    }

    @Override
    public List<CertificateOrder> findDuplicates(Collection<CertificateOrder> orders) {
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }
        Specifications<CertificateOrder> spec = Specifications.where(sameAs(orders));
        return orderRepository.findAll(spec);
    }

    @Override
    public void saveAll(Collection<CertificateOrder> orders) {
        OrderStatus initialStatus = orderStateMachineFactory.getInitialState();
        for (Order order : orders) {
            order.setStatus(initialStatus);
        }
        orderRepository.save(orders);
    }

    @Override
    public CertificateOrder findOrderToValidateByUser(String pageHash) {
        OrderUserValidatePage page = orderUserValidatePageRepository.findNotExpiredByPageHash(pageHash);
        return page == null ? null : page.getOrder();
    }

    @Override
    public CertificateOrder validateByUser(CertificateOrder order) throws Exception {
        return sendEventToFSM(order, OrderEvent.USER_VALIDATES);
    }

    private List<Document> getDocumentList(Map<String, InputStream> fileName2Stream, Long orderId) throws IOException {
        Holder holder = new Holder();
        holder.setId(orderRepository.findHolderIdForOrder(orderId));
        List<Document> documents = new ArrayList<>(fileName2Stream.size());
        for (Map.Entry<String, InputStream> file : fileName2Stream.entrySet()) {
            try (InputStream stream = file.getValue()) {
                Document doc = new Document();
                doc.setName(file.getKey());
                doc.setFile(StreamUtils.copyToByteArray(stream));
                doc.setHolder(holder);
                documents.add(doc);
            }
        }
        return documents;
    }

    @Override
    public List<Document> uploadDocumentsToOrder(Map<String, InputStream> fileName2Stream, Long orderId) throws IOException {
        return documentRepository.save(getDocumentList(fileName2Stream, orderId));
    }

    @Override
    public List<Document> uploadDocumentsToOrderWithRemove(Map<String, InputStream> fileName2Stream, Long orderId) throws IOException {
        documentRepository.removeAllForHolder(orderRepository.findHolderIdForOrder(orderId));
        return documentRepository.save(getDocumentList(fileName2Stream, orderId));
    }

    public int removeDocumentByName(Long holderId, String documentName) {
        return documentRepository.removeDocumentByName(holderId, documentName);
    }

    @Override
    public PageResponse<CertificateOrder> findAll(Unit unit, PageParams params) {
        PredicateProducer pp = (cb, root) -> unit != null ? Optional.of(cb.equal(root.get("unit").get("id"), unit.getId())) :  Optional.empty();
        return findAll(params, false, pp);
    }

    @Override
    public void sendToLrs(CertificateOrder order) throws Exception {
        sendEventToFSM(order, OrderEvent.SEND_TO_LRS);
    }

    @Override
    public CertificateOrder createAndSendToLrs(CertificateOrder order) throws Exception {
        sendToLrs(saveOrder(order));
        return order;
    }

    @Override
    public void reject(CertificateOrder order) throws Exception {
        sendEventToFSM(order, OrderEvent.REJECT);
    }

    private CertificateOrder sendEventToFSM(CertificateOrder order, OrderEvent event) throws Exception {
        StateMachine<OrderStatus, OrderEvent> stateMachine = orderStateMachineFactory.newStateMachine(order.getId());
        stateMachine.getExtendedState().getVariables().put(FsmExtendedStateVariables.ORDER, order);
        stateMachine.sendEvent(event);
        stateMachine.stop();
        if (stateMachine.hasStateMachineError()) {
            throw stateMachine.getExtendedState().get(FsmExtendedStateVariables.EXCEPTION, Exception.class);
        }
        order.setStatus(stateMachine.getState().getId());
        return orderRepository.save(order);
    }

    @Override
    public void updateStatus(String orderNumber, OrderStatus currentStatus, OrderStatus newStatus) {
        super.updateStatus(orderNumber, currentStatus, newStatus);
        if (needToNotifyUser(currentStatus, newStatus)) {
            try {
                this.notificationService.notifyEndUserToActivateCertificate(orderRepository.findOneByLrsOrderNumber(orderNumber));
            } catch (Exception e) {
                LOGGER.error("failed to notify user for lrs_order_number {}", orderNumber, e);
            }
        }
    }

    private boolean needToNotifyUser(OrderStatus currentStatus, OrderStatus newStatus) {
        return ORDER_STATUSES_TO_NOTIFY_USER.contains(currentStatus) && newStatus == OrderStatus.LRS_PRODUCED;
    }

    @Override
    public CertificateOrder enrichOrder(String orderNumber, String ssn, List<Certificate> certificates) {
        CertificateOrder order = super.enrichOrder(orderNumber, ssn, certificates);
        sendBackResponse(order);
        return order;
    }

    private ResponseEntity sendBackResponse(CertificateOrder order) {
        ResponseEntity responseEntity = null;
        ResponseAPI responseAPI = responseAPIService.findByUserExternalIdAndRequestor(order.getUserExternalId(), order.getUnit().getRequestor());
        if (responseAPI != null) {
            responseAPI.setSsn(order.getSsn());
            responseAPI.setDevice(order.getDevice());
            responseEntity = responseAPIService.sendBackResponse(responseAPI);
        }
        return responseEntity;
    }

}
