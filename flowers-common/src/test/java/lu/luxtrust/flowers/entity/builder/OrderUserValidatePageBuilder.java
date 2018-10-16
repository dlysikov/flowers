package lu.luxtrust.flowers.entity.builder;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.OrderUserValidatePage;

import java.util.Date;

public class OrderUserValidatePageBuilder {

    private Long id;
    private CertificateOrder order;
    private Date expirationTime;
    private String mobileValidationCode = "123456";
    private String pageHash = "ASDASDASDASD";

    private OrderUserValidatePage target;

    private OrderUserValidatePageBuilder() {
        this.expirationTime = new Date(new Date().getTime() + 11111111111L);
    }

    public OrderUserValidatePageBuilder pageHash(String pageHash) {
        this.pageHash = pageHash;
        return this;
    }

    public OrderUserValidatePageBuilder expirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
        return this;
    }

    public OrderUserValidatePageBuilder order(CertificateOrder order) {
        this.order = order;
        return this;
    }

    public OrderUserValidatePageBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public OrderUserValidatePageBuilder mobileValidationCode(String mobileValidationCode) {
        this.mobileValidationCode = mobileValidationCode;
        return this;
    }

    public static OrderUserValidatePageBuilder newBuilder() {
        return new OrderUserValidatePageBuilder();
    }

    public OrderUserValidatePage build() {
        target = new OrderUserValidatePage();
        target.setId(id);
        target.setOrder(order);
        target.setMobileValidationCode(mobileValidationCode);
        target.setPageHash(pageHash);
        target.setExpirationTime(expirationTime);
        return target;
    }
}
