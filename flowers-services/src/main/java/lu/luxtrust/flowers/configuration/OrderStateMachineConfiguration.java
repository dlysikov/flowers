package lu.luxtrust.flowers.configuration;

import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.fsm.ContextEntity;
import lu.luxtrust.flowers.fsm.OrderEvent;
import lu.luxtrust.flowers.fsm.OrderStateMachinePersist;
import lu.luxtrust.flowers.fsm.action.*;
import lu.luxtrust.flowers.fsm.guard.*;
import lu.luxtrust.flowers.properties.NotificationProperties;
import lu.luxtrust.flowers.service.NotificationService;
import lu.luxtrust.flowers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory(contextEvents = false)
public class OrderStateMachineConfiguration extends EnumStateMachineConfigurerAdapter<OrderStatus, OrderEvent> {

    private final SendOrderToLRSAction sendOrderToLRSAction;
    private final NotifyUserAction notifyUserAction;
    private final SendMobileValidationCodeAction sendMobileValidationCodeAction;
    private final Face2FaceRequiredGuard face2FaceRequiredGuard;
    private final RemoteIdRequiredGuard remoteIdRequiredGuard;
    private final UserValidatesOrderAction userValidatesOrderAction;
    private final ShortEnrollmentFlowGuard shortEnrollmentFlowGuard;
    private final CsdReviewRequiredGuard csdReviewRequiredGuard;
    private final DiaSigningRequiredGuard diaSigningRequiredGuard;
    private final NotificationProperties notificationProperties;
    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public OrderStateMachineConfiguration(NotifyUserAction notifyUserAction,
                                          UserValidatesOrderAction userValidatesOrderAction,
                                          CsdReviewRequiredGuard csdReviewRequiredGuard,
                                          Face2FaceRequiredGuard face2FaceRequiredGuard,
                                          ShortEnrollmentFlowGuard shortEnrollmentFlowGuard,
                                          RemoteIdRequiredGuard remoteIdRequiredGuard,
                                          SendMobileValidationCodeAction sendMobileValidationCodeAction,
                                          SendOrderToLRSAction sendOrderToLRSAction,
                                          DiaSigningRequiredGuard diaSigningRequiredGuard,
                                          NotificationProperties notificationProperties,
                                          NotificationService notificationService,
                                          UserService userService) {
        this.notifyUserAction = notifyUserAction;
        this.face2FaceRequiredGuard = face2FaceRequiredGuard;
        this.shortEnrollmentFlowGuard = shortEnrollmentFlowGuard;
        this.remoteIdRequiredGuard = remoteIdRequiredGuard;
        this.userValidatesOrderAction = userValidatesOrderAction;
        this.csdReviewRequiredGuard = csdReviewRequiredGuard;
        this.sendOrderToLRSAction = sendOrderToLRSAction;
        this.diaSigningRequiredGuard = diaSigningRequiredGuard;
        this.sendMobileValidationCodeAction = sendMobileValidationCodeAction;
        this.notificationProperties = notificationProperties;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderEvent> states) throws Exception {
        states.withStates()
                .initial(OrderStatus.DRAFT)
                .states(EnumSet.allOf(OrderStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderEvent> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(OrderStatus.DRAFT)
                    .target(OrderStatus.USER_DRAFT)
                    .event(OrderEvent.MOVE_TO_USER_DRAFT)
                    .action(notifyUserAction)
                    .action(sendMobileValidationCodeAction)
                .and()
                .withExternal()
                    .source(OrderStatus.USER_DRAFT)
                    .target(OrderStatus.FACE_2_FACE_REQUIRED)
                    .event(OrderEvent.USER_VALIDATES)
                    .guard(face2FaceRequiredGuard)
                    .action(userValidatesOrderAction)
                    .action(new NotifyCsdReviewAction(notificationService, notificationProperties.getIssuerF2F(), notificationProperties.getAdminNoCsds(), userService))
                .and()
                .withExternal()
                    .source(OrderStatus.USER_DRAFT)
                    .target(OrderStatus.CSD_REVIEW_REQUIRED)
                    .event(OrderEvent.USER_VALIDATES)
                    .guard(csdReviewRequiredGuard)
                    .action(userValidatesOrderAction)
                    .action(new NotifyCsdReviewAction(notificationService, notificationProperties.getIssuerCsdRequired(), notificationProperties.getAdminNoCsds(), userService))
                .and()
                .withExternal()
                    .source(OrderStatus.USER_DRAFT)
                    .target(OrderStatus.DIA_SIGNING_REQUIRED)
                    .event(OrderEvent.USER_VALIDATES)
                    .guard(diaSigningRequiredGuard)
                    .action(userValidatesOrderAction)
                .action(new NotifyDiaReviewAction(notificationService, notificationProperties.getIssuerDiaRequired(), notificationProperties.getAdminNoDiasForUnit(), userService))
                .and()
                .withExternal()
                    .source(OrderStatus.USER_DRAFT)
                    .target(OrderStatus.REMOTE_IDENTIFICATION_REQUIRED)
                    .event(OrderEvent.USER_VALIDATES)
                    .guard(remoteIdRequiredGuard)
                    .action(userValidatesOrderAction)
                    .action(new NotifyCsdReviewAction(notificationService, notificationProperties.getIssuerRemoteId(), notificationProperties.getAdminNoCsds(), userService))
                .and()
                .withExternal()
                    .source(OrderStatus.DRAFT)
                    .target(OrderStatus.SENT_TO_LRS)
                    .event(OrderEvent.SEND_TO_LRS)
                    .action(sendOrderToLRSAction)
                    .guard(shortEnrollmentFlowGuard)
                .and()
                .withExternal()
                    .source(OrderStatus.FACE_2_FACE_REQUIRED)
                    .target(OrderStatus.SENT_TO_LRS)
                    .action(sendOrderToLRSAction)
                    .event(OrderEvent.SEND_TO_LRS)
                .and()
                .withExternal()
                    .source(OrderStatus.CSD_REVIEW_REQUIRED)
                    .target(OrderStatus.SENT_TO_LRS)
                    .action(sendOrderToLRSAction)
                    .event(OrderEvent.SEND_TO_LRS)
                .and()
                .withExternal()
                    .source(OrderStatus.DIA_SIGNING_REQUIRED)
                    .target(OrderStatus.SENT_TO_LRS)
                    .action(sendOrderToLRSAction)
                    .event(OrderEvent.SEND_TO_LRS)
                .and()
                .withExternal()
                    .source(OrderStatus.REMOTE_IDENTIFICATION_REQUIRED)
                    .target(OrderStatus.SENT_TO_LRS)
                    .action(sendOrderToLRSAction)
                    .event(OrderEvent.SEND_TO_LRS)
                .and()
                .withExternal()
                    .source(OrderStatus.FACE_2_FACE_REQUIRED)
                    .target(OrderStatus.REJECTED)
                    .event(OrderEvent.REJECT)
                .and()
                .withExternal()
                    .source(OrderStatus.DRAFT)
                    .target(OrderStatus.REJECTED)
                    .event(OrderEvent.REJECT)
                .and()
                .withExternal()
                    .source(OrderStatus.USER_DRAFT)
                    .target(OrderStatus.REJECTED)
                    .event(OrderEvent.REJECT)
                .and()
                .withExternal()
                    .source(OrderStatus.CSD_REVIEW_REQUIRED)
                    .target(OrderStatus.REJECTED)
                    .event(OrderEvent.REJECT)
                .and()
                .withExternal()
                    .source(OrderStatus.DIA_SIGNING_REQUIRED)
                    .target(OrderStatus.REJECTED)
                    .event(OrderEvent.REJECT)
                .and()
                .withExternal()
                    .source(OrderStatus.REMOTE_IDENTIFICATION_REQUIRED)
                    .target(OrderStatus.REJECTED)
                    .event(OrderEvent.REJECT);
    }

    @Bean
    public StateMachinePersister<OrderStatus, OrderEvent, ContextEntity<OrderStatus, OrderEvent, Long>> persister() {
        return new DefaultStateMachinePersister<>(persist());
    }

    @Bean
    public StateMachinePersist<OrderStatus, OrderEvent, ContextEntity<OrderStatus, OrderEvent, Long>> persist() {
        return new OrderStateMachinePersist();
    }


}
