package lu.luxtrust.flowers.entity.common;

import javax.persistence.*;

@Table(name = "country_vat_config")
@Entity
@SequenceGenerator(sequenceName = "country_vat_config_id", name = "country_vat_config_id")
public class CountryVatConfig implements ID<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "country_vat_config_id")
    @Column(name = "id")
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "country_id")
    private Country country;
    @Column(name = "vat_pattern")
    private String vatPattern;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getVatPattern() {
        return vatPattern;
    }

    public void setVatPattern(String vatPattern) {
        this.vatPattern = vatPattern;
    }
}
