package lu.luxtrust.flowers.fsm;

public abstract class FsmExtendedStateVariables {
    private FsmExtendedStateVariables() {}

    public static final String ORDER = "order";
    public static final String VALIDATION_PAGE = "validation-page";
    public static final String EXCEPTION = "exception";
    public static final String SEND_MOBILE_VALIDATION_CODE = "send-mobile-validation-code";
}
