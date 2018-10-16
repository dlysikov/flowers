package lu.luxtrust.flowers.repository;

import lu.luxtrust.flowers.entity.common.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface NationalityRepository extends JpaRepository<Nationality, Long> {

    Nationality findByNationalityCode(String nationalityCode);

    List<Nationality> findAllByNationalityCodeIn(Collection<String> nationalityCodes);
}
