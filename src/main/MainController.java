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
import ui.DepotPanel;
import ui.GameOverScreen;
import ui.LogPanel;
import ui.QueuePanel;
import ui.RestockPanel;

import java.io.IOException;

public class MainController extends StackPane {
    private final SimulationEngine engine;
    private final QueuePanel   queuePanel;
    private final DepotPanel   depotPanel;
    private final LogPanel     logPanel;
    private final ProgressBar  winProgress;
    private final Label        winProgressLabel;
    private final Label        queueWarningLabel;
    private final Runnable     onRestart;

    public MainController(Difficulty difficulty, Runnable onRestart) {
        this.onRestart = onRestart;

        engine = new SimulationEngine(difficulty);

        queuePanel        = new QueuePanel();
        depotPanel        = new DepotPanel();
        logPanel          = new LogPanel();
        winProgress       = new ProgressBar(0);
        winProgressLabel  = new Label();
        queueWarningLabel = new Label();

        engine.setOnLogMessage(msg  -> Platform.runLater(() -> logPanel.appendMessage(msg)));
        engine.setOnStateChanged(() -> Platform.runLater(this::refreshUI));
        engine.setOnFlightArrival(ac -> Platform.runLater(
            () -> queuePanel.update(engine.getFlightQueue())));
        engine.setOnWin(()           -> Platform.runLater(this::showWin));
        engine.setOnLose(reason      -> Platform.runLater(() -> showLose(reason)));

        var gamePane = buildGamePane(difficulty);
        getChildren().add(gamePane);

        engine.startTimer();
        refreshUI();
        logPanel.appendMessage("SYSTEM: GAA Ground Ops — ONLINE");
        logPanel.appendMessage("SYSTEM: Location — " + difficulty.getCity()
            + " | " + difficulty.getAirport());
        logPanel.appendMessage("SYSTEM: Difficulty — " + difficulty.getTier()
            + " | Flight interval: " + (int) difficulty.getSpawnIntervalSec() + "s");
        logPanel.appendMessage("SYSTEM: Win target — $"
            + String.format("%,.0f", difficulty.getWinTargetBudget())
            + " | Max queue — " + difficulty.getMaxQueueSize());
        logPanel.appendMessage("SYSTEM: Operational cost — $"
            + String.format("%.0f", difficulty.getOperationalCostPerInterval())
            + " per interval. Stay profitable!");
    }

    private BorderPane buildGamePane(Difficulty difficulty) {
        var root = new BorderPane();
        root.setStyle("-fx-background-color: #080808;");

        root.setTop(buildTopBar(difficulty));

        var clearBtn = new Button("[ CLEAR NEXT FLIGHT ]");
        clearBtn.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        clearBtn.setStyle(
            "-fx-background-color: #003a00; -fx-text-fill: #39ff14;" +
            "-fx-border-color: #2a7a2a; -fx-border-width: 1;" +
            "-fx-cursor: hand; -fx-padding: 8 20 8 20;"
        );
        clearBtn.setOnAction(e -> engine.processNextFlight());

        var saveBtn = new Button("[ SAVE STATE ]");
        saveBtn.setFont(Font.font("Monospaced", 11));
        saveBtn.setStyle(
            "-fx-background-color: #1a1a00; -fx-text-fill: #ffff00;" +
            "-fx-border-color: #5a5a00; -fx-border-width: 1; -fx-cursor: hand;"
        );

        saveBtn.setOnAction(e -> {
            try {
                SaveLoadManager.save(engine.getDepot(), engine.getFlightQueue(), engine.getDifficulty());
                logPanel.appendMessage("SAVE: State written to airport_state.csv");
            } catch (IOException ex) {
                logPanel.appendMessage("ERROR: Save failed - " + ex.getMessage());
            } finally {
                logPanel.appendMessage("SYSTEM: Save operation completed.");
            }
        });

        var loadBtn = new Button("[ LOAD STATE ]");
        loadBtn.setFont(Font.font("Monospaced", 11));
        loadBtn.setStyle(
            "-fx-background-color: #1a1a00; -fx-text-fill: #ffff00;" +
            "-fx-border-color: #5a5a00; -fx-border-width: 1; -fx-cursor: hand;"
        );

        loadBtn.setOnAction(e -> {
            try {
                SaveLoadManager.load(engine.getDepot(), engine.getFlightQueue());
                refreshUI();
                logPanel.appendMessage("LOAD: State restored from airport_state.csv");
            } catch (IOException ex) {
                logPanel.appendMessage("ERROR: Load failed - " + ex.getMessage());
            }
        });

        var menuBtn = new Button("[ MAIN MENU ]");
        menuBtn.setFont(Font.font("Monospaced", 11));
        menuBtn.setStyle(
            "-fx-background-color: #0d0d0d; -fx-text-fill: #555555;" +
            "-fx-border-color: #333333; -fx-border-width: 1; -fx-cursor: hand;"
        );
        menuBtn.setOnMouseEntered(e -> menuBtn.setStyle(
            "-fx-background-color: #1a0000; -fx-text-fill: #ff4444;" +
            "-fx-border-color: #ff4444; -fx-border-width: 1; -fx-cursor: hand;"
        ));
        menuBtn.setOnMouseExited(e -> menuBtn.setStyle(
            "-fx-background-color: #0d0d0d; -fx-text-fill: #555555;" +
            "-fx-border-color: #333333; -fx-border-width: 1; -fx-cursor: hand;"
        ));
        menuBtn.setOnAction(e -> {
            engine.stopTimer();
            onRestart.run();
        });

        var controlBar = new HBox(16, clearBtn, saveBtn, loadBtn, menuBtn);
        controlBar.setAlignment(Pos.CENTER);
        controlBar.setPadding(new Insets(10));
        controlBar.setStyle("-fx-background-color: #080808;");

        var restockPanel = new RestockPanel(r -> engine.purchaseSupply(r));

        var leftCol = new VBox(10, queuePanel, queueWarningLabel, restockPanel);
        leftCol.setPrefWidth(500);

        depotPanel.setPrefWidth(300);

        var centerArea = new HBox(10, leftCol, depotPanel);
        centerArea.setPadding(new Insets(10));
        centerArea.setStyle("-fx-background-color: #080808;");

        var bottom = new VBox(0, logPanel);
        bottom.setPadding(new Insets(0, 10, 10, 10));
        bottom.setStyle("-fx-background-color: #080808;");

        root.setCenter(new VBox(0, controlBar, centerArea, bottom));
        return root;
    }

