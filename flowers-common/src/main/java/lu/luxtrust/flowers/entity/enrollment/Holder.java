package lu.luxtrust.flowers.entity.enrollment;

import lu.luxtrust.flowers.entity.common.*;
import lu.luxtrust.flowers.enums.CertificateLevel;
import lu.luxtrust.flowers.enums.CertificateType;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.validation.annotation.Adult;
import lu.luxtrust.flowers.xml.adapter.NationalityAdapter;
import lu.luxtrust.flowers.validation.annotation.Email;
import lu.luxtrust.flowers.validation.annotation.PhoneNumber;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static lu.luxtrust.flowers.validation.ValidationGroups.*;

@Entity
@Table(name = "holder",
        indexes = {
                @Index(name = "duplicate_holder_index", columnList = "first_name, surname, notify_email"),
                @Index(name = "first_name_index", columnList = "first_name"),
                @Index(name = "surname_index", columnList = "surname"),
                @Index(name = "phone_index", columnList = "phone_number"),
                @Index(name = "notify_email_index", columnList = "notify_email")
        }
)
@XmlAccessorType(XmlAccessType.FIELD)
@SequenceGenerator(sequenceName = "holder_id", name = "holder_id")
public class Holder extends AbstractEntity implements Serializable, NationalityHolder, ID<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "holder_id")
    @Column(name = "id")
    private Long id;

    @XmlAttribute
    @NotEmpty(groups = {OrderDraft.class, OrderUserDraftAPI.class})
    @Length(max = 60)
    @Column(name = "first_name")
    private String firstName;

    @XmlAttribute
    @NotEmpty(groups = {OrderDraft.class, OrderUserDraftAPI.class})
    @Length(max = 60)
    @Column(name = "surname")
    private String surName;

    @XmlAttribute
    @XmlJavaTypeAdapter(NationalityAdapter.class)
    @NotNull(groups = OrderUserDraft.class)
    @ManyToOne
    @JoinColumn(name = "nationality_id")
    private Nationality nationality;

    @XmlAttribute
    @Email
    @Length(max = 100)
    @Column(name = "email")
    private String eMail;

    @XmlAttribute
    @Email
    @Length(max = 100)
    @Column(name = "second_email")
    private String eMailSecond;

    @XmlAttribute
    @NotNull(groups = {OrderDraft.class, OrderUserDraftAPI.class})
    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_type")
    private CertificateType certificateType;

    @NotNull(groups = {OrderDraft.class, OrderUserDraftAPI.class})
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type")
    private RoleType roleType;

    @XmlAttribute
    @NotNull(groups = OrderDraft.class)
    @Column(name = "birth_date")
    @Temporal(TemporalType.DATE)
    @Adult
    private Date birthDate;

    @XmlAttribute
    @NotEmpty(groups = OrderUserDraft.class)
    @Length(min = 5, max = 5)
    @Column(name = "activation_code")
    private String activationCode;

    @XmlAttribute
    @NotEmpty(groups = {OrderDraft.class, OrderUserDraftAPI.class})
    @PhoneNumber
    @Column(name = "phone_number")
    private String phoneNumber;

    @XmlAttribute
    @NotEmpty(groups = {OrderDraft.class, OrderUserDraftAPI.class})
    @Email
    @Length(max = 100)
    @Column(name = "notify_email")
    private String notifyEmail;

    @XmlAttribute
    @NotNull(groups = OrderDraft.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_level")
    private CertificateLevel certificateLevel;

    @XmlTransient
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "holder")
    private List<Document> documents;

    @Transient
    @XmlTransient
    private Boolean waitDocuments = Boolean.FALSE;

    public Boolean getWaitDocuments() {
        return waitDocuments;
    }

    public void setWaitDocuments(Boolean waitDocuments) {
        this.waitDocuments = waitDocuments;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Nationality getNationality() {
        return nationality;
    }

    public void setNationality(Nationality nationality) {
        this.nationality = nationality;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String geteMailSecond() {
        return eMailSecond;
    }

    public void seteMailSecond(String eMailSecond) {
        this.eMailSecond = eMailSecond;
    }

    public CertificateType getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNotifyEmail() {
        return notifyEmail;
    }

    public void setNotifyEmail(String notifyEmail) {
        this.notifyEmail = notifyEmail;
    }

    public CertificateLevel getCertificateLevel() {
        return certificateLevel;
    }

    public void setCertificateLevel(CertificateLevel certificateLevel) {
        this.certificateLevel = certificateLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Holder holder = (Holder) o;
        return Objects.equals(firstName, holder.firstName) &&
                Objects.equals(surName, holder.surName) &&
                Objects.equals(nationality, holder.nationality) &&
                Objects.equals(eMail, holder.eMail) &&
                Objects.equals(eMailSecond, holder.eMailSecond) &&
                certificateType == holder.certificateType &&
                roleType == holder.roleType &&
                Objects.equals(birthDate, holder.birthDate) &&
                Objects.equals(activationCode, holder.activationCode) &&
                Objects.equals(phoneNumber, holder.phoneNumber) &&
                Objects.equals(notifyEmail, holder.notifyEmail) &&
                certificateLevel == holder.certificateLevel;
    }

    @Override
    public int hashCode() {

        return Objects.hash(firstName, surName, nationality, eMail, eMailSecond, certificateType, roleType, birthDate, activationCode, phoneNumber, notifyEmail, certificateLevel);
    }
}
