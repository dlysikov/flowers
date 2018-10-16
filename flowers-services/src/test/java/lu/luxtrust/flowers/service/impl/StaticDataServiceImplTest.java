package lu.luxtrust.flowers.service.impl;

import com.google.common.collect.Lists;
import lu.luxtrust.flowers.AbstractWithSpringContextTest;
import lu.luxtrust.flowers.entity.builder.CountryBuilder;
import lu.luxtrust.flowers.entity.builder.NationalityBuilder;
import lu.luxtrust.flowers.entity.common.Country;
import lu.luxtrust.flowers.entity.common.Nationality;
import lu.luxtrust.flowers.entity.enrollment.Holder;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.builder.RequestorBuilder;
import lu.luxtrust.flowers.service.StaticDataService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class StaticDataServiceImplTest extends AbstractWithSpringContextTest {

    private static final String COUNTRY_CODE = "ua";
    private static final String NATIONALITY_CODE = "be";
    private static final String SERVICE_NAME = "DG Sante";

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StaticDataService target;
    private Country country;
    private Nationality nationality;

    @Before
    public void init() {
        country = new Country();
        country.setCountryCode(COUNTRY_CODE);

        nationality = new Nationality();
        nationality.setNationalityCode(NATIONALITY_CODE);

        Requestor requestor = RequestorBuilder.newBuilder().build();
        requestor.setName(SERVICE_NAME);

        entityManager.persist(country);
        entityManager.persist(nationality);
        entityManager.persist(requestor);
    }

    @Test
    public void getAllCountries() {
        List<Country> allCountries = target.getAllCountries();

        assertThat(allCountries).hasSize(1);
        assertThat(allCountries.get(0).getCountryCode()).isEqualTo(COUNTRY_CODE);
    }

    @Test
    public void getAllNationalities() {
        List<Nationality> allNationalities = target.getAllNationalities();

        assertThat(allNationalities).hasSize(1);
        assertThat(allNationalities.get(0).getNationalityCode()).isEqualTo(NATIONALITY_CODE);
    }

    @Test
    public void getAllServices() {
        List<Requestor> allServices = target.getAllServices();

        assertThat(allServices).hasSize(1);
        assertThat(allServices.get(0).getName()).isEqualTo(SERVICE_NAME);
    }

    @Test
    public void enrichWithCountries() {
        Unit unit = new Unit();
        unit.setCountry(CountryBuilder.newBuilder().id(null).countryCode(COUNTRY_CODE).build());

        target.enrichWithCountries(Lists.newArrayList(unit));
        assertThat(unit.getCountry().getId()).isEqualTo(country.getId());
    }

    @Test
    public void enrichWithNationalities() {
        Holder holder = new Holder();
        holder.setNationality(NationalityBuilder.newBuilder().id(null).nationalityCode(NATIONALITY_CODE).build());

        target.enrichWithNationalities(Lists.newArrayList(holder));
        assertThat(holder.getNationality().getId()).isEqualTo(nationality.getId());
    }
}