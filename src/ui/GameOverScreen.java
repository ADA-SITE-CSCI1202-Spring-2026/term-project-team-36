package ui;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import model.Difficulty;
public class GameOverScreen extends StackPane {
    public GameOverScreen(boolean victory, String reason, Difficulty difficulty, double finalBudget, int clearedFlights, Runnable onRestart) {
        String accent = victory ? "#39ff14" : "#ff4444";
        String darkBg = victory ? "#001500" : "#150000";
        String headerTx = victory ? "MISSION ACCOMPLISHED" : "OPERATIONS FAILED";
        String icon = victory ? "[ V I C T O R Y ]" : "[ G A M E   O V E R ]";
        setStyle("-fx-background-color: rgba(0,0,0,0.88);");
        setAlignment(Pos.CENTER);
        VBox card = new VBox(18);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(48, 64, 48, 64));
        card.setMaxWidth(620);
        card.setStyle("-fx-background-color: " + darkBg + "; -fx-border-color: " + accent + "; -fx-border-width: 3; -fx-effect: dropshadow(gaussian, " + accent + ", 30, 0.5, 0, 0);");
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Monospaced", FontWeight.BOLD, 22));
        iconLabel.setTextFill(Color.web(accent));
        Label header = new Label(headerTx);
        header.setFont(Font.font("Monospaced", FontWeight.BOLD, 34));
        header.setTextFill(Color.web(accent));
        Label locationLabel = new Label(difficulty.getCity() + "  |  " + difficulty.getAirport() + "  |  " + difficulty.getTier());
        locationLabel.setFont(Font.font("Monospaced", 12));
        locationLabel.setTextFill(Color.web("#555555"));
        Region sep1 = makeSep(accent);
        VBox stats = new VBox(8);
        stats.setAlignment(Pos.CENTER_LEFT);
        stats.setPadding(new Insets(0, 20, 0, 20));
        double startBudget = difficulty.getInitialBudget();
        double profit = finalBudget - startBudget;
        String profitSign = profit >= 0 ? "+" : "";
        stats.getChildren().addAll(
            statLine("Flights Cleared", clearedFlights + " aircraft dispatched", accent),
            statLine("Final Budget", "$" + String.format("%,.0f", finalBudget), accent),
            statLine("Net Profit / Loss", profitSign + "$" + String.format("%,.0f", profit), profit >= 0 ? "#39ff14" : "#ff4444"),
            statLine("Win Target", "$" + String.format("%,.0f", difficulty.getWinTargetBudget()), "#888888")
        );
        Label reasonLabel = new Label(reason);
        reasonLabel.setFont(Font.font("Monospaced", 12));
        reasonLabel.setTextFill(Color.web(victory ? "#7aaa7a" : "#aa5555"));
        reasonLabel.setWrapText(true);
        reasonLabel.setTextAlignment(TextAlignment.CENTER);
        reasonLabel.setMaxWidth(500);
        Region sep2 = makeSep(accent);
        Button restartBtn = new Button("[ RETURN TO MAIN SCREEN ]");
        restartBtn.setFont(Font.font("Monospaced", FontWeight.BOLD, 14));
        restartBtn.setStyle("-fx-background-color: " + darkBg + "; -fx-text-fill: " + accent + "; -fx-border-color: " + accent + "; -fx-border-width: 2; -fx-cursor: hand; -fx-padding: 12 32 12 32;");
        restartBtn.setOnMouseEntered(e -> restartBtn.setStyle("-fx-background-color: " + accent + "; -fx-text-fill: #000000; -fx-border-color: " + accent + "; -fx-border-width: 2; -fx-cursor: hand; -fx-padding: 12 32 12 32;"));
        restartBtn.setOnMouseExited(e -> restartBtn.setStyle("-fx-background-color: " + darkBg + "; -fx-text-fill: " + accent + "; -fx-border-color: " + accent + "; -fx-border-width: 2; -fx-cursor: hand; -fx-padding: 12 32 12 32;"));
        restartBtn.setOnAction(e -> onRestart.run());
        card.getChildren().addAll(iconLabel, header, locationLabel, sep1, stats, reasonLabel, sep2, restartBtn);
        getChildren().add(card);
        setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(500), this);
        fade.setFromValue(0); fade.setToValue(1);
        ScaleTransition scale = new ScaleTransition(Duration.millis(400), card);
        scale.setFromX(0.7); scale.setToX(1.0); scale.setFromY(0.7); scale.setToY(1.0);
        fade.play(); scale.play();
    }
    private Label statLine(String key, String value, String valueColor) {
        Label l = new Label(String.format("%-22s  %s", key + ":", value));
        l.setFont(Font.font("Monospaced", 13));
        l.setTextFill(Color.web(valueColor));
        return l;
    }
    private Region makeSep(String color) {
        Region r = new Region(); r.setPrefHeight(1);
        r.setStyle("-fx-background-color: " + color + "; -fx-opacity: 0.3;");
        return r;
    }
}
