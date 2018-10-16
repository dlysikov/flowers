package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.entity.enrollment.*;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.model.PageParams;
import lu.luxtrust.flowers.model.PageResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CertificateOrderService extends OrderService<CertificateOrder> {

    CertificateOrder findById(Long id);

    PageResponse<CertificateOrder> findAll(Unit unit, PageParams params);

    CertificateOrder saveOrder(CertificateOrder order);

    CertificateOrder createAndSendToLrs(CertificateOrder order) throws Exception;

    List<CertificateOrder> findDuplicates(Collection<CertificateOrder> orders);

    CertificateOrder notifyOrderHolder(CertificateOrder order) throws Exception;

    CertificateOrder createAndNotifyOrderHolder(CertificateOrder order) throws Exception;

    void saveAll(Collection<CertificateOrder> orders);

    CertificateOrder findOrderToValidateByUser(String pageHash);

    CertificateOrder validateByUser(CertificateOrder order) throws Exception;

    List<Document> uploadDocumentsToOrder(Map<String, InputStream> fileName2Stream, Long orderId) throws IOException;

    List<Document> uploadDocumentsToOrderWithRemove(Map<String, InputStream> fileName2Stream, Long orderId) throws IOException;

    void reject(CertificateOrder order) throws Exception;

    int removeDocumentByName(Long holderId, String documentName);

}
