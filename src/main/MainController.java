package main;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Difficulty;
import model.SimulationEngine;
import persistence.SaveLoadManager;
import ui.*;
public class MainController extends StackPane {
    private final SimulationEngine engine;
    private final QueuePanel queuePanel;
    private final DepotPanel depotPanel;
    private final LogPanel logPanel;
    private final ProgressBar winProgress;
    private final Label winProgressLabel;
    private final Label queueWarningLabel;
    private final Runnable onRestart;
    public MainController(Difficulty difficulty, Runnable onRestart) {
        this.onRestart = onRestart;
        engine = new SimulationEngine(difficulty);
        queuePanel = new QueuePanel();
        depotPanel = new DepotPanel();
        logPanel = new LogPanel();
        winProgress = new ProgressBar(0);
        winProgressLabel = new Label();
        queueWarningLabel = new Label();
        engine.setOnLogMessage(msg -> Platform.runLater(() -> logPanel.appendMessage(msg)));
        engine.setOnStateChanged(() -> Platform.runLater(this::refreshUI));
        engine.setOnFlightArrival(ac -> Platform.runLater(() -> queuePanel.update(engine.getFlightQueue())));
        engine.setOnWin(() -> Platform.runLater(this::showWin));
        engine.setOnLose(reason -> Platform.runLater(() -> showLose(reason)));
        BorderPane gamePane = buildGamePane(difficulty);
        getChildren().add(gamePane);
        engine.startTimer();
        refreshUI();
        logPanel.appendMessage("SYSTEM: GAA Ground Ops — ONLINE");
        logPanel.appendMessage("SYSTEM: Location — " + difficulty.getCity() + " | " + difficulty.getAirport());
        logPanel.appendMessage("SYSTEM: Difficulty — " + difficulty.getTier() + " | Flight interval: " + (int)difficulty.getSpawnIntervalSec() + "s");
        logPanel.appendMessage("SYSTEM: Win target — $" + String.format("%,.0f", difficulty.getWinTargetBudget()) + " | Max queue — " + difficulty.getMaxQueueSize());
        logPanel.appendMessage("SYSTEM: Operational cost — $" + String.format("%.0f", difficulty.getOperationalCostPerInterval()) + " per interval. Stay profitable!");
    }
    private BorderPane buildGamePane(Difficulty difficulty) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #080808;");
        root.setTop(buildTopBar(difficulty));
        Button clearBtn = new Button("[ CLEAR NEXT FLIGHT ]");
      
        clearBtn.setOnAction(e -> engine.processNextFlight());
        Button saveBtn = new Button("[ SAVE STATE ]");
       
        saveBtn.setOnAction(e -> {
            try { SaveLoadManager.save(engine.getDepot(), engine.getFlightQueue()); logPanel.appendMessage("SAVE: State written to airport_state.csv"); }
            catch (Exception ex) { logPanel.appendMessage("ERROR: Save failed - " + ex.getMessage()); }
        });
       
        loadBtn.setStyle("-fx-background-color: #1a5a00; -fx-text-fill: #fhff00; -fx-border-color: #5a5a00; -fx-border-width: 1; -fx-cursor: hand;");
        loadBtn.setOnAction(e -> {
            try { SaveLoadManager.load(engine.getDepot(), engine.getFlightQueue()); refreshUI(); logPanel.appendMessage("LOAD: State restored from airport_state.csv"); }
            catch (Exception ex) { logPanel.appendMessage("ERROR: Load failed - " + ex.getMessage()); }
        });
