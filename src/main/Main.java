package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Difficulty;
import ui.WelcomeScreen;

public class Main extends Application {
    private Stage primaryStage;

    private static Difficulty cmdLineDifficulty = null;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("GAA — Skyways Airport Dispatch Tycoon");
        stage.setMinWidth(860);
        stage.setMinHeight(640);

        if (cmdLineDifficulty != null) {
            launchGame(cmdLineDifficulty);
        } else {
            showWelcomeScreen();
        }
        stage.show();
    }

    private void showWelcomeScreen() {
        var welcome = new WelcomeScreen(this::launchGame);
        primaryStage.setScene(new Scene(welcome, 900, 720));
    }

    private void launchGame(Difficulty difficulty) {
        var game = new MainController(difficulty, this::showWelcomeScreen);
        primaryStage.setScene(new Scene(game, 960, 740));

        primaryStage.setTitle(
            "GAA — " + difficulty.getCity() +
            " | " + difficulty.getAirport() +
            "  [" + difficulty.getTier() + "]"
        );
    }

    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("--difficulty=") || arg.startsWith("--city=")) {
                var value = arg.substring(arg.indexOf('=') + 1).toUpperCase();

                cmdLineDifficulty = switch (value) {
                    case "EASY", "BAKU"     -> Difficulty.BAKU;
                    case "MEDIUM", "MOSCOW" -> Difficulty.MOSCOW;
                    case "HARD", "TOKYO"    -> Difficulty.TOKYO;
                    default -> {
                        System.err.println("Unknown difficulty: " + value
                            + ". Launching welcome screen.");
                        yield null;
                    }
                };
            }
        }
        launch(args);
    }
}
