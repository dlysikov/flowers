package lu.luxtrust.flowers.entity.enrollment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.Device;
import lu.luxtrust.flowers.validation.annotation.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.util.Date;
import java.util.List;

import static lu.luxtrust.flowers.validation.ValidationGroups.*;

@Entity
@PrimaryKeyJoinColumn(name = "id")
@Table(name = "certificate_order", indexes = {
        @Index(name = "device_index", columnList = "device"),
})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@GTCAccepted(groups = OrderUserDraft.class)
@TokenSerialNumberFilled(groups = OrderSentToLRS.class)
@DeliveryAddressFilledGroup({
        @DeliveryAddressFilled(),
        @DeliveryAddressFilled(groups = OrderUserDraft.class, nullValid = false)
})
public class CertificateOrder extends Order {

    @NotNull(groups = OrderUserDraft.class)
    @XmlAttribute
    @Enumerated(EnumType.STRING)
    @Column(name = "device")
    private Device device;

    @XmlAttribute
    @TokenSerialNumber
    @Column(name = "token_serial_number")
    private String tokenSerialNumber;

    @XmlAttribute
    @Temporal(TemporalType.DATE)
    @Column(name = "request_date")
    private Date requestDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issuer_id", updatable = false)
    private User issuer;

    @XmlElement
    @Valid
    @NotNull(groups = {OrderDraft.class, OrderUserDraftAPI.class})
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "holder_id")
    private Holder holder;

    @XmlElement
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "remote_id")
    private Boolean remoteId = Boolean.FALSE;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private List<OrderUserValidatePage> validationPages;

    @Column(name = "user_external_id")
    @NotEmpty(groups = OrderUserDraftAPI.class)
    @XmlTransient
    @Length(max = 100)
    private String userExternalId;

    @XmlAttribute
    @Length(max = 50)
    @Column(name = "department")
    private String department;

    public CertificateOrder() {

    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<OrderUserValidatePage> getValidationPages() {
        return validationPages;
    }

    public void setValidationPages(List<OrderUserValidatePage> validationPages) {
        this.validationPages = validationPages;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getTokenSerialNumber() {
        return tokenSerialNumber;
    }

    public void setTokenSerialNumber(String tokenSerialNumber) {
        this.tokenSerialNumber = tokenSerialNumber;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Holder getHolder() {
        return holder;
    }

    public void setHolder(Holder holder) {
        this.holder = holder;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public User getIssuer() {
        return issuer;
    }

    public void setIssuer(User issuer) {
        this.issuer = issuer;
    }

    public Boolean getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(Boolean remoteId) {
        this.remoteId = remoteId;
    }

    public String getUserExternalId() {
        return userExternalId;
    }

    public void setUserExternalId(String userExternalId) {
        this.userExternalId = userExternalId;
    }

    @PrePersist
    @PreUpdate
    public void initRequestDate() {
        if (this.requestDate == null) {
            this.requestDate = new Date();
        }
    }

}
