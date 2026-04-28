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

       
        VBox logoBox = buildLogoBox();
        setTop(logoBox);

        
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

 
    private Label buildDifficultyLabel() {
        Label l = new Label(">> SELECT AIRPORT LOCATION");
        l.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        l.setTextFill(Color.web("#39ff14"));
        return l;
    }

    
    private HBox buildDifficultyRow() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER);

        for (Difficulty d : Difficulty.values()) {
            VBox card = buildCityCard(d);
            row.getChildren().add(card);
        }
        return row;
    }

    private VBox buildCityCard(Difficulty d) {
        String accent = d.getAccentColor();
        String bg     = d.getBgColor();

        Label city = new Label(d.getCity());
        city.setFont(Font.font("Monospaced", FontWeight.BOLD, 22));
        city.setTextFill(Color.web(accent));

        Label airport = new Label(d.getAirport());
        airport.setFont(Font.font("Monospaced", 10));
        airport.setTextFill(Color.web("#888888"));

        Label tier = new Label("[ " + d.getTier() + " ]");
        tier.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
        tier.setTextFill(Color.web(accent));

        Label interval = new Label("Flight interval: " + (int) d.getSpawnIntervalSec() + "s");
        interval.setFont(Font.font("Monospaced", 11));
        interval.setTextFill(Color.web("#888888"));

        Label budget = new Label("Budget:  $" + String.format("%,.0f", d.getInitialBudget()));
        budget.setFont(Font.font("Monospaced", 11));
        budget.setTextFill(Color.web("#888888"));

        Label fuel = new Label("Fuel:    " + d.getInitialFuel() + "L");
        fuel.setFont(Font.font("Monospaced", 11));
        fuel.setTextFill(Color.web("#888888"));

        Label meals = new Label("Meals:   " + d.getInitialMeals() + " units");
        meals.setFont(Font.font("Monospaced", 11));
        meals.setTextFill(Color.web("#888888"));

        Label desc = new Label(d.getDescription());
        desc.setFont(Font.font("Monospaced", 10));
        desc.setTextFill(Color.web("#555555"));
        desc.setWrapText(true);
        desc.setMaxWidth(200);
        desc.setTextAlignment(TextAlignment.CENTER);

        VBox card = new VBox(8, city, airport, tier, interval, budget, fuel, meals, desc);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(210);
        card.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-border-color: #333333;" +
            "-fx-border-width: 1;" +
            "-fx-cursor: hand;"
        );

      
        card.setOnMouseClicked(e -> selectCard(d, card, accent, bg));

       
        card.setOnMouseEntered(e -> {
            if (selectedDifficulty != d) {
                card.setStyle(
                    "-fx-background-color: " + darken(bg) + ";" +
                    "-fx-border-color: " + accent + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-cursor: hand;"
                );
            }
        });
        card.setOnMouseExited(e -> {
            if (selectedDifficulty != d) {
                card.setStyle(
                    "-fx-background-color: " + bg + ";" +
                    "-fx-border-color: #333333;" +
                    "-fx-border-width: 1;" +
                    "-fx-cursor: hand;"
                );
            }
        });

        return card;
    }

    private void selectCard(Difficulty d, VBox clickedCard, String accent, String bg) {
        selectedDifficulty = d;

      
        HBox row = (HBox) clickedCard.getParent();
        for (int i = 0; i < row.getChildren().size(); i++) {
            VBox c = (VBox) row.getChildren().get(i);
            Difficulty cd = Difficulty.values()[i];
            c.setStyle(
                "-fx-background-color: " + cd.getBgColor() + ";" +
                "-fx-border-color: #333333;" +
                "-fx-border-width: 1;" +
                "-fx-cursor: hand;"
            );
        }

    
        clickedCard.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-border-color: " + accent + ";" +
            "-fx-border-width: 3;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, " + accent + ", 12, 0.4, 0, 0);"
        );

        beginBtn.setDisable(false);
        beginBtn.setStyle(
            "-fx-background-color: #003a00;" +
            "-fx-text-fill: #39ff14;" +
            "-fx-border-color: " + accent + ";" +
            "-fx-border-width: 2;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 12 40 12 40;" +
            "-fx-effect: dropshadow(gaussian, " + accent + ", 10, 0.5, 0, 0);"
        );
        beginBtn.setText("[ BEGIN OPERATIONS — " + d.getCity() + " ]");
    }

 
    private VBox buildBeginArea(Consumer<Difficulty> onStart) {
        beginBtn = new Button("[ SELECT A LOCATION ABOVE ]");
        beginBtn.setFont(Font.font("Monospaced", FontWeight.BOLD, 14));
        beginBtn.setStyle(
            "-fx-background-color: #111111;" +
            "-fx-text-fill: #333333;" +
            "-fx-border-color: #333333;" +
            "-fx-border-width: 2;" +
            "-fx-padding: 12 40 12 40;"
        );
        beginBtn.setDisable(true);

        beginBtn.setOnAction(e -> {
            if (selectedDifficulty != null) {
                FadeTransition ft = new FadeTransition(Duration.millis(400), this);
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
                ft.setOnFinished(ev -> onStart.accept(selectedDifficulty));
                ft.play();
            }
        });

        VBox box = new VBox(16, beginBtn);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10, 0, 30, 0));
        return box;
    }

  
    private Region buildSeparator() {
        Region r = new Region();
        r.setPrefHeight(1);
        r.setStyle("-fx-background-color: #1a2a1a;");
        return r;
    }

    private String darken(String hex) {
      
        return hex.replace("00", "11");
    }
}
