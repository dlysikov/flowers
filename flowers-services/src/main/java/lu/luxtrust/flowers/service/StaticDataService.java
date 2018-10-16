package lu.luxtrust.flowers.service;

import lu.luxtrust.flowers.entity.common.*;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.RequestorConfiguration;

import java.util.Collection;
import java.util.List;

public interface StaticDataService {

    List<Country> getAllCountries();
    List<Nationality> getAllNationalities();
    List<Requestor> getAllServices();
    List<Language> getAllLanguages();
    RequestorConfiguration getRequestorConfiguration(Long id);
    void enrichWithCountries(Collection<CountryHolder> countryHolders);
    void enrichWithNationalities(Collection<NationalityHolder> nationalityHolders);
}
