package model;

import java.util.EnumMap;
import java.util.Map;

public class CargoFreighter extends Aircraft {

    public CargoFreighter(String flightNumber) {
        super(flightNumber, 800, 0, 60, 5000.0);
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
