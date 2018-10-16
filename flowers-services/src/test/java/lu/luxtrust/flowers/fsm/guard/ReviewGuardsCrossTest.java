package lu.luxtrust.flowers.fsm.guard;

import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.Holder;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.Requestor;
import lu.luxtrust.flowers.entity.system.RequestorConfiguration;
import lu.luxtrust.flowers.enums.CertificateLevel;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.fsm.FsmExtendedStateVariables;
import lu.luxtrust.flowers.fsm.OrderEvent;
import lu.luxtrust.flowers.repository.RequestorRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReviewGuardsCrossTest {

    @Mock private StateContext<OrderStatus, OrderEvent> stateContext;
    @Mock private ExtendedState extendedState;
    @Mock private CertificateOrder order;
    @Mock private Holder holder;
    @Mock private RequestorRepository RequestorRepository;
    @Mock private RequestorConfiguration config;
    @Mock private Unit unit;

    private CsdReviewRequiredGuard csdReviewRequiredGuard;
    private DiaSigningRequiredGuard diaSigningRequiredGuard;
    private Face2FaceRequiredGuard face2FaceRequiredGuard;
    private RemoteIdRequiredGuard remoteIdRequiredGuard;

    private Requestor requestor = new Requestor();

    @Before
    public void init() {
        requestor.setId(42L);
        requestor.setId(43L);
        when(order.getHolder()).thenReturn(holder);
        when(order.getUnit()).thenReturn(unit);
        when(unit.getRequestor()).thenReturn(requestor);
        when(stateContext.getExtendedState()).thenReturn(extendedState);
        when(extendedState.get(FsmExtendedStateVariables.ORDER, CertificateOrder.class)).thenReturn(order);
        when(RequestorRepository.findConfiguration(requestor.getId())).thenReturn(config);

        diaSigningRequiredGuard = new DiaSigningRequiredGuard(RequestorRepository);
        csdReviewRequiredGuard = new CsdReviewRequiredGuard(RequestorRepository);
        face2FaceRequiredGuard = new Face2FaceRequiredGuard();
        remoteIdRequiredGuard = new RemoteIdRequiredGuard();
    }

    @Test
    public void csdReviewRequiredGuardOnly() {
        when(holder.getCertificateLevel()).thenReturn(CertificateLevel.LCP);
        when(config.getValidatedBy()).thenReturn(RequestorConfiguration.ValidatedBy.CSD);
        assertThat(csdReviewRequiredGuard.evaluate(stateContext)).isTrue();
        assertThat(diaSigningRequiredGuard.evaluate(stateContext)).isFalse();
        assertThat(face2FaceRequiredGuard.evaluate(stateContext)).isFalse();
        assertThat(remoteIdRequiredGuard.evaluate(stateContext)).isFalse();
    }

    @Test
    public void diaSigningRequiredGuardOnly() {
        when(holder.getCertificateLevel()).thenReturn(CertificateLevel.LCP);
        when(config.getValidatedBy()).thenReturn(RequestorConfiguration.ValidatedBy.DIA);
        assertThat(csdReviewRequiredGuard.evaluate(stateContext)).isFalse();
        assertThat(diaSigningRequiredGuard.evaluate(stateContext)).isTrue();
        assertThat(face2FaceRequiredGuard.evaluate(stateContext)).isFalse();
        assertThat(remoteIdRequiredGuard.evaluate(stateContext)).isFalse();
    }

    @Test
    public void face2FaceRequiredGuardOnly() {
        when(holder.getCertificateLevel()).thenReturn(CertificateLevel.NCP);
        when(order.getRemoteId()).thenReturn(Boolean.FALSE);
        assertThat(csdReviewRequiredGuard.evaluate(stateContext)).isFalse();
        assertThat(diaSigningRequiredGuard.evaluate(stateContext)).isFalse();
        assertThat(face2FaceRequiredGuard.evaluate(stateContext)).isTrue();
        assertThat(remoteIdRequiredGuard.evaluate(stateContext)).isFalse();
    }

    @Test
    public void reviewRequiredGuardOnly() {
        when(holder.getCertificateLevel()).thenReturn(CertificateLevel.NCP);
        when(order.getRemoteId()).thenReturn(Boolean.TRUE);
        assertThat(csdReviewRequiredGuard.evaluate(stateContext)).isFalse();
        assertThat(diaSigningRequiredGuard.evaluate(stateContext)).isFalse();
        assertThat(face2FaceRequiredGuard.evaluate(stateContext)).isFalse();
        assertThat(remoteIdRequiredGuard.evaluate(stateContext)).isTrue();
    }

}
