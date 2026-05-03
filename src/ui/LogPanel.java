package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LogPanel extends VBox {
    private final TextArea logArea;

    public LogPanel() {
        super(8);
        setPadding(new Insets(12));
        setStyle("-fx-background-color: #0d0d0d; -fx-border-color: #2a5c2a; -fx-border-width: 2;");

        Label title = new Label(">> DISPATCH RADIO");
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        title.setTextFill(Color.web("#39ff14"));

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setPrefHeight(180);
        logArea.setStyle(
            "-fx-control-inner-background: #0a0a0a;" +
            "-fx-text-fill: #39ff14;" +
            "-fx-font-family: 'Monospaced';" +
            "-fx-font-size: 11px;" +
            "-fx-border-color: #1a3a1a;"
        );

        getChildren().addAll(title, logArea);
    }

    public void appendMessage(String message) {
        logArea.appendText(message + "\n");
    }
}
