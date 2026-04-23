package service;

import model.Aircraft;

]public class BaggageHandler implements IGroundService {

    @Override
    public boolean canProcess(Aircraft aircraft) {
        return true;  
    }

    @Override
    public void serviceFlight(Aircraft aircraft) {
    }

    @Override
    public String getServiceType() { return "Baggage Handler"; }
}
