package lu.luxtrust.flowers.validation;

import javax.validation.groups.Default;

public abstract class ValidationGroups {
    private ValidationGroups(){}

    public interface OrderDraft extends Default{}

    public interface OrderUserDraft extends OrderDraft{}

    public interface OrderSentToLRS extends OrderUserDraft{}

    public interface OrderUserDraftAPI extends Default {}

}
