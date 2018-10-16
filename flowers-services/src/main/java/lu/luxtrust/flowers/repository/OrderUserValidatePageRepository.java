package lu.luxtrust.flowers.repository;

import lu.luxtrust.flowers.entity.enrollment.OrderUserValidatePage;
import lu.luxtrust.flowers.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface OrderUserValidatePageRepository extends JpaRepository<OrderUserValidatePage, Long> {

    @Query("select p from OrderUserValidatePage p where p.pageHash=?1 and p.expirationTime> CURRENT_TIMESTAMP")
    OrderUserValidatePage findNotExpiredByPageHash(String pageHash);

    @Query("select p.order.status from OrderUserValidatePage p where p.pageHash=?1 and p.expirationTime > CURRENT_TIMESTAMP")
    OrderStatus findOrderStatusForNotExpiredPageHash(String pageHash);

    @Query("select p.order.id from OrderUserValidatePage p where p.pageHash = ?1")
    Long findOrderIdByPageHash(String pageHash);

    @Modifying
    @Query("delete from OrderUserValidatePage p where p.expirationTime < CURRENT_TIMESTAMP")
    int removePagesWithExpirationDateLessThenCurrent();

    @Modifying
    void removeByOrderId(Long orderId);

    @Query("select p.id from OrderUserValidatePage p where p.pageHash=?1 and p.mobileValidationCode =?2 and p.expirationTime > CURRENT_TIMESTAMP")
    Long findIdByPageHashAndMobileValidationCode(String pageHash, String mobileValidationCode);
}
