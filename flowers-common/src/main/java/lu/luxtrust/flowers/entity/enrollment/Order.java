package lu.luxtrust.flowers.entity.enrollment;

import lu.luxtrust.flowers.entity.common.AbstractEntity;
import lu.luxtrust.flowers.entity.common.ID;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.validation.annotation.NestedObjectByGroup;
import lu.luxtrust.flowers.validation.annotation.NestedObjectByGroupWrapper;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static lu.luxtrust.flowers.validation.ValidationGroups.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "orders", indexes = {
        @Index(name = "status_index", columnList = "status"),
        @Index(name = "ssn_index", columnList = "ssn"),
        @Index(name = "lrs_order_number_index", columnList = "lrs_order_number")})
@SequenceGenerator(sequenceName = "order_id", name = "order_id")
public abstract class Order extends AbstractEntity implements Serializable, ID<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id")
    @Column(name = "id")
    private Long id;

    @Column(name="lrs_order_number")
    private String lrsOrderNumber;

    @Column(name = "ssn")
    private String ssn;

    @NotNull(groups = {OrderDraft.class, OrderUserDraftAPI.class})
    @NestedObjectByGroupWrapper(@NestedObjectByGroup(groups = OrderUserDraftAPI.class, fieldsExclusion = {"requestor"}))
    @ManyToOne()
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @Column(name = "publish")
    private Boolean publish = Boolean.FALSE;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.DRAFT;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", fetch = FetchType.LAZY)
    private List<Certificate> certificates;

    @Column(name = "accepted_gtc")
    private Boolean acceptedGTC = Boolean.FALSE;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_status_date")
    private Date lastStatusDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLrsOrderNumber() {
        return lrsOrderNumber;
    }

    public void setLrsOrderNumber(String lrsOrderNumber) {
        this.lrsOrderNumber = lrsOrderNumber;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(Boolean publish) {
        this.publish = publish;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        if (!Objects.equals(this.status, status) || this.lastStatusDate == null) {
            this.setLastStatusDate(new Date());
        }
        this.status = status;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }

    public Boolean getAcceptedGTC() {
        return acceptedGTC;
    }

    public void setAcceptedGTC(Boolean acceptedGTC) {
        this.acceptedGTC = acceptedGTC;
    }

    public Date getLastStatusDate() {
        return lastStatusDate;
    }

    public void setLastStatusDate(Date lastStatusDate) {
        this.lastStatusDate = lastStatusDate;
    }
}
