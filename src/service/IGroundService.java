package service;

import model.Aircraft;

public interface IGroundService {
    boolean canProcess(Aircraft aircraft);
    void serviceFlight(Aircraft aircraft);
    String getServiceType();
}
