package lu.luxtrust.flowers.repository;

import lu.luxtrust.flowers.entity.enrollment.Order;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface OrderRepository<O extends Order> extends JpaRepository<O, Long>, JpaSpecificationExecutor<O> {

    Unit findUnitByOrderId(Long orderId);

    List<String> findLrsOrderNumbersForStatus(OrderStatus status);

    void updateStatus(OrderStatus status, String lrsOrderNumber);

    String getSsnByLrsOrderNumber(String lrsOrderNumber);

    O findOneByLrsOrderNumber(String lrsOrderNumber);
}
