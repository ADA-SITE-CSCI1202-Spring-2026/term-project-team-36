package model;

import java.util.Map;

public abstract class Aircraft {

    private final String flightNumber;
    private final int requiredFuel;
    private final int requiredMeals;
    private final int turnaroundTime;
    private final double reward;

    protected Aircraft(String flightNumber, int requiredFuel, int requiredMeals,
                       int turnaroundTime, double reward) {
        this.flightNumber = flightNumber;
        this.requiredFuel = requiredFuel;
        this.requiredMeals = requiredMeals;
        this.turnaroundTime = turnaroundTime;
        this.reward = reward;
    }

    // Function will be add soon

    @Override
    public String toString() { return "[" + getType() + "] " + flightNumber; }
}
