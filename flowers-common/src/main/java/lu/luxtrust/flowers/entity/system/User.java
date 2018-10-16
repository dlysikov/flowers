package lu.luxtrust.flowers.entity.system;

import lu.luxtrust.flowers.entity.common.*;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.enums.CertificateType;
import lu.luxtrust.flowers.validation.annotation.Adult;
import lu.luxtrust.flowers.validation.annotation.Email;
import lu.luxtrust.flowers.validation.annotation.PhoneNumber;
import lu.luxtrust.flowers.validation.annotation.UserMandatoryConditions;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@UserMandatoryConditions
@Entity
@Table(name = "user_details")
@SequenceGenerator(sequenceName = "user_details_id", name = "user_details_id")
public class User extends AbstractEntity implements Serializable, ID<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_details_id")
    @Column(name = "id")
    private Long id;

    @NotEmpty
    @Length(max = 50)
    @Column(unique = true, name = "ssn", nullable = false)
    private String ssn;

    @NotEmpty
    @Length(max = 60)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotEmpty
    @Length(max = 60)
    @Column(name = "surname", nullable = false)
    private String surName;

    @Email
    @NotEmpty
    @Length(max = 100)
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_type")
    private CertificateType certificateType;

    @ManyToMany
    @JoinTable(name = "ref_user_role", joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private Set<Role> roles;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language defaultLanguage;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @Adult
    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    @ManyToOne
    @JoinColumn(name = "nationality_id")
    private Nationality nationality;

    @PhoneNumber
    @Column(name = "phone_number")
    private String phoneNumber;

    public User() {
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public User(Long id) {
        this.id = id;
    }

    public Language getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(Language defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public CertificateType getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Nationality getNationality() {
        return nationality;
    }

    public void setNationality(Nationality nationality) {
        this.nationality = nationality;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
