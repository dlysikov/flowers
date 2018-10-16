package lu.luxtrust.flowers.entity.builder;

import lu.luxtrust.flowers.entity.enrollment.Document;
import lu.luxtrust.flowers.entity.enrollment.Holder;
import lu.luxtrust.flowers.entity.common.Nationality;
import lu.luxtrust.flowers.enums.CertificateLevel;
import lu.luxtrust.flowers.enums.CertificateType;
import lu.luxtrust.flowers.enums.RoleType;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class HolderBuilder {
    private String firstName = "holder first name";
    private String surName = "holder surname";
    private String eMail = "ddd@gmail.com";
    private String secondEMail = "1ddd@gmail.com";
    private CertificateType certificateType = CertificateType.PROFESSIONAL_PERSON;
    private RoleType roleType = RoleType.DIA;
    private Date birthDate = new Date(LocalDate.of(1998, 10, 10).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    private String activationCode = "12311";
    private String phoneNumber = "+380931234567";
    private String notifyEMail = "ddd22@gmail.com";
    private CertificateLevel certificateLevel = CertificateLevel.LCP;
    private Nationality nationality;
    private Boolean waitDocuments = Boolean.FALSE;
    private List<Document> documentList;

    private Holder holder;

    private HolderBuilder() {
    }

    public HolderBuilder certificateLevel(CertificateLevel certificateLevel) {
        this.certificateLevel = certificateLevel;
        return this;
    }

    public HolderBuilder notifyEmail(String notifyEmail) {
        this.notifyEMail = notifyEmail;
        return this;
    }

    public HolderBuilder phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public HolderBuilder activationCode(String activationCode) {
        this.activationCode = activationCode;
        return this;
    }

    public HolderBuilder birthDate(Date birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public HolderBuilder roleType(RoleType roleType) {
        this.roleType = roleType;
        return this;
    }

    public HolderBuilder certificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
        return this;
    }

    public HolderBuilder secondEmail(String secondEMail) {
        this.secondEMail = secondEMail;
        return this;
    }

    public HolderBuilder email(String eMail) {
        this.eMail = eMail;
        return this;
    }

    public HolderBuilder nationality(Nationality nationality) {
        this.nationality = nationality;
        return this;
    }

    public HolderBuilder surName(String surName) {
        this.surName = surName;
        return this;
    }

    public HolderBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public HolderBuilder waitDocuments(Boolean waitDocuments){
        this.waitDocuments = waitDocuments;
        return this;
    }

    public HolderBuilder documents(List<Document> documentList ){
        this.documentList = documentList;
        return this;
    }

    public static HolderBuilder newBuilder() {
        return new HolderBuilder();
    }

    public Holder build() {
        this.holder = new Holder();
        this.holder.setFirstName(this.firstName);
        this.holder.setSurName(this.surName);
        this.holder.setNationality(this.nationality);
        this.holder.seteMail(this.eMail);
        this.holder.seteMailSecond(this.secondEMail);
        this.holder.setCertificateType(this.certificateType);
        this.holder.setRoleType(this.roleType);
        this.holder.setBirthDate(this.birthDate);
        this.holder.setActivationCode(this.activationCode);
        this.holder.setPhoneNumber(this.phoneNumber);
        this.holder.setNotifyEmail(this.notifyEMail);
        this.holder.setCertificateLevel(this.certificateLevel);
        this.holder.setWaitDocuments(this.waitDocuments);
        this.holder.setDocuments(documentList);
        return this.holder;
    }
}
