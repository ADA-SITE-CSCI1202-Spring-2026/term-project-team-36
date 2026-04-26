package model;

import java.util.EnumMap;
import java.util.Map;

public class CommercialJet extends Aircraft {

    public CommercialJet(String flightNumber) {
        super(flightNumber, 500, 200, 45, 8000.0);
    }

    @Override
    public String getType() { return "CommercialJet"; }

    @Override
    public Map<Resource, Integer> getRequiredResources() {
        var map = new EnumMap<Resource, Integer>(Resource.class);
        map.put(Resource.JET_FUEL, getRequiredFuel());
        map.put(Resource.MEALS, getRequiredMeals());
        return map;
    }
}
