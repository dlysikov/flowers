package lu.luxtrust.flowers.service.impl;

import com.google.common.collect.Lists;
import lu.luxtrust.flowers.model.FieldFilter;
import lu.luxtrust.flowers.service.PredicateProducer;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.*;

final class SearchSpecifications {

    private interface FilterPredicateProducer {
        Predicate produce(Root root, CriteriaBuilder cb, FieldFilter fieldFilter, Object value);
    }

    private static final List<String> JOIN_TYPES = Lists.newArrayList("MANY_TO_MANY", "ONE_TO_MANY");
    private static final Map<FieldFilter.ValueType, FilterPredicateProducer> FILTER_TYPE_2_HANDLER = new HashMap<>();

    static {
        FILTER_TYPE_2_HANDLER.put(FieldFilter.ValueType.STRING_LIKE, (root, cb, field, value) -> cb.like(cb.lower(getPath(field, root)), "%" + value.toString().toLowerCase() + "%"));
        FILTER_TYPE_2_HANDLER.put(FieldFilter.ValueType.STRING_EQ, (root, cb, field, value) -> cb.equal(cb.upper(getPath(field, root).as(String.class)), value.toString().toUpperCase()));
        FILTER_TYPE_2_HANDLER.put(FieldFilter.ValueType.INTEGER, (root, cb, field, value) -> cb.equal(getPath(field, root), Long.parseLong(value.toString())));
        FILTER_TYPE_2_HANDLER.put(FieldFilter.ValueType.FLOAT, (root, cb, field, value) -> cb.equal(getPath(field, root), Double.parseDouble(value.toString())));
    }

    private SearchSpecifications() {

    }

    static <T> Specification<T> filterQuery(List<FieldFilter> filter, PredicateProducer... additionalPredicates) {
        return filterQuery(filter, false, additionalPredicates);
    }

    static <T> Specification<T> filterQuery(List<FieldFilter> filter, boolean distinct, PredicateProducer... additionalPredicates) {
        return (root, query, cb) -> {
            query.distinct(distinct);
            List<Predicate> predicates = new ArrayList<>();
            for (FieldFilter fieldFilter: filter) {
                if (!isEmptyList(fieldFilter.getValue()) && FILTER_TYPE_2_HANDLER.containsKey(fieldFilter.getType())) {
                    if (fieldFilter.getValue().size() > 1) {
                        Predicate[] partPredicate = new Predicate[fieldFilter.getValue().size()];
                        for (int i = 0; i < partPredicate.length; i++) {
                            partPredicate[i] = FILTER_TYPE_2_HANDLER.get(fieldFilter.getType()).produce(root, cb, fieldFilter, fieldFilter.getValue().get(i));
                        }
                        predicates.add(cb.or(partPredicate));
                    } else {
                        predicates.add(FILTER_TYPE_2_HANDLER.get(fieldFilter.getType()).produce(root, cb, fieldFilter, fieldFilter.getValue().get(0)));
                    }
                }
            }
            for (PredicateProducer pp: additionalPredicates) {
                pp.produce(cb, root).ifPresent(predicates::add);
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    private static boolean isEmptyList(List<?> list) {
        return list == null || list.isEmpty();
    }

    private static Expression<String> getPath(FieldFilter ff, Root<?> root) {
        Queue<String> pathParts = new LinkedList<>(Arrays.asList(ff.getField().split("\\.")));
        Path<String> path;
        if (JOIN_TYPES.contains(root.getModel().getAttribute(pathParts.peek()).getPersistentAttributeType().toString())) {
            Join<Object, Object> join = root.join(pathParts.poll());
            path = join.get(pathParts.poll());
        } else {
            path = root.get(pathParts.poll());
        }
        for (String pathPart : pathParts) {
            path = path.get(pathPart);
        }
        return path;
    }
}
