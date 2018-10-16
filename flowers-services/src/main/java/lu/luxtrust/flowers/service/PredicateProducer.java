package lu.luxtrust.flowers.service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Optional;

public interface PredicateProducer {
    Optional<Predicate> produce(CriteriaBuilder cb, Root root);
}