    private VBox buildTopBar(Difficulty difficulty) {
        var appTitle = new Label("GAA — SKYWAYS AIRPORT DISPATCH TYCOON");
        appTitle.setFont(Font.font("Monospaced", FontWeight.BOLD, 16));
        appTitle.setTextFill(Color.web("#39ff14"));

        var diffLabel = new Label(
            difficulty.getCity() + "  |  " + difficulty.getAirport() +
            "  [" + difficulty.getTier() + "]" +
            "  |  flight every " + (int) difficulty.getSpawnIntervalSec() + "s"
        );
        diffLabel.setFont(Font.font("Monospaced", 11));
        diffLabel.setTextFill(Color.web(difficulty.getAccentColor()));

        winProgress.setPrefWidth(400);
        winProgress.setPrefHeight(14);
        winProgress.setStyle(
            "-fx-accent: " + difficulty.getAccentColor() + ";" +
            "-fx-background-color: #1a1a1a;" +
            "-fx-border-color: #333333; -fx-border-width: 1;"
        );

        winProgressLabel.setFont(Font.font("Monospaced", 11));
        winProgressLabel.setTextFill(Color.web(difficulty.getAccentColor()));

        var progressRow = new HBox(10, new Label("TARGET: "), winProgress, winProgressLabel);
        progressRow.setAlignment(Pos.CENTER);
        var progressRowTitle = (Label) progressRow.getChildren().get(0);
        progressRowTitle.setFont(Font.font("Monospaced", 11));
        progressRowTitle.setTextFill(Color.web("#555555"));

        var topContent = new VBox(5, appTitle, diffLabel, progressRow);
        topContent.setAlignment(Pos.CENTER);

        var topBar = new HBox(topContent);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(12));
        topBar.setStyle(
            "-fx-background-color: #050505;" +
            "-fx-border-color: #1a3a1a; -fx-border-width: 0 0 2 0;"
        );
        return new VBox(topBar);
    }

    private void refreshUI() {
        depotPanel.update(engine.getDepot());
        queuePanel.update(engine.getFlightQueue());
        updateProgressBar();
        updateQueueWarning();
    }

    private void updateProgressBar() {
        var current  = engine.getDepot().getBudgetAsDouble();
        var start    = engine.getDifficulty().getInitialBudget();
        var target   = engine.getDifficulty().getWinTargetBudget();
        var progress = Math.max(0, Math.min(1.0, (current - start) / (target - start)));
        winProgress.setProgress(progress);
        winProgressLabel.setText(String.format(
            "$%,.0f / $%,.0f  (%.0f%%)",
            current, target, progress * 100
        ));
    }

    private void updateQueueWarning() {
        var qSize   = engine.getFlightQueue().size();
        var maxSize = engine.getDifficulty().getMaxQueueSize();

        if (qSize == 0) {
            queueWarningLabel.setText("");
        } else if (qSize >= maxSize - 2) {
            queueWarningLabel.setText(
                "!! QUEUE CRITICAL: " + qSize + "/" + maxSize + " — CLEAR FLIGHTS NOW !!"
            );
            queueWarningLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 11));
            queueWarningLabel.setTextFill(Color.web("#ff4444"));
        } else {
            queueWarningLabel.setText("Queue: " + qSize + "/" + maxSize);
            queueWarningLabel.setFont(Font.font("Monospaced", 11));
            queueWarningLabel.setTextFill(Color.web("#888888"));
        }
    }

    public void loadSave() {
        try {
            SaveLoadManager.load(engine.getDepot(), engine.getFlightQueue());
            refreshUI();
            logPanel.appendMessage("LOAD: Session restored from saved state.");
        } catch (IOException ex) {
            logPanel.appendMessage("ERROR: Could not restore save — " + ex.getMessage());
        }
    }

    private void showWin() {
        var d = engine.getDifficulty();
        var screen = new GameOverScreen(
            true,
            "Budget target of $" + String.format("%,.0f", d.getWinTargetBudget()) + " reached!",
            d,
            engine.getDepot().getBudgetAsDouble(),
            engine.getStatistics(),
            onRestart
        );
        getChildren().add(screen);
    }

    private void showLose(String reason) {
        var d = engine.getDifficulty();
        var screen = new GameOverScreen(
            false,
            reason,
            d,
            engine.getDepot().getBudgetAsDouble(),
            engine.getStatistics(),
            onRestart
        );
        getChildren().add(screen);
    }
}
