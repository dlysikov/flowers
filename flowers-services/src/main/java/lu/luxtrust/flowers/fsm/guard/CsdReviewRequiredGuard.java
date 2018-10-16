package lu.luxtrust.flowers.fsm.guard;

import lu.luxtrust.flowers.entity.system.RequestorConfiguration;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.repository.RequestorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CsdReviewRequiredGuard extends ReviewRequiredGuard {

    @Autowired
    public CsdReviewRequiredGuard(RequestorRepository RequestorRepository) {
        super(RequestorConfiguration.ValidatedBy.CSD, RequestorRepository, OrderStatus.CSD_REVIEW_REQUIRED);
    }
}
