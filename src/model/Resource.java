package model;

import java.io.Serializable;

public enum Resource implements Serializable {
    JET_FUEL("Jet Fuel", "L"),
    MEALS("In-flight Meals", "units");

    private final String displayName;
    private final String unit;

    Resource(String displayName, String unit) {
        this.displayName = displayName;
        this.unit = unit;
    }

    public String getDisplayName() { return displayName; }
    public String getUnit()        { return unit; }

    public int getDefaultRestockAmount() {
        return switch (this) {
            case JET_FUEL -> 500;
            case MEALS    -> 50;
        };
    }

    public double getRestockCost() {
        return switch (this) {
            case JET_FUEL -> 2000.0;
            case MEALS    -> 500.0;
        };
    }

    @Override
    public String toString() { return displayName; }
}
