package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Resource;

import java.util.function.Consumer;

public class RestockPanel extends VBox {

    private final ComboBox<Resource> resourceDropdown;

    public RestockPanel(Consumer<Resource> onPurchase) {
        super(10);
        setPadding(new Insets(12));
        setStyle("-fx-background-color: #1a0d0d; -fx-border-color: #7a3a3a; -fx-border-width: 2;");

        Label title = new Label(">> SUPPLY REQUISITION");
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        title.setTextFill(Color.web("#ff7878"));

        Label hint = new Label("Jet Fuel: +500L for $2,000  |  Meals: +50 units for $500");
        hint.setFont(Font.font("Monospaced", 10));
        hint.setTextFill(Color.web("#aa5555"));

        resourceDropdown = new ComboBox<>();
        resourceDropdown.getItems().addAll(Resource.values());
        resourceDropdown.setValue(Resource.JET_FUEL);
        resourceDropdown.setStyle(
            "-fx-background-color: #2a0000;" +
            "-fx-text-fill: #ff7878;" +
            "-fx-font-family: 'Monospaced';"
        );

        Button purchaseBtn = new Button("[ PURCHASE CARGO ]");
        purchaseBtn.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
        purchaseBtn.setStyle(
            "-fx-background-color: #3a0000;" +
            "-fx-text-fill: #ff7878;" +
            "-fx-border-color: #7a3a3a;" +
            "-fx-border-width: 1;" +
            "-fx-cursor: hand;"
        );
        purchaseBtn.setOnAction(e -> onPurchase.accept(resourceDropdown.getValue()));

        HBox row = new HBox(10, resourceDropdown, purchaseBtn);
        getChildren().addAll(title, hint, row);
    }
}
