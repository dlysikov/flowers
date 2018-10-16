package lu.luxtrust.flowers.entity.builder;

import lu.luxtrust.flowers.entity.system.RequestorConfiguration;

public class RequestorConfigurationBuilder {

    private Long id;
    private Long orderValidationPageTTL = 11111111l;
    private Boolean shortFlow = Boolean.FALSE;

    private RequestorConfiguration target;

    private RequestorConfigurationBuilder() {

    }

    public static RequestorConfigurationBuilder newBuilder() {
        return new RequestorConfigurationBuilder();
    }

    public RequestorConfigurationBuilder shortFlow(Boolean shortFlow) {
        this.shortFlow = shortFlow;
        return this;
    }

    public RequestorConfigurationBuilder orderValidationPageTTL(Long orderValidationPageTTL) {
        this.orderValidationPageTTL = orderValidationPageTTL;
        return this;
    }

    public RequestorConfigurationBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public RequestorConfiguration build() {
        this.target = new RequestorConfiguration();
        this.target.setId(id);
        this.target.setOrderValidationPageTTL(orderValidationPageTTL);
        this.target.setShortFlow(shortFlow);
        return target;
    }
}
