package lu.luxtrust.flowers.entity.system;

import lu.luxtrust.flowers.entity.common.AbstractEntity;
import lu.luxtrust.flowers.entity.common.ID;

import javax.persistence.*;

@Entity
@Table(name = "requestor_config")
@SequenceGenerator(sequenceName = "requestor_config_id", name = "requestor_config_id")
public class RequestorConfiguration extends AbstractEntity implements ID<Long> {

    public enum ValidatedBy {
        CSD, DIA
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requestor_config_id")
    @Column(name = "id")
    private Long id;

    @Column(name = "order_validation_page_ttl")
    private Long orderValidationPageTTL = 86400000L;

    @Column(name = "short_flow")
    private Boolean shortFlow = Boolean.FALSE;

    @Column(name = "remote_id")
    private Boolean remoteId = Boolean.FALSE;

    @Column(name = "response_url")
    private String responseURL;

    @Column(name = "csd_envolvement")
    private Boolean csdEnvolvement = Boolean.FALSE;

    @Transient
    private ValidatedBy validatedBy = ValidatedBy.DIA;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderValidationPageTTL() {
        return orderValidationPageTTL;
    }

    public void setOrderValidationPageTTL(Long orderValidationPageTTL) {
        this.orderValidationPageTTL = orderValidationPageTTL;
    }

    public Boolean getShortFlow() {
        return shortFlow;
    }

    public void setShortFlow(Boolean shortFlow) {
        this.shortFlow = shortFlow;
    }

    public Boolean getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(Boolean remoteId) {
        this.remoteId = remoteId;
    }

    public String getResponseURL() {
        return responseURL;
    }

    public void setResponseURL(String responseURL) {
        this.responseURL = responseURL;
    }

    public ValidatedBy getValidatedBy() {
        return this.validatedBy;
    }

    public void setValidatedBy(ValidatedBy validatedBy) {
        this.validatedBy = validatedBy;
    }

    public Boolean getCsdEnvolvement() {
        return csdEnvolvement;
    }

    public void setCsdEnvolvement(Boolean csdEnvolvement) {
        this.csdEnvolvement = csdEnvolvement;
    }
}
