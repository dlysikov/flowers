package lu.luxtrust.flowers.repository;

import lu.luxtrust.flowers.entity.enrollment.ESealOrder;
import lu.luxtrust.flowers.entity.enrollment.Order;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ESealOrderRepository extends OrderRepository<ESealOrder> {

    @Query("select h.unit from ESealOrder h where h.id=?1")
    Unit findUnitByOrderId(Long orderId);

    @Query("select o.lrsOrderNumber from ESealOrder o where o.status = ?1")
    List<String> findLrsOrderNumbersForStatus(OrderStatus status);

    @Modifying
    @Transactional
    @Query("update ESealOrder set status = ?1 where lrsOrderNumber = ?2")
    void updateStatus(OrderStatus status, String lrsOrderNumber);

    @Query("select o.ssn from ESealOrder o where o.lrsOrderNumber=?1")
    String getSsnByLrsOrderNumber(String lrsOrderNumber);

    @Query("select o from ESealOrder o where o.lrsOrderNumber=?1")
    ESealOrder findOneByLrsOrderNumber(String lrsOrderNumber);

    @Query("select o.status from ESealOrder o where o.id=?1")
    OrderStatus getStatusById(Long id);

}
