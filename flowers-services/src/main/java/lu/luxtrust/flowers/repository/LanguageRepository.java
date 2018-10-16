package lu.luxtrust.flowers.repository;

import lu.luxtrust.flowers.entity.common.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
}
