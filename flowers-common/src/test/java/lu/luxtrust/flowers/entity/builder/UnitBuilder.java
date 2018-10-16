package lu.luxtrust.flowers.entity.builder;

import lu.luxtrust.flowers.entity.common.Country;
import lu.luxtrust.flowers.entity.enrollment.CompanyIdentifier;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Requestor;

public class UnitBuilder {

    private Long id;
    private Country country = CountryBuilder.newBuilder().build();
    private Requestor requestor = RequestorBuilder.newBuilder().build();
    private String companyName = "company name";
    private String commonName = "common name";
    private String phoneNumber = "+380503453182";
    private String eMail = "ddd@gmail.com";
    private CompanyIdentifier.Type identifierType = CompanyIdentifier.Type.OTHER;
    private String identifierValue = "423423423423";
    private String postCode = "123123";
    private String city = "Kyiv";
    private String streetAndHouseNo = "Radisheva 10/14";

    private UnitBuilder() {
    }

    public static UnitBuilder newBuilder() {
        return new UnitBuilder();
    }

    public UnitBuilder streetAndHouseNo(String streetAndHouseNo) {
        this.streetAndHouseNo = streetAndHouseNo;
        return this;
    }

    public UnitBuilder city(String city) {
        this.city = city;
        return this;
    }

    public UnitBuilder postCode(String postCode) {
        this.postCode = postCode;
        return this;
    }

    public UnitBuilder identifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
        return this;
    }

    public UnitBuilder identifierType(CompanyIdentifier.Type identifierType) {
        this.identifierType = identifierType;
        return this;
    }

    public UnitBuilder eMail(String eMail) {
        this.eMail = eMail;
        return this;
    }

    public UnitBuilder phoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public UnitBuilder commonName(String commonName) {
        this.commonName = commonName;
        return this;
    }

    public UnitBuilder companyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public UnitBuilder requestor(Requestor requestor) {
        this.requestor = requestor;
        return this;
    }

    public UnitBuilder country(Country country) {
        this.country = country;
        return this;
    }

    public UnitBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public Unit build() {
        Unit unit = new Unit();
        unit.setId(id);
        unit.setCountry(country);
        unit.setRequestor(requestor);
        unit.setPostCode(postCode);
        unit.setCity(city);
        unit.setStreetAndHouseNo(streetAndHouseNo);
        unit.setCompanyName(companyName);
        unit.setCommonName(commonName);
        unit.setPhoneNumber(phoneNumber);
        unit.seteMail(eMail);
        CompanyIdentifier identifier = new CompanyIdentifier();
        identifier.setValue(identifierValue);
        identifier.setType(identifierType);
        unit.setIdentifier(identifier);
        return unit;
    }

    public static Unit copy(Unit unit) {
        Unit copy = new Unit();
        copy.setCompanyName(unit.getCompanyName());
        copy.setId(unit.getId());
        copy.setCountry(unit.getCountry());
        copy.setRequestor(unit.getRequestor());
        copy.setPostCode(unit.getPostCode());
        copy.setCity(unit.getCity());
        copy.setStreetAndHouseNo(unit.getStreetAndHouseNo());
        copy.setCommonName(unit.getCommonName());
        copy.setPhoneNumber(unit.getPhoneNumber());
        copy.seteMail(unit.geteMail());
        copy.setIdentifier(unit.getIdentifier());
        return copy;
    }
}
