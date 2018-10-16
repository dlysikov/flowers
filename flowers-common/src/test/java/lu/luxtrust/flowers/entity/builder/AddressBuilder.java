package lu.luxtrust.flowers.entity.builder;

import lu.luxtrust.flowers.entity.enrollment.Address;
import lu.luxtrust.flowers.entity.common.Country;

public class AddressBuilder {
    private String company = "Address test company";
    private String streetAndHouseNo = "Address streetAndHouseNo";
    private String postCode = "post code";
    private String city = "Address city";
    private String name = "name";
    private String addressLine2 = "line 2";
    private Country country;

    private Address address;

    private AddressBuilder() {
    }

    public AddressBuilder addressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
        return this;
    }

    public AddressBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AddressBuilder company(String company) {
        this.company = company;
        return this;
    }

    public AddressBuilder country(Country country) {
        this.country = country;
        return this;
    }

    public AddressBuilder city(String city) {
        this.city = city;
        return this;
    }

    public AddressBuilder postCode(String postCode) {
        this.postCode = postCode;
        return this;
    }

    public AddressBuilder streetAndHouseNo(String streetAndHouseNo) {
        this.streetAndHouseNo = streetAndHouseNo;
        return this;
    }

    public static AddressBuilder newBuilder() {
        return new AddressBuilder();
    }

    public Address build() {
        this.address = new Address();
        this.address.setCompany(this.company);
        this.address.setCity(this.city);
        this.address.setCountry(this.country);
        this.address.setPostCode(this.postCode);
        this.address.setStreetAndHouseNo(this.streetAndHouseNo);
        this.address.setName(this.name);
        this.address.setAddressLine2(this.addressLine2);
        return this.address;
    }
}
