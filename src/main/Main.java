###put skeleton for welcome screen

package main;
public class Main {
    private Stage primaryStage;
    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("GAA — Skyways Airport Dispatch Tycoon");
        stage.setMinWidth(860);
        stage.setMinHeight(640);
        showWelcomeScreen();
        stage.show();
    }
    private void showWelcomeScreen() {
        WelcomeScreen welcome = new WelcomeScreen(this::launchGame);
        primaryStage.setScene(new Scene(welcome, 900, 720));
    }
    private void launchGame(Difficulty difficulty) {
        MainController game = new MainController(difficulty, this::showWelcomeScreen);
        primaryStage.setScene(new Scene(game, 960, 740));
        primaryStage.setTitle("GAU — " + difficulty.getCity() + " | " + difficulty.getAirport() + "  [" + difficulty.getTier() + "]");
    }
    public static void main(String[] args) { launch(args); }
}
