package lu.luxtrust.flowers.entity.builder;

import lu.luxtrust.flowers.entity.enrollment.*;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.Device;
import lu.luxtrust.flowers.enums.OrderStatus;

import java.util.Date;

public class CertificateOrderBuilder {

    private Long id;
    private OrderStatus status = OrderStatus.DRAFT;
    private Device device = Device.MOBILE;
    private String tokenSerialNumber = "27-3686864-0";
    private Date requestDate = new Date();
    private boolean remoteId;
    private boolean acceptedGTC;
    private String lrsOrderNumber = "123123123";
    private String userExternalId;
    private User issuer;
    private Date lastStatusDate = new Date();
    private Address address = AddressBuilder.newBuilder().build();
    private Holder holder = HolderBuilder.newBuilder().build();
    private Unit unit = UnitBuilder.newBuilder().build();
    private String department = "Company department";
    private String ssn = "dfafasfasf";
    private boolean publish;

    private CertificateOrder order;

    private CertificateOrderBuilder() {
    }

    public CertificateOrderBuilder lastStatusDate(Date lastStatusDate) {
        this.lastStatusDate = lastStatusDate;
        return this;
    }

    public CertificateOrderBuilder publish(boolean publish) {
        this.publish = publish;
        return this;
    }

    public CertificateOrderBuilder ssn(String ssn) {
        this.ssn = ssn;
        return this;
    }

    public CertificateOrderBuilder department(String department) {
        this.department = department;
        return this;
    }

    public CertificateOrderBuilder unit(Unit unit) {
        this.unit = unit;
        return this;
    }

    public CertificateOrderBuilder lrsOrderNumber(String lrsOrderNumber) {
        this.lrsOrderNumber = lrsOrderNumber;
        return this;
    }

    public CertificateOrderBuilder acceptedGTC(boolean acceptedGTC) {
        this.acceptedGTC = acceptedGTC;
        return this;
    }

    public CertificateOrderBuilder remoteId(boolean remoteId) {
        this.remoteId = remoteId;
        return this;
    }

    public CertificateOrderBuilder requestDate(Date requestDate) {
        this.requestDate = requestDate;
        return this;
    }

    public CertificateOrderBuilder tokenSerialNumber(String tokenSerialNumber) {
        this.tokenSerialNumber = tokenSerialNumber;
        return this;
    }

    public CertificateOrderBuilder device(Device device) {
        this.device = device;
        return this;
    }

    public CertificateOrderBuilder holder(Holder holder) {
        this.holder = holder;
        return this;
    }

    public CertificateOrderBuilder address(Address address) {
        this.address = address;
        return this;
    }

    public CertificateOrderBuilder issuer(User issuer) {
        this.issuer = issuer;
        return this;
    }

    public CertificateOrderBuilder status(OrderStatus status) {
        this.status = status;
        return this;
    }

    public CertificateOrderBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public CertificateOrderBuilder userExternalId(String userExternalId) {
        this.userExternalId = userExternalId;
        return  this;
    }

    public CertificateOrder build() {
        this.order = new CertificateOrder();
        this.order.setId(id);
        this.order.setDevice(device);
        this.order.setTokenSerialNumber(tokenSerialNumber);
        this.order.setRequestDate(requestDate);
        this.order.setRemoteId(remoteId);
        this.order.setAddress(address);
        this.order.setHolder(holder);
        this.order.setStatus(status);
        this.order.setIssuer(issuer);
        this.order.setSsn(ssn);
        this.order.setAcceptedGTC(acceptedGTC);
        this.order.setLrsOrderNumber(lrsOrderNumber);
        this.order.setUserExternalId(userExternalId);
        this.order.setPublish(publish);
        this.order.setUnit(unit);
        this.order.setLastStatusDate(lastStatusDate);
        this.order.setDepartment(department);
        return this.order;
    }

    public static CertificateOrderBuilder newBuilder() {
        return new CertificateOrderBuilder();
    }
}
