package lu.luxtrust.flowers.model;

import java.util.List;

public class FieldFilter {
    public enum ValueType {
        STRING_LIKE, INTEGER, FLOAT, STRING_EQ
    }

    public FieldFilter() {
    }

    public FieldFilter(String field, List<String> value, ValueType type) {
        this.field = field;
        this.value = value;
        this.type = type;
    }

    private String field;
    private List<String> value;
    private ValueType type;

    public ValueType getType() {
        return type;
    }

    public void setType(ValueType type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
}
