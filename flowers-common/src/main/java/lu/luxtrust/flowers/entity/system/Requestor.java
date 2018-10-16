package lu.luxtrust.flowers.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lu.luxtrust.flowers.entity.common.AbstractEntity;
import lu.luxtrust.flowers.entity.common.ID;

import javax.persistence.*;
import java.util.Objects;

@Table(name = "requestor")
@Entity
@SequenceGenerator(sequenceName = "requestor_id", name = "requestor_id")
public class Requestor extends AbstractEntity implements ID<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "requestor_id")
    @Column(name = "id")
    private Long id;

    @JsonIgnore
    @Column(name = "csn", unique = true)
    private String csn;

    @Column(name = "service_name", unique = true, nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "config_id", nullable = false)
    private RequestorConfiguration config;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RequestorConfiguration getConfig() {
        return config;
    }

    public void setConfig(RequestorConfiguration config) {
        this.config = config;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Requestor that = (Requestor) o;
        return Objects.equals(csn, that.csn) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(csn, name);
    }
}
