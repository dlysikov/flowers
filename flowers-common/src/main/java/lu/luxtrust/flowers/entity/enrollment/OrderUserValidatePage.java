package lu.luxtrust.flowers.entity.enrollment;

import lu.luxtrust.flowers.entity.common.AbstractEntity;
import lu.luxtrust.flowers.entity.common.ID;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "order_user_validate_page", indexes = {
        @Index(name = "pagehash_and_mobile_code", columnList = "page_hash, mobile_validation_code")
})
@SequenceGenerator(sequenceName = "order_user_validate_page_id", name = "order_user_validate_page_id")
public class OrderUserValidatePage extends AbstractEntity implements ID<Long> {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_user_validate_page_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private CertificateOrder order;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expiration_time", nullable = false)
    private Date expirationTime;

    @Column(name = "page_hash", nullable = false, unique = true)
    private String pageHash;

    @Column(name = "mobile_validation_code", nullable = false)
    private String mobileValidationCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CertificateOrder getOrder() {
        return order;
    }

    public void setOrder(CertificateOrder order) {
        this.order = order;
    }

    public String getPageHash() {
        return pageHash;
    }

    public void setPageHash(String pageHash) {
        this.pageHash = pageHash;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getMobileValidationCode() {
        return mobileValidationCode;
    }

    public void setMobileValidationCode(String mobileValidationCode) {
        this.mobileValidationCode = mobileValidationCode;
    }
}
