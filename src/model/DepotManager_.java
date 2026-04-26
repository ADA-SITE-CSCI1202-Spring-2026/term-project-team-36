package model;
import java.util.HashMap;
import java.util.Map;
public class DepotManager {
    private final HashMap<Resource, Integer> depot;
    private double budget;
    public DepotManager(double initialBudget, int initialFuel, int initialMeals) {
        this.budget = initialBudget;
        this.depot = new HashMap<>();
    }
    public boolean hasResources(Map<Resource, Integer> required) {
        for (Map.Entry<Resource, Integer> entry : required.entrySet()) {
            int available = depot.getOrDefault(entry.getKey(), 0);
            if (available < entry.getValue()) return false;
        }
        return true;
    }
    public void consumeResources(Map<Resource, Integer> required) {
        for (Map.Entry<Resource, Integer> entry : required.entrySet()) {
            int current = depot.getOrDefault(entry.getKey(), 0);
            depot.put(entry.getKey(), Math.max(0, current - entry.getValue()));
        }
    }
    public void restockResource(Resource r, int amount, double cost) {
        if (budget >= cost) { budget -= cost; depot.put(r, depot.getOrDefault(r, 0) + amount); }
    }
    public void restockResource(Resource r, int amount) {
        double defaultCost = (r == Resource.JET_FUEL) ? 2000.0 : 500.0;
        restockResource(r, amount, defaultCost);
    }
    public boolean canAfford(double cost) { return budget >= cost; }
    public boolean canAfford(int amount) { return canAfford((double) amount); }
    public int getResource(Resource r) { return depot.getOrDefault(r, 0); }
    public void setResource(Resource r, int amount) { depot.put(r, amount); }
    public Map<Resource, Integer> getAllResources() { return new HashMap<>(depot); }
}
