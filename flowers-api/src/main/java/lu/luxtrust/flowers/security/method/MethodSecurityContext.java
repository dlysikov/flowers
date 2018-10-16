package lu.luxtrust.flowers.security.method;

import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.repository.*;
import lu.luxtrust.flowers.service.ResponseAPIService;
import lu.luxtrust.flowers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Component
public class MethodSecurityContext {

    @Autowired
    private CertificateOrderRepository certificateOrderRepository;

    @Autowired
    private ESealOrderRepository esealOrderRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private OrderUserValidatePageRepository orderUserValidatePageRepository;

    @Autowired
    private ResponseAPIService responseAPIService;

    @Autowired
    private UserService userService;

    @Value("${user-validation-page.mobile-code-validation.enabled}")
    private Boolean sendSmsValidationCode;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private List<IsManagerOfEvaluator> isManagerOfEvaluators;

    public void setIsManagerOfEvaluators(List<IsManagerOfEvaluator> isManagerOfEvaluators) {
        this.isManagerOfEvaluators = isManagerOfEvaluators;
    }

    public CertificateOrderRepository getCertificateOrderRepository() {
        return certificateOrderRepository;
    }

    public void setCertificateOrderRepository(CertificateOrderRepository certificateOrderRepository) {
        this.certificateOrderRepository = certificateOrderRepository;
    }

    public DocumentRepository getDocumentRepository() {
        return documentRepository;
    }

    public void setDocumentRepository(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public OrderUserValidatePageRepository getOrderUserValidatePageRepository() {
        return orderUserValidatePageRepository;
    }

    public void setOrderUserValidatePageRepository(OrderUserValidatePageRepository orderUserValidatePageRepository) {
        this.orderUserValidatePageRepository = orderUserValidatePageRepository;
    }

    public ResponseAPIService getResponseAPIService() {
        return responseAPIService;
    }

    public void setResponseAPIService(ResponseAPIService responseAPIService) {
        this.responseAPIService = responseAPIService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public Boolean getSendSmsValidationCode() {
        return sendSmsValidationCode;
    }

    public void setSendSmsValidationCode(Boolean sendSmsValidationCode) {
        this.sendSmsValidationCode = sendSmsValidationCode;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ESealOrderRepository getEsealOrderRepository() {
        return esealOrderRepository;
    }

    public void setEsealOrderRepository(ESealOrderRepository esealOrderRepository) {
        this.esealOrderRepository = esealOrderRepository;
    }

    public IsManagerOfEvaluator getIsManagerOfEvaluator(String type) {
        if (StringUtils.isEmpty(type)) {
            throw new IllegalArgumentException("Type can't be empty IsManagerOfEvaluator`s");
        }
        Optional<IsManagerOfEvaluator> evaluator = isManagerOfEvaluators.stream().filter((e) -> e.supportedType().getSimpleName().toLowerCase().equals(type.toLowerCase())).findFirst();
        return evaluator.orElseThrow(() -> new IllegalArgumentException("Type + " + type + " is not supported by IsManagerOfEvaluator`s"));
    }
}
