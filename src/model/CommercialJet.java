package model;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

public final class CommercialJet extends Aircraft {

    private static final long serialVersionUID = 1L;

    public CommercialJet(String flightNumber) {
        super(flightNumber, 500, 200, 45, new BigDecimal("8000"));
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
