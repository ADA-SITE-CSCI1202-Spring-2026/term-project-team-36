package model;

import service.BaggageHandler;
import service.CateringVan;
import service.FuelingTruck;
import service.IGroundService;
import util.AirportLogger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

// Week 13: Implements Runnable for background thread execution
// Week 6:  Contains a nested static class (GameState)
public class SimulationEngine implements Runnable {

    // Week 10: Generic class usage
    private final BoundedQueue<Aircraft> flightQueue;
    private final DepotManager depot;
    // Week 8: ArrayList of polymorphic services
    private final List<IGroundService> groundCrews;
    private final Difficulty difficulty;
    // Week 11: Stream-based statistics
    private final FlightStatistics statistics;
    // Week 4: StringBuilder-based logger
    private final AirportLogger logger;
    private final Random random = new Random();

    // Week 13: volatile for thread visibility across simulation and UI threads
    private volatile boolean gameOver = false;
    private Thread simulationThread;

    // Week 5: Static member — shared flight counter
    private static int globalFlightCounter = 100;
    private int flightCounter = globalFlightCounter;

    // Week 11: Functional interfaces as event callbacks (Consumer, Runnable)
    private Consumer<Aircraft> onFlightArrival;
    private Consumer<String>   onLogMessage;
    private Runnable           onStateChanged;
    private Runnable           onWin;
    private Consumer<String>   onLose;

    // Week 4: Array of airline prefixes
    private static final String[] AIRLINE_PREFIXES =
        {"UA", "SW", "DL", "AA", "SK", "FX", "PV", "BA", "LH", "QF"};

    // ── Week 6: Nested static class — immutable game state snapshot ──────
    public static class GameState {
        private final BigDecimal budget;
        private final int queueSize;
        private final int clearedFlights;

        public GameState(BigDecimal budget, int queueSize, int clearedFlights) {
            this.budget = budget;
            this.queueSize = queueSize;
            this.clearedFlights = clearedFlights;
        }

        public BigDecimal getBudget()      { return budget; }
        public int getQueueSize()          { return queueSize; }
        public int getClearedFlights()     { return clearedFlights; }
    }

    // ── Constructor ──────────────────────────────────────────────────────
    public SimulationEngine(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.flightQueue = new BoundedQueue<>(difficulty.getMaxQueueSize());
        this.depot = new DepotManager(
            difficulty.getInitialBudget(),
            difficulty.getInitialFuel(),
            difficulty.getInitialMeals()
        );
        this.statistics = new FlightStatistics();
        this.logger = new AirportLogger(50);

        // Week 6: Polymorphism — different IGroundService implementations in one list
        groundCrews = new ArrayList<>();
        groundCrews.add(new FuelingTruck());
        groundCrews.add(new CateringVan());
        groundCrews.add(new BaggageHandler());
    }

    // ── Callback setters ─────────────────────────────────────────────────
    public void setOnFlightArrival(Consumer<Aircraft> h) { this.onFlightArrival = h; }
    public void setOnLogMessage(Consumer<String> h)      { this.onLogMessage = h; }
    public void setOnStateChanged(Runnable h)            { this.onStateChanged = h; }
    public void setOnWin(Runnable h)                     { this.onWin = h; }
    public void setOnLose(Consumer<String> h)            { this.onLose = h; }

    // ── Week 13: Start simulation in a daemon thread ─────────────────────
    public void startTimer() {
        simulationThread = new Thread(this, "AirportSimulation");
        simulationThread.setDaemon(true);  // Exits when main app closes
        simulationThread.start();
    }

    public void stopTimer() {
        gameOver = true;
        if (simulationThread != null) {
            simulationThread.interrupt();  // Week 13: Wake thread from sleep
        }
    }

