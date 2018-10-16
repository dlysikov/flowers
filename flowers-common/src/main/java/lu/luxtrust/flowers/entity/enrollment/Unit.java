package lu.luxtrust.flowers.entity.enrollment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lu.luxtrust.flowers.entity.common.*;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.validation.annotation.CompanyIdentifierValid;
import lu.luxtrust.flowers.validation.annotation.Email;
import lu.luxtrust.flowers.validation.annotation.PhoneNumber;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "unit", indexes = {
        @Index(name = "unit_identifier_index", columnList = "identifier_type,identifier_value"),
        @Index(name = "unit_identifier_value", columnList = "identifier_value"),
        @Index(name = "unit_common_name", columnList = "common_name")
})
@CompanyIdentifierValid(fullyEmptyAccepted = false)
@SequenceGenerator(sequenceName = "unit_id", name = "unit_id")
@NamedEntityGraph(name = "Unit.all", attributeNodes = {
        @NamedAttributeNode("id"), @NamedAttributeNode("country"), @NamedAttributeNode(value = "requestor", subgraph = "Unit.requestor"),
        @NamedAttributeNode("postCode"), @NamedAttributeNode("city"), @NamedAttributeNode("streetAndHouseNo"),
        @NamedAttributeNode("companyName"), @NamedAttributeNode("commonName"), @NamedAttributeNode("identifier"),
        @NamedAttributeNode("phoneNumber"), @NamedAttributeNode("eMail")
},subgraphs = {
        @NamedSubgraph(name = "Unit.requestor", attributeNodes = {
                @NamedAttributeNode("id"), @NamedAttributeNode("csn"),
                @NamedAttributeNode("name"), @NamedAttributeNode("config")})
})
@XmlAccessorType(XmlAccessType.FIELD)
public class Unit extends AbstractEntity implements Serializable,CompanyIdentifierHolder, CountryHolder, ID<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unit_id")
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "requestor_id")
    private Requestor requestor;

    @Length(max = 15)
    @NotEmpty
    @Column(name = "post_code")
    private String postCode;

    @Length(max = 25)
    @NotEmpty
    @Column(name = "city")
    private String city;

    @Length(max = 35)
    @NotEmpty
    @Column(name = "street_and_house_no")
    private String streetAndHouseNo;

    @NotEmpty
    @Length(max = 50)
    @Column(name = "company_name")
    private String companyName;

    @NotEmpty
    @Length(max = 50)
    @Column(name = "common_name")
    private String commonName;

    @XmlElement
    @Valid
    private CompanyIdentifier identifier;

    @PhoneNumber
    @Column(name = "phone_number")
    private String phoneNumber;

    @Email
    @Length(max = 100)
    @Column(name = "email")
    private String eMail;

    @JsonIgnore
    @OneToMany(mappedBy = "unit")
    private List<User> users;

    @JsonIgnore
    @OneToMany(mappedBy = "unit")
    private List<Order> orders;

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

    public Requestor getRequestor() {
        return requestor;
    }

    public void setRequestor(Requestor requestor) {
        this.requestor = requestor;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public CompanyIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(CompanyIdentifier identifier) {
        this.identifier = identifier;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
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

    public String getStreetAndHouseNo() {
        return streetAndHouseNo;
    }

    public void setStreetAndHouseNo(String streetAndHouseNo) {
        this.streetAndHouseNo = streetAndHouseNo;
    }

    @Override
    public String toString() {
        return "Unit{" +
                "id=" + id +
                ", country=" + country +
                ", requestor=" + requestor +
                ", companyName='" + companyName + '\'' +
                ", postCode='" + postCode + '\'' +
                ", city='" + city + '\'' +
                ", streetAndHouseNo='" + streetAndHouseNo + '\'' +
                ", commonName='" + commonName + '\'' +
                ", identifier=" + identifier +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", eMail='" + eMail + '\'' +
                '}';
    }

    public static boolean equalsWithoutOptional(Unit u1, Unit u2) {
        if (u1 == u2) {
            return Boolean.TRUE;
        }
        if (u1 == null || u2 == null) {
            return Boolean.FALSE;
        }
        return Objects.equals(u1.commonName, u2.commonName) &&
               Objects.equals(u1.companyName, u2.companyName) &&
               Objects.equals(u1.country.getId(), u2.country.getId()) &&
               Objects.equals(u1.requestor.getId(), u2.requestor.getId()) &&
               Objects.equals(u1.identifier, u2.identifier);
    }
}
