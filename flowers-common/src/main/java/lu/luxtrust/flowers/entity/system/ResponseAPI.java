package lu.luxtrust.flowers.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lu.luxtrust.flowers.entity.common.AbstractEntity;
import lu.luxtrust.flowers.enums.StatusAPI;
import lu.luxtrust.flowers.enums.Device;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "response_api", indexes = {
        @Index(name = "response_api_ext_id", columnList = "user_external_id,requestor_id", unique = true)
})
@SequenceGenerator(sequenceName = "response_api_id", name = "response_api_id")
public class ResponseAPI extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "response_api_id")
    @Column(name = "id")
    private Long id;

    @Column(name = "user_external_id")
    private String userExternalId;

    @Column(name = "ssn")
    private String ssn;

    @JsonIgnore
    @Column(name = "service_ssn")
    private String serviceSSN;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private Requestor requestor;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusAPI status;

    @Column(name = "device")
    @Enumerated(EnumType.STRING)
    private Device device;

    @JsonIgnore
    @Temporal(TemporalType.DATE)
    @Column(name = "modified_date")
    private Date modifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Requestor getRequestor() {
        return requestor;
    }

    public void setRequestor(Requestor requestor) {
        this.requestor = requestor;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getServiceSSN() {
        return serviceSSN;
    }

    public void setServiceSSN(String serviceSSN) {
        this.serviceSSN = serviceSSN;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setCurrentDate() {
        this.modifiedDate = new Date();
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getUserExternalId() {
        return userExternalId;
    }

    public void setUserExternalId(String userExternalId) {
        this.userExternalId = userExternalId;
    }

    public StatusAPI getStatus() {
        return status;
    }

    public void setStatus(StatusAPI status) {
        this.status = status;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
