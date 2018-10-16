package lu.luxtrust.flowers.service.util;

import lu.luxtrust.flowers.entity.common.Country;
import lu.luxtrust.flowers.entity.common.CountryVatConfig;
import lu.luxtrust.flowers.repository.CountryVatConfigRepository;
import lu.luxtrust.flowers.validation.CountryVatDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class CountryVatConfigHolder implements CountryVatDictionary {

    private final Map<String, Pattern> vatPatterns;

    @Autowired
    public CountryVatConfigHolder(CountryVatConfigRepository repository) {
        List<CountryVatConfig> all = repository.findAll();
        Map<String, Pattern> vatPatterns = new HashMap<>();
        for (CountryVatConfig countryVatConfig : all) {
            vatPatterns.put(countryVatConfig.getCountry().getCountryCode(), Pattern.compile(countryVatConfig.getVatPattern()));
        }
        this.vatPatterns = Collections.unmodifiableMap(vatPatterns);
    }

    public Pattern getVatPattern(String countryCode) {
        return vatPatterns.get(countryCode);
    }

    public boolean isRcslValid(Country country) {
        return vatPatterns.containsKey(country.getCountryCode());
    }

}
