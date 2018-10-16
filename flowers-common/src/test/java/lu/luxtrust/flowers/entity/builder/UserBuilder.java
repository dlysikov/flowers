package lu.luxtrust.flowers.entity.builder;

import lu.luxtrust.flowers.entity.common.Language;
import lu.luxtrust.flowers.entity.common.Nationality;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.CertificateType;

import java.util.Date;
import java.util.Set;

public class UserBuilder {

    private Long id;
    private String firstName = "John";
    private String surname = "Smith";
    private String ssn = "1231231231";
    private CertificateType certificateType = CertificateType.PROFESSIONAL_PERSON;
    private Set<Role> roles;
    private String email = "john.smith@gmail.com";
    private Language defaultLanguage;
    private Unit unit;
    private Date birthDate = new Date(100500);
    private Nationality nationality;
    private String phoneNumber = "+380931234567";

    private UserBuilder() {
    }

    public UserBuilder defaultLanguage(Language defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder roles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }

    public UserBuilder certificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
        return this;
    }

    public UserBuilder ssn(String ssn) {
        this.ssn = ssn;
        return this;
    }

    public UserBuilder surname(String surname) {
        this.surname = surname;
        return this;
    }

    public UserBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public UserBuilder unit(Unit unit) {
        this.unit = unit;
        return this;
    }

    public UserBuilder birthDate(Date date) {
        this.birthDate = date;
        return this;
    }

    public UserBuilder nationality(Nationality nationality) {
        this.nationality = nationality;
        return this;
    }

    public UserBuilder phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public static UserBuilder newBuilder() {
        return new UserBuilder();
    }

    public User build() {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setSurName(surname);
        user.setRoles(roles);
        user.setSsn(ssn);
        user.setDefaultLanguage(defaultLanguage);
        user.setCertificateType(certificateType);
        user.setUnit(unit);
        user.setBirthDate(birthDate);
        user.setNationality(nationality);
        user.setPhoneNumber(phoneNumber);
        return user;
    }
}
