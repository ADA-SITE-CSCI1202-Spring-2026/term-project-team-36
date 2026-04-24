package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.DepotManager;
import model.Resource;

public class DepotPanel extends VBox {

    private final Label budgetLabel;
    private final Label fuelLabel;
    private final Label mealsLabel;

    public DepotPanel() {
        super(10);
        setPadding(new Insets(12));
        setStyle("-fx-background-color: #0d1a0d; -fx-border-color: #3a7a3a; -fx-border-width: 2;");

        Label title = new Label(">> TERMINAL DEPOT");
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        title.setTextFill(Color.web("#39ff14"));

        budgetLabel = makeStatLabel("BUDGET: $50,000.00");
        fuelLabel   = makeStatLabel("JET FUEL: 3000 L");
        mealsLabel  = makeStatLabel("IN-FLIGHT MEALS: 250 units");

        getChildren().addAll(title, budgetLabel, fuelLabel, mealsLabel);
    }

    private Label makeStatLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Monospaced", 12));
        l.setTextFill(Color.web("#a8ff78"));
        return l;
    }

    public void update(DepotManager depot) {
        budgetLabel.setText(String.format("BUDGET:          $%.2f", depot.getBudget()));
        fuelLabel.setText(String.format("JET FUEL:        %d L", depot.getResource(Resource.JET_FUEL)));
        mealsLabel.setText(String.format("IN-FLIGHT MEALS: %d units", depot.getResource(Resource.MEALS)));
    }
}
