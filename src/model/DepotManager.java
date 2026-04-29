package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

// Week 3:  Encapsulation — private fields with public methods
// Week 6:  BigDecimal for precise monetary calculations
// Week 8:  HashMap for resource storage
// Week 13: synchronized methods for thread safety
public class DepotManager {

    // Week 3: Private fields
    private final Map<Resource, Integer> depot;  // Week 8: HashMap
    private BigDecimal budget;                   // Week 6: BigDecimal

    // Week 3: Constructor with this keyword
    public DepotManager(BigDecimal initialBudget, int initialFuel, int initialMeals) {
        this.budget = initialBudget;
        this.depot = new HashMap<>();
        depot.put(Resource.JET_FUEL, initialFuel);
        depot.put(Resource.MEALS, initialMeals);
    }

    // Week 3: Method overloading — second constructor accepting double
    public DepotManager(double initialBudget, int initialFuel, int initialMeals) {
        this(BigDecimal.valueOf(initialBudget), initialFuel, initialMeals);
    }

    // Week 13: synchronized for thread-safe access from simulation and UI threads
    public synchronized boolean hasResources(Map<Resource, Integer> required) {
        for (var entry : required.entrySet()) {  // Week 2: var, Week 4: for-each
            int available = depot.getOrDefault(entry.getKey(), 0);
            if (available < entry.getValue()) return false;
        }
        return true;
    }

    public synchronized void consumeResources(Map<Resource, Integer> required) {
        for (var entry : required.entrySet()) {
            var current = depot.getOrDefault(entry.getKey(), 0);  // Week 2: var
            depot.put(entry.getKey(), Math.max(0, current - entry.getValue()));
        }
    }

    // Week 3: Method overloading — restockResource with BigDecimal cost
    public synchronized void restockResource(Resource r, int amount, BigDecimal cost) {
        if (budget.compareTo(cost) >= 0) {
            budget = budget.subtract(cost);
            depot.put(r, depot.getOrDefault(r, 0) + amount);
        }
    }

    // Week 3: Method overloading — restockResource with double cost
    public synchronized void restockResource(Resource r, int amount, double cost) {
        restockResource(r, amount, BigDecimal.valueOf(cost));
    }

    public synchronized boolean canAfford(BigDecimal cost) {
        return budget.compareTo(cost) >= 0;
    }

    // Week 3: Method overloading — canAfford with double
    public synchronized boolean canAfford(double cost) {
        return canAfford(BigDecimal.valueOf(cost));
    }

    public synchronized int getResource(Resource r) {
        return depot.getOrDefault(r, 0);
    }

    // Week 6: Returns BigDecimal for precise financial operations
    public synchronized BigDecimal getBudget() { return budget; }

    // Convenience method for UI formatting
    public synchronized double getBudgetAsDouble() {
        return budget.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    // Week 3: Method overloading — addBudget
    public synchronized void addBudget(BigDecimal amount) { budget = budget.add(amount); }
    public synchronized void addBudget(double amount)     { addBudget(BigDecimal.valueOf(amount)); }

    // Week 3: Method overloading — setBudget
    public synchronized void setBudget(BigDecimal amount) { this.budget = amount; }
    public synchronized void setBudget(double amount)     { this.budget = BigDecimal.valueOf(amount); }

    public synchronized void setResource(Resource r, int amount) {
        depot.put(r, amount);
    }

    // Week 8: Returns a defensive copy of all resources
    public synchronized Map<Resource, Integer> getAllResources() {
        return new HashMap<>(depot);
    }
}
