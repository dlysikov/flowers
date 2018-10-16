package lu.luxtrust.flowers.fsm.guard;

import lu.luxtrust.flowers.entity.system.RequestorConfiguration;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.repository.RequestorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DiaSigningRequiredGuard extends ReviewRequiredGuard {

    @Autowired
    public DiaSigningRequiredGuard(RequestorRepository RequestorRepository) {
        super(RequestorConfiguration.ValidatedBy.DIA, RequestorRepository, OrderStatus.DIA_SIGNING_REQUIRED);
    }
}
