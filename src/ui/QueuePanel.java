package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Aircraft;
import model.BoundedQueue;

public class QueuePanel extends VBox {
    private final ListView<String> listView;

    public QueuePanel() {
        super(8);
        setPadding(new Insets(12));
        setStyle("-fx-background-color: #0d0d1a; -fx-border-color: #3a3a7a; -fx-border-width: 2;");

        var title = new Label(">> HOLDING PATTERN");
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        title.setTextFill(Color.web("#78aaff"));

        listView = new ListView<>();
        listView.setPrefHeight(200);
        listView.setStyle(
            "-fx-control-inner-background: #0a0a1a;" +
            "-fx-text-fill: #78aaff;" +
            "-fx-font-family: 'Monospaced';" +
            "-fx-font-size: 11px;" +
            "-fx-border-color: #1a1a3a;"
        );

        getChildren().addAll(title, listView);
    }

    public void update(BoundedQueue<Aircraft> queue) {
        listView.getItems().clear();
        for (Aircraft ac : queue) {
            listView.getItems().add(
                String.format("%-15s | Fuel: %4dL | Meals: %3d | +$%s",
                    ac.getFlightNumber(), ac.getRequiredFuel(),
                    ac.getRequiredMeals(), ac.getReward().toPlainString())
            );
        }
    }
}
