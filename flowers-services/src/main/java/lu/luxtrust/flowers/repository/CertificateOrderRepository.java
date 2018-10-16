package lu.luxtrust.flowers.repository;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CertificateOrderRepository extends OrderRepository<CertificateOrder> {

    @Query("select o from CertificateOrder o join fetch o.holder where o.lrsOrderNumber=?1")
    CertificateOrder findOneByLrsOrderNumber(String lrsOrderNumber);

    @Query("select o.status from CertificateOrder o where o.id = ?1")
    OrderStatus findOrderStatusById(Long orderId);

    @Query("select o.lrsOrderNumber from CertificateOrder o where o.status = ?1")
    List<String> findLrsOrderNumbersForStatus(OrderStatus status);

    @Modifying
    @Transactional
    @Query("update CertificateOrder set status = ?1 where lrsOrderNumber = ?2")
    void updateStatus(OrderStatus status, String lrsOrderNumber);

    @Query("select o.holder.id from CertificateOrder o where o.id=?1")
    Long findHolderIdForOrder(Long orderId);

    @Query("select o.issuer from CertificateOrder o where o.id=?1")
    User findIssuer(Long orderId);

    @Query("select o from CertificateOrder o left join o.validationPages p where o.status='USER_DRAFT' and p.id is null")
    List<CertificateOrder> findOrdersWithoutActiveValidationPage();

    @Query("select h.unit from CertificateOrder h where h.id=?1")
    Unit findUnitByOrderId(Long orderId);

    @Query("select ordr from CertificateOrder ordr join ordr.unit u join u.requestor r where ordr.userExternalId = ?1 and r.id=?2")
    CertificateOrder findByUserExternalIdAndRequestor(String userExternalId, Long requestorId);

    @Query("select o.ssn from CertificateOrder o where o.lrsOrderNumber=?1")
    String getSsnByLrsOrderNumber(String lrsOrderNumber);


    boolean existsByUserExternalIdAndUnitRequestor(String userExternalId, Requestor requestor);
}
