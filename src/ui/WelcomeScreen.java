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
     private VBox buildLogoBox() {
        Label gaa = new Label("G  A  A");
        gaa.setFont(Font.font("Monospaced", FontWeight.BOLD, 52));
        gaa.setTextFill(Color.web("#39ff14"));

        Label sub = new Label("GLOBAL AIRPORT AUTHORITY");
        sub.setFont(Font.font("Monospaced", FontWeight.BOLD, 15));
        sub.setTextFill(Color.web("#1a7a1a"));

        Label tagline = new Label("Ground Operations Dispatch System  ——  Director's Console");
        tagline.setFont(Font.font("Monospaced", 11));
        tagline.setTextFill(Color.web("#2a4a2a"));

        Label welcome = new Label("WELCOME, DIRECTOR");
        welcome.setFont(Font.font("Monospaced", FontWeight.BOLD, 17));
        welcome.setTextFill(Color.web("#a8ff78"));

       //cursor
        Timeline blink = new Timeline(
            new KeyFrame(Duration.seconds(0.8), e -> welcome.setVisible(false)),
            new KeyFrame(Duration.seconds(1.6), e -> welcome.setVisible(true))
        );
        blink.setCycleCount(Timeline.INDEFINITE);
        blink.play();

        VBox box = new VBox(6, gaa, sub, tagline, welcome);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(32, 0, 24, 0));
        box.setStyle(
            "-fx-background-color: #050505;" +
            "-fx-border-color: #1a3a1a;" +
            "-fx-border-width: 0 0 2 0;"
        );
        return box;
    }

 //tutor
    private VBox buildTutorialBox() {
        Label title = new Label(">> OPERATIONS BRIEFING");
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        title.setTextFill(Color.web("#39ff14"));

        VBox lines = new VBox(5);
        for (String line : TUTORIAL_LINES) {
            Label l = new Label(line);
            l.setFont(Font.font("Monospaced", 12));
            l.setTextFill(Color.web("#7aaa7a"));
            lines.getChildren().add(l);
        }

        VBox box = new VBox(10, title, lines);
        box.setPadding(new Insets(16));
        box.setStyle(
            "-fx-background-color: #080f08;" +
            "-fx-border-color: #1a3a1a;" +
            "-fx-border-width: 1;"
        );
        return box;
    }

  //diff
    private Label buildDifficultyLabel() {
        Label l = new Label(">> SELECT AIRPORT LOCATION");
        l.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        l.setTextFill(Color.web("#39ff14"));
        return l;
    }

