package lu.luxtrust.flowers.job;

import com.google.common.collect.Lists;
import lu.luxtrust.flowers.entity.enrollment.CertificateOrder;
import lu.luxtrust.flowers.entity.enrollment.OrderUserValidatePage;
import lu.luxtrust.flowers.model.SMS;
import lu.luxtrust.flowers.repository.CertificateOrderRepository;
import lu.luxtrust.flowers.repository.OrderUserValidatePageRepository;
import lu.luxtrust.flowers.service.NotificationService;
import lu.luxtrust.flowers.service.OrderValidationPageService;
import lu.luxtrust.flowers.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EmailNotificationJob {

    private static final Logger LOG = LoggerFactory.getLogger(EmailNotificationJob.class);

    private final CertificateOrderRepository orderRepository;
    private final int userValidationChunkSize;
    private final OrderValidationPageService orderValidationPageService;
    private final NotificationService notificationService;
    private final OrderUserValidatePageRepository orderUserValidatePageRepository;
    private final Boolean sendSmsValidationCode;
    private final SmsService smsService;

    @Autowired
    public EmailNotificationJob(@Value("${job.email.notification.user-validation-chunk-size:100}") int userValidationChunkSize,
                                @Value("${user-validation-page.mobile-code-validation.enabled}") Boolean sendSmsValidationCode,
                                OrderValidationPageService orderValidationPageService,
                                SmsService smsService,
                                OrderUserValidatePageRepository orderUserValidatePageRepository,
                                NotificationService notificationService,
                                CertificateOrderRepository orderRepository) {
        this.userValidationChunkSize = userValidationChunkSize;
        this.orderUserValidatePageRepository = orderUserValidatePageRepository;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
        this.orderValidationPageService = orderValidationPageService;
        this.sendSmsValidationCode = sendSmsValidationCode;
        this.smsService = smsService;
    }

    @Scheduled(cron = "${job.email.user-validation-page.period:0 0/10 * ? * *}")
    public void sendOrderValidationEmailToUser() {
        LOG.info("Email notification job started.");

        int count = orderUserValidatePageRepository.removePagesWithExpirationDateLessThenCurrent();
        LOG.info("{} Expired validation pages have been cleaned up", count);

        List<OrderUserValidatePage> validationPages = orderValidationPageService.generateValidationPages(orderRepository.findOrdersWithoutActiveValidationPage());
        LOG.info("{} user validation pages generated. Sending emails...", validationPages.size());
        try {
            notificationService.notifyEndUserToValidateOrder(validationPages);
            if (sendSmsValidationCode) {
                sendMobileValidationCodes(validationPages);
            }
            LOG.info("{} users were notified to validation their orders.", validationPages.size());
        } catch (Exception e) {
            LOG.error("Notification of {} users failed! Exception: {}", validationPages.size(), e);
        }

        LOG.info("Email notification job finished.");
    }

    private void sendMobileValidationCodes(List<OrderUserValidatePage> validationPages) {
        Map<SMS, OrderUserValidatePage> sms2page = new HashMap<>(validationPages.size() * 2);
        validationPages.forEach((page) -> {
            sms2page.put(orderValidationPageService.generateMobileValidationSMS(page), page);
        });
        List<OrderUserValidatePage> failed = new LinkedList<>();
        List<Long> failedIds = new LinkedList<>();
        smsService.send(new ArrayList<>(sms2page.keySet())).forEach((sms) -> {
            if (sms.getStatus() == SMS.Status.FAILED) {
                OrderUserValidatePage page = sms2page.get(sms);
                page.setExpirationTime(new Date(System.currentTimeMillis() - 1000000));
                failedIds.add(page.getOrder().getId());
                failed.add(page);
            }
        });
        if (!failed.isEmpty()) {
            LOG.error("Cant send sms codes to the following orders=[{}]", failedIds);
            orderUserValidatePageRepository.save(failed);
        }
    }
}
