package lu.luxtrust.flowers.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ValidatedList<T> {

    @Valid
    @NotNull
    private List<T> items;

    public ValidatedList() {

    }

    public ValidatedList(List<T> items) {
        this.items = items;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
