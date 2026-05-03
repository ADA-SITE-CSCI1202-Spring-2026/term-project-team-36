package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public class DepotManager {
    private final Map<Resource, Integer> depot;
    private BigDecimal budget;

    public DepotManager(BigDecimal initialBudget, int initialFuel, int initialMeals) {
        this.budget = initialBudget;
        this.depot = new HashMap<>();
        depot.put(Resource.JET_FUEL, initialFuel);
        depot.put(Resource.MEALS, initialMeals);
    }

    public DepotManager(double initialBudget, int initialFuel, int initialMeals) {
        this(BigDecimal.valueOf(initialBudget), initialFuel, initialMeals);
    }

    public synchronized boolean hasResources(Map<Resource, Integer> required) {
        for (var entry : required.entrySet()) {
            int available = depot.getOrDefault(entry.getKey(), 0);
            if (available < entry.getValue()) return false;
        }
        return true;
    }

    public synchronized void consumeResources(Map<Resource, Integer> required) {
        for (var entry : required.entrySet()) {
            var current = depot.getOrDefault(entry.getKey(), 0);
            depot.put(entry.getKey(), Math.max(0, current - entry.getValue()));
        }
    }

    public synchronized void restockResource(Resource r, int amount, BigDecimal cost) {
        if (budget.compareTo(cost) >= 0) {
            budget = budget.subtract(cost);
            depot.put(r, depot.getOrDefault(r, 0) + amount);
        }
    }

    public synchronized void restockResource(Resource r, int amount, double cost) {
        restockResource(r, amount, BigDecimal.valueOf(cost));
    }

    public synchronized boolean canAfford(BigDecimal cost) {
        return budget.compareTo(cost) >= 0;
    }

    public synchronized boolean canAfford(double cost) {
        return canAfford(BigDecimal.valueOf(cost));
    }

    public synchronized int getResource(Resource r) {
        return depot.getOrDefault(r, 0);
    }

    public synchronized BigDecimal getBudget() { return budget; }

    public synchronized double getBudgetAsDouble() {
        return budget.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public synchronized void addBudget(BigDecimal amount) { budget = budget.add(amount); }
    public synchronized void addBudget(double amount)     { addBudget(BigDecimal.valueOf(amount)); }

    public synchronized void setBudget(BigDecimal amount) { this.budget = amount; }
    public synchronized void setBudget(double amount)     { this.budget = BigDecimal.valueOf(amount); }

    public synchronized void setResource(Resource r, int amount) {
        depot.put(r, amount);
    }

    public synchronized Map<Resource, Integer> getAllResources() {
        return new HashMap<>(depot);
    }
}
