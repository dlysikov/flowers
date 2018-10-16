package lu.luxtrust.flowers.entity.builder;

import lu.luxtrust.flowers.entity.common.Country;

public class CountryBuilder {

    private Long id;
    private String countryCode = "ua";

    private Country country;

    private CountryBuilder() {

    }

    public CountryBuilder countryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public CountryBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public static CountryBuilder newBuilder() {
        return new CountryBuilder();
    }

    public Country build() {
        this.country = new Country();
        this.country.setCountryCode(countryCode);
        this.country.setId(id);
        return country;
    }
}