    // ── Week 13: Runnable.run() — background simulation loop ─────────────
    @Override
    public void run() {
        while (!gameOver) {
            try {
                // Week 13: Thread.sleep for timed intervals
                Thread.sleep((long) (difficulty.getSpawnIntervalSec() * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (gameOver) break;

            // Week 13: synchronized block — thread-safe state modification
            synchronized (this) {
                // Operational cost deducted each interval
                var opCost = BigDecimal.valueOf(difficulty.getOperationalCostPerInterval());
                depot.addBudget(opCost.negate());
                log("OPS: Ground crew salary deducted: -$"
                    + String.format("%.0f", difficulty.getOperationalCostPerInterval()));

                // Bankrupt check
                if (depot.getBudget().compareTo(BigDecimal.ZERO) < 0) {
                    triggerLose("BANKRUPTCY — operational costs exceeded all revenue.");
                    return;
                }

                // Spawn new flight
                var ac = generateRandomFlight();  // Week 2: var
                flightQueue.offer(ac);
                log("WARNING: New Arrival - " + ac.getType() + " " + ac.getFlightNumber());

                if (onFlightArrival != null) onFlightArrival.accept(ac);
                if (onStateChanged != null)  onStateChanged.run();

                // Queue overflow check
                if (flightQueue.isFull()) {
                    triggerLose("QUEUE OVERFLOW — holding pattern exceeded "
                        + difficulty.getMaxQueueSize() + " aircraft. Airspace collapsed.");
                    return;
                }
            }
        }
    }

    // ── Flight generation ────────────────────────────────────────────────
    public Aircraft generateRandomFlight() {
        // Week 4: Array access
        var prefix = AIRLINE_PREFIXES[random.nextInt(AIRLINE_PREFIXES.length)];
        // Week 4: String concatenation
        var number = prefix + "-" + (++flightCounter);
        // Week 2: Switch expression with arrow syntax
        return switch (random.nextInt(3)) {
            case 0  -> new CommercialJet(number);
            case 1  -> new CargoFreighter(number);
            default -> new PrivateCharter(number);
        };
    }

    // ── Week 13: synchronized — process next flight (called from UI thread) ──
    public synchronized boolean processNextFlight() {
        if (gameOver) return false;

        var aircraft = flightQueue.poll();  // Week 2: var
        if (aircraft == null) {
            log("INFO: No flights in holding pattern.");
            return false;
        }

        var required = aircraft.getRequiredResources();

        if (!depot.hasResources(required)) {
            // Week 4: StringBuilder for building error message
            var missing = new StringBuilder();
            for (var entry : required.entrySet()) {  // Week 4: for-each
                if (depot.getResource(entry.getKey()) < entry.getValue()) {
                    missing.append(entry.getKey().getDisplayName())
                           .append(" (need ").append(entry.getValue())
                           .append(", have ").append(depot.getResource(entry.getKey())).append(") ");
                }
            }
            log("ERROR: Cannot clear " + aircraft.getFlightNumber()
                + " — Insufficient: " + missing.toString().trim() + "!");
            if (onStateChanged != null) onStateChanged.run();
            return false;
        }

        // Week 6: Polymorphism — iterate services, each handles aircraft differently
        for (var crew : groundCrews) {
            if (crew.canProcess(aircraft)) {
                crew.serviceFlight(aircraft);
                log("DISPATCH: " + crew.getServiceType()
                    + " servicing " + aircraft.getFlightNumber());
            }
        }

        depot.consumeResources(required);
        depot.addBudget(aircraft.getReward());

        // Week 6: Record — create immutable flight event for statistics
        var event = new FlightEvent(
            aircraft.getFlightNumber(),
            aircraft.getType(),
            aircraft.getReward(),
            LocalDateTime.now()
        );
        statistics.record(event);

        log("CLEARED: " + aircraft.getFlightNumber()
            + " departed. Revenue: +$" + aircraft.getReward().toPlainString()
            + "  |  Budget: $" + String.format("%,.0f", depot.getBudget()));

        if (onStateChanged != null) onStateChanged.run();

        // Win check
        if (depot.getBudget().compareTo(
                BigDecimal.valueOf(difficulty.getWinTargetBudget())) >= 0) {
            triggerWin();
        }

        return true;
    }

    // ── Week 13: synchronized — purchase supplies (called from UI thread) ──
    public synchronized boolean purchaseSupply(Resource r) {
        if (gameOver) return false;

        // Week 2: Switch expression within enum for amounts and costs
        var amount = r.getDefaultRestockAmount();
        var cost = BigDecimal.valueOf(r.getRestockCost());

        if (!depot.canAfford(cost)) {
            log("ERROR: Insufficient budget to purchase "
                + r.getDisplayName() + "! Need $" + cost);
            return false;
        }

        depot.restockResource(r, amount, cost);

        // Week 6: Record for supply order tracking
        var order = new SupplyOrder(r, amount, cost, LocalDateTime.now());

        log("SUPPLY: Purchased " + amount + r.getUnit()
            + " of " + r.getDisplayName() + " for $" + cost);

        if (onStateChanged != null) onStateChanged.run();
        return true;
    }

    // ── Win / Lose triggers ──────────────────────────────────────────────
    private void triggerWin() {
        if (gameOver) return;
        gameOver = true;
        stopTimer();
        log(">>> VICTORY: Target budget reached! Airport is thriving!");
        if (onWin != null) onWin.run();
    }

    private void triggerLose(String reason) {
        if (gameOver) return;
        gameOver = true;
        log(">>> GAME OVER: " + reason);
        if (onLose != null) onLose.accept(reason);
    }

    // ── Logging ──────────────────────────────────────────────────────────
    private void log(String msg) {
        logger.append(msg);
        if (onLogMessage != null) onLogMessage.accept(msg);
    }

    // ── Getters ──────────────────────────────────────────────────────────
    public BoundedQueue<Aircraft> getFlightQueue()  { return flightQueue; }
    public DepotManager           getDepot()        { return depot; }
    public Difficulty              getDifficulty()   { return difficulty; }
    public int                     getClearedFlights() { return statistics.totalCleared(); }
    public boolean                 isGameOver()      { return gameOver; }
    public FlightStatistics        getStatistics()   { return statistics; }
    public AirportLogger           getLogger()       { return logger; }

    // Week 6: Returns nested class instance — snapshot of current game state
    public synchronized GameState snapshot() {
        return new GameState(depot.getBudget(), flightQueue.size(), getClearedFlights());
    }

    public void addFlightToQueue(Aircraft ac) {
        flightQueue.offer(ac);
    }
}
