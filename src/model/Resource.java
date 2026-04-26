package model;

public enum Resource {
    JET_FUEL("Jet Fuel", "L"),
    MEALS("In-flight Meals", "units");

    private final String displayName;
    private final String unit;

    Resource(String displayName, String unit) {
        this.displayName = displayName;
        this.unit = unit;
    }

    public String getDisplayName() { return displayName; }
    public String getUnit() { return unit; }

    @Override
    public String toString() { return displayName; }
}
