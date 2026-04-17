package ui;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import model.Difficulty;

import java.util.function.Consumer;

public class WelcomeScreen extends BorderPane {

    private Difficulty selectedDifficulty = null;
    private Button beginBtn;

    private static final String[] TUTORIAL_LINES = {
        "01  Aircraft arrive in the HOLDING PATTERN every few seconds.",
        "02  Click [ CLEAR NEXT FLIGHT ] to service and dispatch.",
        "03  Each aircraft requires Jet Fuel and/or Meals from the DEPOT.",
        "04  Depot resources DEPLETE with every cleared flight.",
        "05  Use SUPPLY REQUISITION to restock before you run dry.",
        "06  If resources are insufficient — flight is REJECTED. No refund.",
        "07  Earn revenue by clearing flights. Keep the airport profitable.",
        "08  Save your session anytime. Load it to resume operations.",
    };

    public WelcomeScreen(Consumer<Difficulty> onStart) {
        setStyle("-fx-background-color: #050505;");

        // лого
        VBox logoBox = buildLogoBox();
        setTop(logoBox);

      //цент
        VBox center = new VBox(28);
        center.setPadding(new Insets(20, 60, 20, 60));
        center.setAlignment(Pos.TOP_CENTER);

        center.getChildren().addAll(
            buildTutorialBox(),
            buildSeparator(),
            buildDifficultyLabel(),
            buildDifficultyRow(),
            buildSeparator(),
            buildBeginArea(onStart)
        );

        ScrollPane scroll = new ScrollPane(center);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: #050505; -fx-background-color: #050505; -fx-border-color: transparent;");

        setCenter(scroll);

        // низ
        Label ver = new Label("GAA GROUND OPS SYSTEM  v1.0  |  CSCI 1202  |  SKYWAYS INTL");
        ver.setFont(Font.font("Monospaced", 9));
        ver.setTextFill(Color.web("#333333"));
        HBox bottom = new HBox(ver);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(8));
        bottom.setStyle("-fx-background-color: #080808; -fx-border-color: #1a1a1a; -fx-border-width: 1 0 0 0;");
        setBottom(bottom);
    }
