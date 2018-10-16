package lu.luxtrust.flowers.repository;

import lu.luxtrust.flowers.entity.common.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    Country findByCountryCode(String countryCode);

    List<Country> findAllByCountryCodeIn(Collection<String> countryCodes);
}
