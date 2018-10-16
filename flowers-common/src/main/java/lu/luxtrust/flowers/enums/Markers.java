package lu.luxtrust.flowers.enums;

public enum Markers {
    AUDIT("AUDIT");

    private String name;

    public String getName() {
        return name;
    }

    Markers(String markerName) {
        this.name = markerName;
    }
}
