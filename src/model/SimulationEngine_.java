package model;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import service.BaggageHandler;
public class SimulationEngine {
    private final Queue<Aircraft> flightQueue;
    private final DepotManager depot;
    private final List<IGroundService> groundCrews;
    private final Difficulty difficulty;
    private Timeline timer;
    private final Random random = new Random();
    private int flightCounter = 100;
    private int clearedFlights = 0;
    private boolean gameOver = false;
    private Consumer<Aircraft> onFlightArrival;
    private Consumer<String> onLogMessage;
    private Runnable onStateChanged;
    private Runnable onWin;
    private Consumer<String> onLose;
    private static final String[] AIRLINE_PREFIXES = {"UA","SW","DL","AA","SK","FX","PV","BA","LH","QF"};
    public SimulationEngine(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.flightQueue = new ArrayDeque<>();
        this.depot = new DepotManager(difficulty.getInitialBudget(), difficulty.getInitialFuel(), difficulty.getInitialMeals());
        groundCrews = new ArrayList<>();
        groundCrews.add(new BaggageHandler());
    }
    public void setOnFlightArrival(Consumer<Aircraft> h) { this.onFlightArrival = h; }
    public void setOnLogMessage(Consumer<String> h) { this.onLogMessage = h; }
    public void setOnStateChanged(Runnable h) { this.onStateChanged = h; }
    public void setOnWin(Runnable h) { this.onWin = h; }
    public void setOnLose(Consumer<String> h) { this.onLose = h; }
    public void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(difficulty.getSpawnIntervalSec()), e -> {
            if (gameOver) return;
            double opCost = difficulty.getOperationalCostPerInterval();
            depot.addBudget(-opCost);
            log("WARNING: New Arrival - " + ac.getType() + " " + ac.getFlightNumber());
            if (onFlightArrival != null) onFlightArrival.accept(ac);
            if (onStateChanged != null) onStateChanged.run();
            if (flightQueue.size() > difficulty.getMaxQueueSize()) {
                triggerLose("QUEUE OVERFLOW — holding pattern exceeded " + difficulty.getMaxQueueSize() + " aircraft. Airspace collapsed.");
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }
    public void stopTimer() { if (timer != null) timer.stop(); }
    public Aircraft generateRandomFlight() {
        String prefix = AIRLINE_PREFIXES[random.nextInt(AIRLINE_PREFIXES.length)];
        String number = prefix + "-" + (++flightCounter);
        return switch (random.nextInt(3)) {
            case 0 -> new CommercialJet(number);
            case 1 -> new CargoFreighter(number);
            default -> new PrivateCharter(number);
        };
    }
    public boolean processNextFlight() {
        if (gameOver) return false;
        Aircraft aircraft = flightQueue.poll();
        if (aircraft == null) { log("INFO: No flights in holding pattern."); return false; }
        java.util.Map<Resource, Integer> required = aircraft.getRequiredResources();
        if (!depot.hasResources(required)) {
            StringBuilder missing = new StringBuilder();
            for (java.util.Map.Entry<Resource, Integer> entry : required.entrySet()) {
                if (depot.getResource(entry.getKey()) < entry.getValue()) {
                    missing.append(entry.getKey().getDisplayName()).append(" (need ").append(entry.getValue()).append(", have ").append(depot.getResource(entry.getKey())).append(") ");
                }
            }
            log("ERROR: Cannot clear " + aircraft.getFlightNumber() + " — Insufficient: " + missing.toString().trim() + "!");
            if (onStateChanged != null) onStateChanged.run();
            return false;
        }
        for (IGroundService crew : groundCrews) {
            if (crew.canProcess(aircraft)) {
                crew.serviceFlight(aircraft);
                log("DISPATCH: " + crew.getServiceType() + " servicing " + aircraft.getFlightNumber());
            }
        }
        depot.consumeResources(required);
        depot.addBudget(aircraft.getReward());
        clearedFlights++;
        log("CLEARED: " + aircraft.getFlightNumber() + " departed. Revenue: +$" + String.format("%.0f", aircraft.getReward()) + "  |  Budget: $" + String.format("%,.0f", depot.getBudget()));
        if (onStateChanged != null) onStateChanged.run();
        if (depot.getBudget() >= difficulty.getWinTargetBudget()) { triggerWin(); }
        return true;
    }
    public boolean purchaseSupply(Resource r) {
        if (gameOver) return false;
        int amount; double cost;
        if (r == Resource.JET_FUEL) { amount = 500; cost = 2000.0; }
        else { amount = 50; cost = 500.0; }
        if (!depot.canAfford(cost)) { log("ERROR: Insufficient budget to purchase " + r.getDisplayName() + "! Need $" + cost); return false; }
        depot.restockResource(r, amount, cost);
        log("SUPPLY: Purchased " + amount + r.getUnit() + " of " + r.getDisplayName() + " for $" + cost);
        if (onStateChanged != null) onStateChanged.run();
        return true;
    }
    private void triggerWin() { if (gameOver) return; gameOver = true; stopTimer(); log(">>> VICTORY: Target budget reached! Airport is thriving!"); if (onWin != null) onWin.run(); }
    private void triggerLose(String reason) { if (gameOver) return; gameOver = true; stopTimer(); log(">>> GAME OVER: " + reason); if (onLose != null) onLose.accept(reason); }
    private void log(String msg) { if (onLogMessage != null) onLogMessage.accept(msg); }
    public Queue<Aircraft> getFlightQueue() { return flightQueue; }
    public DepotManager getDepot() { return depot; }
    public Difficulty getDifficulty() { return difficulty; }
    public int getClearedFlights() { return clearedFlights; }
    public boolean isGameOver() { return gameOver; }
    public void addFlightToQueue(Aircraft ac) { flightQueue.add(ac); }
}
