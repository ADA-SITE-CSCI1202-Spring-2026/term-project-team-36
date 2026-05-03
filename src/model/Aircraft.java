package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public abstract sealed class Aircraft
        implements Serializable, Comparable<Aircraft>
        permits CommercialJet, CargoFreighter, PrivateCharter {
    private static final long serialVersionUID = 1L;

    private final String flightNumber;
    private final int requiredFuel;
    private final int requiredMeals;
    private final int turnaroundTime;
    private final BigDecimal reward;

    protected Aircraft(String flightNumber, int requiredFuel, int requiredMeals,
                       int turnaroundTime, BigDecimal reward) {
        this.flightNumber = flightNumber;
        this.requiredFuel = requiredFuel;
        this.requiredMeals = requiredMeals;
        this.turnaroundTime = turnaroundTime;
        this.reward = reward;
    }

    public abstract String getType();
    public abstract Map<Resource, Integer> getRequiredResources();

    public String toString(boolean detailed) {
        if (detailed) {
            var sb = new StringBuilder();
            sb.append("[").append(getType()).append("] ")
              .append(flightNumber)
              .append(" | Fuel: ").append(requiredFuel).append("L")
              .append(" | Meals: ").append(requiredMeals)
              .append(" | +$").append(reward.toPlainString());
            return sb.toString();
        }
        return toString();
    }

    @Override
    public int compareTo(Aircraft other) {
        return this.reward.compareTo(other.reward);
    }

    public String getFlightNumber()  { return flightNumber; }
    public int getRequiredFuel()     { return requiredFuel; }
    public int getRequiredMeals()    { return requiredMeals; }
    public int getTurnaroundTime()   { return turnaroundTime; }
    public BigDecimal getReward()    { return reward; }

    @Override
    public String toString() {
        return "[" + getType() + "] " + flightNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Aircraft ac)) return false;
        return Objects.equals(flightNumber, ac.flightNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightNumber);
    }
}
