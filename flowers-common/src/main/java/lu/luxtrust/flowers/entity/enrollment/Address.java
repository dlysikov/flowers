package lu.luxtrust.flowers.entity.enrollment;

import lu.luxtrust.flowers.entity.common.AbstractEntity;
import lu.luxtrust.flowers.entity.common.Country;
import lu.luxtrust.flowers.entity.common.CountryHolder;
import lu.luxtrust.flowers.entity.common.ID;
import lu.luxtrust.flowers.xml.adapter.CountryAdapter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Objects;

import static lu.luxtrust.flowers.validation.ValidationGroups.*;

@Entity
@Table(name = "delivery_address")
@XmlAccessorType(XmlAccessType.FIELD)
@SequenceGenerator(sequenceName = "delivery_address_id", name = "delivery_address_id")
public class Address extends AbstractEntity implements Serializable, CountryHolder, ID<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delivery_address_id")
    @Column(name = "id")
    private Long id;

    @XmlAttribute(name = "nameOfAddressee")
    @Length(max = 100)
    @Column(name = "name_of_addressee")
    private String name;

    @XmlAttribute
    @Length(max = 35)
    @Column(name = "company")
    private String company;

    @XmlAttribute
    @Length(max = 35)
    @NotEmpty(groups = OrderUserDraft.class)
    @Column(name = "street_and_house_no")
    private String streetAndHouseNo;

    @XmlAttribute
    @Length(max = 35)
    @Column(name = "address_line_2")
    private String addressLine2;

    @XmlAttribute
    @Length(max = 15)
    @NotEmpty(groups = OrderUserDraft.class)
    @Column(name = "post_code")
    private String postCode;

    @XmlAttribute
    @Length(max = 25)
    @NotEmpty(groups = OrderUserDraft.class)
    @Column(name = "city")
    private String city;

    @XmlJavaTypeAdapter(CountryAdapter.class)
    @XmlAttribute
    @NotNull(groups = OrderUserDraft.class)
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getStreetAndHouseNo() {
        return streetAndHouseNo;
    }

    public void setStreetAndHouseNo(String streetAndHouseNo) {
        this.streetAndHouseNo = streetAndHouseNo;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(name, address.name) &&
                Objects.equals(company, address.company) &&
                Objects.equals(streetAndHouseNo, address.streetAndHouseNo) &&
                Objects.equals(addressLine2, address.addressLine2) &&
                Objects.equals(postCode, address.postCode) &&
                Objects.equals(city, address.city) &&
                Objects.equals(country, address.country);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, company, streetAndHouseNo, addressLine2, postCode, city, country);
    }
}
