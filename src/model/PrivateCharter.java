package model;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

public final class PrivateCharter extends Aircraft {
    private static final long serialVersionUID = 1L;

    public PrivateCharter(String flightNumber) {
        super(flightNumber, 150, 20, 20, new BigDecimal("3000"));
    }

    @Override
    public String getType() { return "PrivateCharter"; }

    @Override
    public Map<Resource, Integer> getRequiredResources() {
        var map = new EnumMap<Resource, Integer>(Resource.class);
        map.put(Resource.JET_FUEL, getRequiredFuel());
        map.put(Resource.MEALS, getRequiredMeals());
        return map;
    }
}
