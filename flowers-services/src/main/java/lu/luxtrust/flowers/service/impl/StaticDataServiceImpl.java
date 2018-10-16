package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.entity.common.*;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.RequestorConfiguration;
import lu.luxtrust.flowers.repository.*;
import lu.luxtrust.flowers.service.StaticDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class StaticDataServiceImpl implements StaticDataService {

    private final CountryRepository countryRepository;
    private final NationalityRepository nationalityRepository;
    private final RequestorRepository RequestorRepository;
    private final LanguageRepository languageRepository;

    @Autowired
    public StaticDataServiceImpl(CountryRepository countryRepository,
                                 LanguageRepository languageRepository,
                                 NationalityRepository nationalityRepository,
                                 RequestorRepository RequestorRepository) {
        this.countryRepository = countryRepository;
        this.nationalityRepository = nationalityRepository;
        this.RequestorRepository = RequestorRepository;
        this.languageRepository = languageRepository;
    }

    @Override
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public List<Nationality> getAllNationalities() {
        return nationalityRepository.findAll();
    }

    @Override
    public List<Requestor> getAllServices() {
        return RequestorRepository.findAll();
    }

    @Override
    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    @Override
    public RequestorConfiguration getRequestorConfiguration(Long id) {
        return RequestorRepository.findConfiguration(id);
    }

    @Override
    public void enrichWithNationalities(Collection<NationalityHolder> nationalityHolders) {
        List<String> nationalityCodes = nationalityHolders.stream()
                .filter(c -> c.getNationality() != null && !StringUtils.isEmpty(c.getNationality().getNationalityCode()))
                .map(c -> c.getNationality().getNationalityCode().toLowerCase())
                .collect(Collectors.toList());

        Map<String, Long> code2id = nationalityRepository.findAllByNationalityCodeIn(nationalityCodes)
                .stream()
                .collect(Collectors.toMap(Nationality::getNationalityCode, Nationality::getId));

        for (NationalityHolder holder: nationalityHolders) {
            if (holder.getNationality() != null) {
                Nationality c = holder.getNationality();
                if (code2id.containsKey(c.getNationalityCode())) {
                    c.setId(code2id.get(c.getNationalityCode()));
                } else {
                    holder.setNationality(null);
                }
            }
        }
    }

    @Override
    public void enrichWithCountries(Collection<CountryHolder> countryHolders) {
        List<String> countryCodes = countryHolders.stream()
                .filter(c -> c.getCountry() != null && !StringUtils.isEmpty(c.getCountry().getCountryCode()))
                .map(c -> c.getCountry().getCountryCode().toLowerCase())
                .collect(Collectors.toList());

        Map<String, Long> code2id = countryRepository.findAllByCountryCodeIn(countryCodes)
                .stream()
                .collect(Collectors.toMap(Country::getCountryCode, Country::getId));

        for (CountryHolder holder: countryHolders) {
            if (holder.getCountry() != null) {
                Country c = holder.getCountry();
                if (code2id.containsKey(c.getCountryCode())) {
                    c.setId(code2id.get(c.getCountryCode()));
                } else {
                    holder.setCountry(null);
                }
            }
        }
    }
}
