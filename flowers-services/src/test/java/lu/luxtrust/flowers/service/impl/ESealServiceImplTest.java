package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.entity.builder.UnitBuilder;
import lu.luxtrust.flowers.entity.builder.UserBuilder;
import lu.luxtrust.flowers.entity.enrollment.ESealOrder;
import lu.luxtrust.flowers.entity.enrollment.Unit;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.properties.NotificationProperties;
import lu.luxtrust.flowers.repository.ESealOrderRepository;
import lu.luxtrust.flowers.service.LrsService;
import lu.luxtrust.flowers.service.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.mail.MessagingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ESealServiceImplTest {

    private static final User esealManager1 = UserBuilder.newBuilder().id(1L).email("email1").build();
    private static final User esealManager2 = UserBuilder.newBuilder().id(2L).email("email2").build();
    private static final User esealManager3 = UserBuilder.newBuilder().id(3L).email("email3").build();
    private static final String esealAdministratorEmail1 = "adminMail1";
    private static final String esealAdministratorEmail2 = "adminMail2";
    private static final Unit unit = UnitBuilder.newBuilder().id(1L).build();
    private static final Unit unit2 = UnitBuilder.newBuilder().id(2L).build();
    private static final long ORDER_ID = 42L;

    @Mock private ESealOrderRepository repository;
    @Mock private LrsService lrsService;
    @Mock private NotificationService notificationService;

    private NotificationProperties notificationProperties = new NotificationProperties();
    private ESealServiceImpl service;

    private ESealOrder old;
    private ESealOrder order;

    {
        notificationProperties.setEsealAdministratorChanged(new NotificationProperties.NotificationConfig());
        notificationProperties.setEsealAdministratorAssigned(new NotificationProperties.NotificationConfig());
        notificationProperties.setEsealAdministratorRemoved(new NotificationProperties.NotificationConfig());
        notificationProperties.setEsealAdministratorEsealStatusChanged(new NotificationProperties.NotificationConfig());
        notificationProperties.setEsealAdministratorManagersChanged(new NotificationProperties.NotificationConfig());

        notificationProperties.setEsealManagerAssigned(new NotificationProperties.NotificationConfig());
        notificationProperties.setEsealManagerRemoved(new NotificationProperties.NotificationConfig());
        notificationProperties.setEsealAdministratorManagersChanged(new NotificationProperties.NotificationConfig());
        notificationProperties.setEsealManagerEsealStatusChanged(new NotificationProperties.NotificationConfig());
    }

    @Before
    public void init() {
        service = new ESealServiceImpl(repository, lrsService, notificationService, notificationProperties);
        old = new ESealOrder();
        order = new ESealOrder();
        order.setUnit(unit);
        order.setStatus(OrderStatus.DRAFT);
        order.seteSealAdministratorEmail(esealAdministratorEmail2);
        order.seteSealManagers(Collections.singletonList(esealManager2));
        when(repository.getOne(ORDER_ID)).thenReturn(old);
        when(repository.save(order)).thenReturn(order);
    }

    @Test
    public void save() throws Exception {
        service.save(order);
        order.setId(ORDER_ID);

        verifyNoMoreInteractions(notificationService);

        copy(order, old);
        order.seteSealManagers(Arrays.asList(esealManager1, esealManager2));
        order.seteSealAdministratorEmail(esealAdministratorEmail1);
        service.save(order);

        verifyNoMoreInteractions(notificationService);

        copy(order, old);
        service.sendToLrs(order);

        verify(notificationService).notify(anyMap(), eq(Collections.singletonList(esealAdministratorEmail1)), eq(notificationProperties.getEsealAdministratorAssigned()));
        verify(notificationService).notify(anyMap(), eq(Arrays.asList(esealManager1.getEmail(), esealManager2.getEmail())), eq(notificationProperties.getEsealManagerAssigned()));
        verify(notificationService).notify(anyMap(), eq(Collections.singletonList(esealAdministratorEmail1)), eq(notificationProperties.getEsealAdministratorEsealStatusChanged()));
        verify(notificationService).notify(anyMap(), eq(Arrays.asList(esealManager1.getEmail(), esealManager2.getEmail())), eq(notificationProperties.getEsealManagerEsealStatusChanged()));
        verifyNoMoreInteractions(notificationService);

        copy(order, old);
        order.seteSealAdministratorEmail(esealAdministratorEmail2);
        service.save(order);

        verify(notificationService).notify(anyMap(), eq(Collections.singletonList(esealAdministratorEmail1)), eq(notificationProperties.getEsealAdministratorRemoved()));
        verify(notificationService).notify(anyMap(), eq(Collections.singletonList(esealAdministratorEmail2)), eq(notificationProperties.getEsealAdministratorAssigned()));
        verify(notificationService).notify(anyMap(), eq(Arrays.asList(esealManager1.getEmail(), esealManager2.getEmail())), eq(notificationProperties.getEsealAdministratorChanged()));
        verifyNoMoreInteractions(notificationService);

        copy(order, old);
        order.seteSealManagers(Arrays.asList(esealManager2, esealManager3));
        service.save(order);

        verify(notificationService).notify(anyMap(), eq(Collections.singletonList(esealAdministratorEmail2)), eq(notificationProperties.getEsealAdministratorManagersChanged()));
        verify(notificationService).notify(anyMap(), eq(Collections.singletonList(esealManager1.getEmail())), eq(notificationProperties.getEsealManagerRemoved()));
        verify(notificationService).notify(anyMap(), eq(Collections.singletonList(esealManager3.getEmail())), eq(notificationProperties.getEsealManagerAssigned()));
        verifyNoMoreInteractions(notificationService);

        copy(order, old);
        order.setStatus(OrderStatus.LRS_PRODUCED);
        service.save(order);

        verify(notificationService).notify(anyMap(), eq(Collections.singletonList(esealAdministratorEmail2)), eq(notificationProperties.getEsealAdministratorEsealStatusChanged()));
        verify(notificationService).notify(anyMap(), eq(Arrays.asList(esealManager2.getEmail(), esealManager3.getEmail())), eq(notificationProperties.getEsealHasToBeActivated()));
        verifyNoMoreInteractions(notificationService);

        verify(repository, times(7)).save(order);
    }

    @Test
    public void sendToLrsWithoutDraft() throws Exception {
        order.seteSealManagers(Arrays.asList(esealManager1, esealManager2));
        order.seteSealAdministratorEmail(esealAdministratorEmail1);
        service.sendToLrs(order);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.SENT_TO_LRS);
        verify(repository, times(2)).save(order);
        verify(notificationService).notify(anyMap(), eq(Collections.singletonList(esealAdministratorEmail1)), eq(notificationProperties.getEsealAdministratorAssigned()));
        verify(notificationService).notify(anyMap(), eq(Arrays.asList(esealManager1.getEmail(), esealManager2.getEmail())), eq(notificationProperties.getEsealManagerAssigned()));
        verify(notificationService).notify(anyMap(), eq(Collections.singletonList(esealAdministratorEmail1)), eq(notificationProperties.getEsealAdministratorEsealStatusChanged()));
        verify(notificationService).notify(anyMap(), eq(Arrays.asList(esealManager1.getEmail(), esealManager2.getEmail())), eq(notificationProperties.getEsealManagerEsealStatusChanged()));
        verifyNoMoreInteractions(notificationService);
    }

    private void copy(ESealOrder from, ESealOrder to) {
        to.seteSealManagers(new ArrayList<>(from.geteSealManagers()));
        to.seteSealAdministratorEmail(from.geteSealAdministratorEmail());
        to.setUnit(from.getUnit());
        to.setId(from.getId());
        to.setStatus(from.getStatus());
    }

    @Test(expected = FlowersException.class)
    public void saveExceptionWhenUnitChangedNotInDraft() {
        order.setStatus(OrderStatus.SENT_TO_LRS);
        service.save(order);
        order.setId(ORDER_ID);

        copy(order, old);
        order.setUnit(unit2);
        service.save(order);
    }

    @Test
    public void sendToLrs() throws Exception {
        when(lrsService.register(order)).thenReturn("trololo");

        service.sendToLrs(order);

        assertThat(order.getLrsOrderNumber()).isEqualTo("trololo");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SENT_TO_LRS);
    }

}