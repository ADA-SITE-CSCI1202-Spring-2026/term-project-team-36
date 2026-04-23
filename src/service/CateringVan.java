package service;

import model.Aircraft;
import model.Resource;

public class CateringVan implements IGroundService {

    @Override
    public boolean canProcess(Aircraft aircraft) {
        return aircraft.getRequiredResources().containsKey(Resource.MEALS)
                && aircraft.getRequiredResources().get(Resource.MEALS) > 0;
    }

    @Override
    public void serviceFlight(Aircraft aircraft) {
    }

    @Override
    public String getServiceType() { return "Catering Van"; }
}
