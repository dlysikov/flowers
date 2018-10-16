package lu.luxtrust.flowers.repository;

import lu.luxtrust.flowers.entity.common.CountryVatConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryVatConfigRepository extends JpaRepository<CountryVatConfig, Long> {

}
