package lu.luxtrust.flowers.validation;

import lu.luxtrust.flowers.entity.common.Country;

import java.util.regex.Pattern;

public interface CountryVatDictionary {

    Pattern getVatPattern(String countryCode);

    boolean isRcslValid(Country country);
}
