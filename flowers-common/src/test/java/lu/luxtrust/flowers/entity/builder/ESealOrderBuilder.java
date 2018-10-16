package lu.luxtrust.flowers.entity.builder;

import lu.luxtrust.flowers.entity.enrollment.ESealOrder;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.enums.OrderStatus;

import java.util.Date;

public class ESealOrderBuilder {
    private Long id;
    private OrderStatus status = OrderStatus.DRAFT;
    private String ssn= "12312312312";
    private boolean publish;
    private Date lastStatusDate;
    private String lrsOrderNumber = "123123123";
    private Unit unit = UnitBuilder.newBuilder().build();
    private boolean acceptedGTC;
    private String organisationalUnit = "org unit";
    private String eSealAdministratorEmail = "sdfa@gmail.com";

    public static ESealOrderBuilder newBuilder() {
        return new ESealOrderBuilder();
    }

    private ESealOrderBuilder() {

    }

    public ESealOrder build() {
        ESealOrder order = new ESealOrder();
        order.setId(id);
        order.setSsn(ssn);
        order.setStatus(status);
        order.setPublish(publish);
        order.setLrsOrderNumber(lrsOrderNumber);
        order.setUnit(unit);
        order.setAcceptedGTC(acceptedGTC);
        order.setOrganisationalUnit(organisationalUnit);
        order.seteSealAdministratorEmail(eSealAdministratorEmail);
        order.setLastStatusDate(lastStatusDate);
        return order;
    }

    public ESealOrderBuilder eSealAdministratorEmail(String eSealAdministratorEmail) {
        this.eSealAdministratorEmail = eSealAdministratorEmail;
        return this;
    }

    public ESealOrderBuilder organisationUnit(String organisationalUnit) {
        this.organisationalUnit = organisationalUnit;
        return this;
    }

    public ESealOrderBuilder acceptedGTC(boolean acceptedGTC) {
        this.acceptedGTC = acceptedGTC;
        return this;
    }

    public ESealOrderBuilder unit(Unit unit) {
        this.unit = unit;
        return this;
    }

    public ESealOrderBuilder lrsOrderNumber(String lrsOrderNumber) {
        this.lrsOrderNumber = lrsOrderNumber;
        return this;
    }

    public ESealOrderBuilder publish(boolean publish) {
        this.publish = publish;
        return this;
    }

    public ESealOrderBuilder ssn(String ssn) {
        this.ssn = ssn;
        return this;
    }

    public ESealOrderBuilder status(OrderStatus status) {
        this.status = status;
        return this;
    }

    public ESealOrderBuilder lastStatusDate(Date lastStatusDate) {
        this.lastStatusDate = lastStatusDate;
        return this;
    }

    public ESealOrderBuilder id(Long id) {
        this.id = id;
        return this;
    }

}
