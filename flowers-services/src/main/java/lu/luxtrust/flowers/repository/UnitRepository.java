package lu.luxtrust.flowers.repository;

import lu.luxtrust.flowers.entity.enrollment.CompanyIdentifier;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long>, JpaSpecificationExecutor<Unit> {

    @Query("select count(uu.id) + count(o.id) from Unit u left join u.users uu left join u.orders o where u.id=?1")
    int findRelationsCount(Long unitId);

    Unit findByIdentifier(CompanyIdentifier identifier);

    List<Unit> findAllByIdentifierIn(Set<CompanyIdentifier> identifier);

    boolean existsByIdentifierTypeAndIdentifierValue(CompanyIdentifier.Type type, String value);

    boolean existsByIdentifierTypeAndIdentifierValueAndIdNot(CompanyIdentifier.Type type, String value, Long unitId);

    @EntityGraph(value = "Unit.all", type = EntityGraph.EntityGraphType.FETCH)
    List<Unit> findAll();

    @EntityGraph(value = "Unit.all", type = EntityGraph.EntityGraphType.FETCH)
    List<Unit> findAll(Specification<Unit> spec, Sort sort);

    @EntityGraph(value = "Unit.all", type = EntityGraph.EntityGraphType.FETCH)
    List<Unit> findAll(Specification<Unit> spec);

    @EntityGraph(value = "Unit.all", type = EntityGraph.EntityGraphType.FETCH)
    Page<Unit> findAll(Specification<Unit> spec, Pageable pageable);
}
