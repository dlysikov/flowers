package lu.luxtrust.flowers.entity.enrollment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lu.luxtrust.flowers.entity.common.AbstractEntity;
import lu.luxtrust.flowers.entity.common.ID;

import javax.persistence.*;

@Entity
@Table(name = "certificate")
@SequenceGenerator(sequenceName = "certificate_id", name = "certificate_id")
public class Certificate extends AbstractEntity implements ID<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "certificate_id")
    @Column(name = "id")
    private Long id;

    @Column(name="csn")
    private String csn;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false, updatable = false)
    private Order order;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCsn() {
        return csn;
    }

    public void setCsn(String csn) {
        this.csn = csn;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
