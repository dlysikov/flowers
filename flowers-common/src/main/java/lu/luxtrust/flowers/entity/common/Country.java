package lu.luxtrust.flowers.entity.common;

import javax.persistence.*;

@Table(name = "country")
@Entity
@SequenceGenerator(sequenceName = "country_id", name = "country_id")
public class Country implements ID<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "country_id")
    @Column(name = "id")
    private Long id;

    @Column(name = "country_code", nullable = false, unique = true)
    private String countryCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
