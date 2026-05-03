package model;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

public final class CargoFreighter extends Aircraft {

    private static final long serialVersionUID = 1L;

    public CargoFreighter(String flightNumber) {
        super(flightNumber, 800, 0, 60, new BigDecimal("5000"));
    }

    @Override
    public String getType() { return "CargoFreighter"; }

    @Override
    public Map<Resource, Integer> getRequiredResources() {
        var map = new EnumMap<Resource, Integer>(Resource.class);
        map.put(Resource.JET_FUEL, getRequiredFuel());
        return map;
    }
}
