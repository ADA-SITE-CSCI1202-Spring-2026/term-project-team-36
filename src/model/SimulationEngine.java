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

public class SimulationEngine implements Runnable {
    private final BoundedQueue<Aircraft> flightQueue;
    private final DepotManager depot;

    private final List<IGroundService> groundCrews;
    private final Difficulty difficulty;

    private final FlightStatistics statistics;

    private final AirportLogger logger;
    private final Random random = new Random();

    private volatile boolean gameOver = false;
    private Thread simulationThread;

    private int flightCounter = 100;

    private Consumer<Aircraft> onFlightArrival;
    private Consumer<String>   onLogMessage;
    private Runnable           onStateChanged;
    private Runnable           onWin;
    private Consumer<String>   onLose;

    private static final String[] AIRLINE_PREFIXES =
        {"UA", "SW", "DL", "AA", "SK", "FX", "PV", "BA", "LH", "QF"};

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

        groundCrews = new ArrayList<>();
        groundCrews.add(new FuelingTruck());
        groundCrews.add(new CateringVan());
        groundCrews.add(new BaggageHandler());
    }

    public void setOnFlightArrival(Consumer<Aircraft> h) { this.onFlightArrival = h; }
    public void setOnLogMessage(Consumer<String> h)      { this.onLogMessage = h; }
    public void setOnStateChanged(Runnable h)            { this.onStateChanged = h; }
    public void setOnWin(Runnable h)                     { this.onWin = h; }
    public void setOnLose(Consumer<String> h)            { this.onLose = h; }

    public void startTimer() {
        simulationThread = new Thread(this, "AirportSimulation");
        simulationThread.setDaemon(true);
        simulationThread.start();
    }

    public void stopTimer() {
        gameOver = true;
        if (simulationThread != null) {
            simulationThread.interrupt();
        }
    }

    @Override
    public void run() {
        while (!gameOver) {
            try {
                Thread.sleep((long) (difficulty.getSpawnIntervalSec() * 1000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (gameOver) break;

            synchronized (this) {
                var opCost = BigDecimal.valueOf(difficulty.getOperationalCostPerInterval());
                depot.addBudget(opCost.negate());
                log("OPS: Ground crew salary deducted: -$"
                    + String.format("%.0f", difficulty.getOperationalCostPerInterval()));

                if (depot.getBudget().compareTo(BigDecimal.ZERO) < 0) {
                    triggerLose("BANKRUPTCY — operational costs exceeded all revenue.");
                    return;
                }

                var ac = generateRandomFlight();
                flightQueue.offer(ac);
                log("WARNING: New Arrival - " + ac.getType() + " " + ac.getFlightNumber());

                if (onFlightArrival != null) onFlightArrival.accept(ac);
                if (onStateChanged != null)  onStateChanged.run();

                if (flightQueue.isFull()) {
                    triggerLose("QUEUE OVERFLOW — holding pattern exceeded "
                        + difficulty.getMaxQueueSize() + " aircraft. Airspace collapsed.");
                    return;
                }
            }
        }
    }

    public Aircraft generateRandomFlight() {
        var prefix = AIRLINE_PREFIXES[random.nextInt(AIRLINE_PREFIXES.length)];

        var number = prefix + "-" + (++flightCounter);

        return switch (random.nextInt(3)) {
            case 0  -> new CommercialJet(number);
            case 1  -> new CargoFreighter(number);
            default -> new PrivateCharter(number);
        };
    }

    public synchronized boolean processNextFlight() {
        if (gameOver) return false;

        var aircraft = flightQueue.poll();
        if (aircraft == null) {
            log("INFO: No flights in holding pattern.");
            return false;
        }

        var required = aircraft.getRequiredResources();

        if (!depot.hasResources(required)) {
            var missing = new StringBuilder();
            for (var entry : required.entrySet()) {
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

        for (var crew : groundCrews) {
            if (crew.canProcess(aircraft)) {
                crew.serviceFlight(aircraft);
                log("DISPATCH: " + crew.getServiceType()
                    + " servicing " + aircraft.getFlightNumber());
            }
        }

        depot.consumeResources(required);
        depot.addBudget(aircraft.getReward());

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

        if (depot.getBudget().compareTo(
                BigDecimal.valueOf(difficulty.getWinTargetBudget())) >= 0) {
            triggerWin();
        }

        return true;
    }

    public synchronized boolean purchaseSupply(Resource r) {
        if (gameOver) return false;

        var amount = r.getDefaultRestockAmount();
        var cost = BigDecimal.valueOf(r.getRestockCost());

        if (!depot.canAfford(cost)) {
            log("ERROR: Insufficient budget to purchase "
                + r.getDisplayName() + "! Need $" + cost);
            return false;
        }

        depot.restockResource(r, amount, cost);

        var order = new SupplyOrder(r, amount, cost, LocalDateTime.now());
        log("SUPPLY: " + order.toLogString());

        if (onStateChanged != null) onStateChanged.run();
        return true;
    }

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

    private void log(String msg) {
        logger.append(msg);
        if (onLogMessage != null) onLogMessage.accept(msg);
    }

    public BoundedQueue<Aircraft> getFlightQueue()  { return flightQueue; }
    public DepotManager           getDepot()        { return depot; }
    public Difficulty              getDifficulty()   { return difficulty; }
    public int                     getClearedFlights() { return statistics.totalCleared(); }
    public boolean                 isGameOver()      { return gameOver; }
    public FlightStatistics        getStatistics()   { return statistics; }
    public AirportLogger           getLogger()       { return logger; }

    public synchronized GameState snapshot() {
        return new GameState(depot.getBudget(), flightQueue.size(), getClearedFlights());
    }

    public void addFlightToQueue(Aircraft ac) {
        flightQueue.offer(ac);
    }
}
