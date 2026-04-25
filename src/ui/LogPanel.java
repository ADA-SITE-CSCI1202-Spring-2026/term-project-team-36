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
       
        Label title = new Label(">> DISPATCH RADIO");
        title.setFont(Font.font("Monospaced", FontWeight.BOLD, 13));
   

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
      
            "-fx-control-inner-background: #0a0a0a;" +
         
            "-fx-font-family: 'Monospaced';" +
         
        );

        getChildren().addAll(title, logArea);
    }

    public void appendMessage(String message) {
        logArea.appendText(message + "\n");
    }
}
