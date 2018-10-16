package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.Collection;

final class OrderSpecifications {
    private OrderSpecifications() {
    }

    static Specification<CertificateOrder> sameAs(Collection<CertificateOrder> order) {
        return (root, query, cb) -> {
            Join<Object, Object> holder = root.join("holder", JoinType.INNER);
            root.fetch("holder").fetch("nationality", JoinType.LEFT);
            root.fetch("address", JoinType.LEFT).fetch("country", JoinType.LEFT);
            root.fetch("unit", JoinType.LEFT).fetch("country", JoinType.LEFT);
            root.fetch("unit", JoinType.LEFT).fetch("requestor", JoinType.LEFT).fetch("config", JoinType.LEFT);
            return cb.or(order.stream().map(o -> cb.and(
                    cb.equal(cb.lower(holder.get("firstName")), o.getHolder().getFirstName().toLowerCase()),
                    cb.equal(cb.lower(holder.get("surName")), o.getHolder().getSurName().toLowerCase()),
                    cb.equal(cb.lower(holder.get("notifyEmail")), o.getHolder().getNotifyEmail().toLowerCase())
            )).toArray(size -> new Predicate[size]));
        };
    }
}
