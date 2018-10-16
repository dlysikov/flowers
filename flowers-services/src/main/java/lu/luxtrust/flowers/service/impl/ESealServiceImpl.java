package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.entity.enrollment.ESealOrder;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.OrderStatus;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.model.PageParams;
import lu.luxtrust.flowers.model.PageResponse;
import lu.luxtrust.flowers.properties.NotificationProperties;
import lu.luxtrust.flowers.repository.ESealOrderRepository;
import lu.luxtrust.flowers.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ESealServiceImpl extends OrderServiceImpl<ESealOrder> implements ESealService  {

    private final ESealOrderRepository repository;
    private final LrsService lrsService;
    private final NotificationService notificationService;
    private final NotificationProperties notificationProperties;

    @Autowired
    public ESealServiceImpl(ESealOrderRepository repository, LrsService lrsService, NotificationService notificationService, NotificationProperties notificationProperties) {
        super(repository, lrsService);
        this.repository = repository;
        this.lrsService = lrsService;
        this.notificationService = notificationService;
        this.notificationProperties = notificationProperties;
    }

    @Override
    public void sendToLrs(ESealOrder order) throws Exception {
        repository.save(order);
        order.setLrsOrderNumber(lrsService.register(order));
        order.setStatus(OrderStatus.SENT_TO_LRS);
        repository.save(order);
        notifyUpdate(OrderStatus.DRAFT, null, null, order);
    }

    @Override
    public PageResponse<ESealOrder> findAll(Long userId, PageParams params) {
        if (userId != null) {
            PredicateProducer pp = (cb, root) -> Optional.of(cb.equal(root.join("eSealManagers").get("id"), userId));
            return findAll(params, true, pp);
        }
        return findAll(params, false);
    }

    @Override
    public boolean isValuableDataModifiable(Long id) {
        return isValuableDataModifiable(repository.getStatusById(id));
    }

    private boolean isValuableDataModifiable(OrderStatus status) {
        return status == OrderStatus.DRAFT;
    }

    @Override
    public ESealOrder save(ESealOrder order) {
        OrderStatus oldStatus = null;
        List<User> oldManagers = null;
        String oldAdminEmail = null;
        if (order.getId() != null) {
            ESealOrder old = repository.getOne(order.getId());
            oldStatus = old.getStatus();
            oldManagers = new ArrayList<>(old.geteSealManagers());
            oldAdminEmail = old.geteSealAdministratorEmail();
            if (!isValuableDataModifiable(order.getStatus())) {
                boolean unitChanged = old.getUnit() != null && !old.getUnit().getId().equals(order.getUnit().getId());
                if (unitChanged) {
                    throw new FlowersException("restricted to modify unit in order with id=" + order.getId());
                }
            }
        }
        repository.save(order);
        notifyUpdate(oldStatus, oldManagers, oldAdminEmail, order);
        return order;
    }

    @Override
    public void updateStatus(String orderNumber, OrderStatus currentStatus, OrderStatus newStatus) {
        super.updateStatus(orderNumber, currentStatus, newStatus);
        ESealOrder order = repository.findOneByLrsOrderNumber(orderNumber);
        notifyStatusChange(currentStatus, order, order.geteSealManagers().stream().map(User::getEmail).collect(Collectors.toList()));
    }

    private void notifyUpdate(OrderStatus oldStatus, List<User> oldManagers, String oldAdminEmail, ESealOrder order) {
        if (order.getStatus() == OrderStatus.DRAFT) {
            return;
        }
        List<String> esealManagerEmails = order.geteSealManagers().stream().map(User::getEmail).collect(Collectors.toList());
        notifyAdminChange(oldStatus, oldAdminEmail, order, esealManagerEmails);
        notifyManagersChange(oldStatus, oldManagers, order);
        notifyStatusChange(oldStatus, order, esealManagerEmails);
    }

    private void notifyAdminChange(OrderStatus oldStatus, String oldAdminEmail, ESealOrder order, List<String> esealManagerEmails) {
        if (oldAdminEmail == null || oldStatus == OrderStatus.DRAFT) {
            notificationService.notify(Collections.singletonMap("order", order), Collections.singletonList(order.geteSealAdministratorEmail()), notificationProperties.getEsealAdministratorAssigned());
        } else {
            if (!oldAdminEmail.equals(order.geteSealAdministratorEmail())) {
                notificationService.notify(Collections.singletonMap("order", order), Collections.singletonList(oldAdminEmail), notificationProperties.getEsealAdministratorRemoved());
                notificationService.notify(Collections.singletonMap("order", order), Collections.singletonList(order.geteSealAdministratorEmail()), notificationProperties.getEsealAdministratorAssigned());
                notificationService.notify(Collections.singletonMap("order", order), esealManagerEmails, notificationProperties.getEsealAdministratorChanged());
            }
        }
    }

    private void notifyManagersChange(OrderStatus oldStatus, List<User> oldManagers, ESealOrder order) {
        if (oldManagers == null || oldManagers.isEmpty() || oldStatus == OrderStatus.DRAFT) {
            List<String> newManagersEmails = order.geteSealManagers().stream().map(User::getEmail).collect(Collectors.toList());
            notificationService.notify(Collections.singletonMap("order", order), newManagersEmails, notificationProperties.getEsealManagerAssigned());
        } else {
            Map<Long, User> oldManagersMap = oldManagers.stream().collect(Collectors.toMap(User::getId, Function.identity()));
            Map<Long, User> newManagersMap = order.geteSealManagers().stream().collect(Collectors.toMap(User::getId, Function.identity()));
            if (!CollectionUtils.isEqualCollection(oldManagersMap.keySet(), newManagersMap.keySet())) {
                List<Long> removedManagers = new ArrayList<>(oldManagersMap.keySet());
                removedManagers.removeAll(newManagersMap.keySet());
                if (!removedManagers.isEmpty()) {
                    List<String> removedManagersEmails = removedManagers.stream().map(id -> oldManagersMap.get(id).getEmail()).collect(Collectors.toList());
                    notificationService.notify(Collections.singletonMap("order", order), removedManagersEmails, notificationProperties.getEsealManagerRemoved());
                }

                List<Long> assignedManagers = new ArrayList<>(newManagersMap.keySet());
                assignedManagers.removeAll(oldManagersMap.keySet());
                if (!newManagersMap.isEmpty()) {
                    List<String> newManagersEmails = assignedManagers.stream().map(id -> newManagersMap.get(id).getEmail()).collect(Collectors.toList());
                    notificationService.notify(Collections.singletonMap("order", order), newManagersEmails, notificationProperties.getEsealManagerAssigned());
                }
                Map<String, Object> params = new HashMap<>();
                params.put("removed", removedManagers.stream().map(oldManagersMap::get).collect(Collectors.toList()));
                params.put("assigned", assignedManagers.stream().map(newManagersMap::get).collect(Collectors.toList()));
                params.put("order", order);
                notificationService.notify(params, Collections.singletonList(order.geteSealAdministratorEmail()), notificationProperties.getEsealAdministratorManagersChanged());
            }
        }
    }

    private void notifyStatusChange(OrderStatus oldStatus, ESealOrder order, List<String> esealManagerEmails) {
        if (oldStatus != order.getStatus()) {
            Map<String, Object> params = new HashMap<>();
            params.put("oldStatus", oldStatus);
            params.put("order", order);
            notificationService.notify(params, Collections.singletonList(order.geteSealAdministratorEmail()), notificationProperties.getEsealAdministratorEsealStatusChanged());
            if (order.getStatus() == OrderStatus.LRS_PRODUCED) {
                notificationService.notify(params, esealManagerEmails, notificationProperties.getEsealHasToBeActivated());
            } else {
                notificationService.notify(params, esealManagerEmails, notificationProperties.getEsealManagerEsealStatusChanged());
            }
        }
    }
}
