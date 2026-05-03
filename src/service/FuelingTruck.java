package service;

import model.Aircraft;
import model.Resource;

public class FuelingTruck implements IGroundService {
    @Override
    public boolean canProcess(Aircraft aircraft) {
        return aircraft.getRequiredResources().containsKey(Resource.JET_FUEL)
                && aircraft.getRequiredResources().get(Resource.JET_FUEL) > 0;
    }

    @Override
    public void serviceFlight(Aircraft aircraft) {
    }

    @Override
    public String getServiceType() { return "Fueling Truck"; }
}
