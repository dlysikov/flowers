package lu.luxtrust.flowers.entity.common;

import javax.persistence.*;

@Table(name = "nationality")
@Entity
@SequenceGenerator(sequenceName = "nationality_id", name = "nationality_id")
public class Nationality implements ID<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nationality_id")
    @Column(name = "id")
    private Long id;
    @Column(name = "nationality_code", unique = true, nullable = false)
    private String nationalityCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNationalityCode() {
        return nationalityCode;
    }

    public void setNationalityCode(String nationalityCode) {
        this.nationalityCode = nationalityCode;
    }
}
