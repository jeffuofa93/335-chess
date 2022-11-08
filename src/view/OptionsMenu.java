package view;

import controller.ChessController;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
/**
 * This class will create an Option menu that enables users to
 * load/save a game and create a new game.
 */
public class OptionsMenu extends Stage {

    private ChessController controller;
    private VBox networkBox = new VBox();
    private RadioButton competitive;
    private boolean competitiveMode = false;

    /**
     * The constructor will be initialized from the view (ChessGUIView).
     *
     * @param controller    the game controller (ChessController)
     */
    public OptionsMenu(ChessController controller) {
        this.controller = controller;
        this.setTitle("Game Options");
        this.initModality(Modality.APPLICATION_MODAL);
        setOptions();
    }

    /**
     * This will create/display a VBox with network features.
     */
    private void setOptions() {
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);
        networkBox = new VBox();
        networkBox.setSpacing(14);
        createButtonRow();
        borderPane.setCenter(networkBox);
        this.setScene(scene);
        setStage();
        BorderPane.setMargin(networkBox, new Insets(8, 8, 8, 8));
    }

    /**
     * This method will add a row of buttons (Load, Save, New) to the
     * menu window.
     */
    private void createButtonRow() {
        HBox buttonRow1 = new HBox();
        buttonRow1.setSpacing(8);
        HBox buttonRow2 = new HBox();
        buttonRow2.setSpacing(8);
        Button loadGame = new Button("Load Game");
        setButton(loadGame);
        // call method to notify controller
        loadGame.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Saved Game File");
            File gameFile = fileChooser.showOpenDialog(this);
            controller.loadGame(gameFile, true);
            this.close();
        });

        Button newGame = new Button("New Game");
        setButton(newGame);
        newGame.setOnAction(actionEvent -> {
            controller.loadGame(null, true);
            this.close();
        });

        Button saveGame = new Button("Save Game");
        setButton(saveGame);
        saveGame.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save A Game File");
            String gameData = controller.saveGame();
            File saveFile = fileChooser.showSaveDialog(this);
            if (saveFile != null) {
                PrintWriter writer = null;
                try {
                    writer = new PrintWriter(saveFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                writer.flush();
                writer.println(gameData);
                writer.close();
            }

            this.close();
        });

        buttonRow1.getChildren().addAll(newGame, saveGame, loadGame);

        Button startServer = new Button("Start as Server");
        Button startClient = new Button("Start as Client");
        setButton(startServer);
        setButton(startClient);
        startServer.setOnAction(event -> {
            startClient.setDisable(true);
            startServer.setDisable(true);
            controller.startServer();
            controller.loadGame(null, true);
            this.close();
        });
        startClient.setOnAction(event -> {
            startClient.setDisable(true);
            startServer.setDisable(true);
            controller.startClient();
            controller.loadGame(null, false);
            this.close();
        });
        buttonRow2.getChildren().addAll(startServer, startClient);
        competitive = new RadioButton("Competitive Mode");
        competitive.setSelected(false);
        competitive.setOnAction(actionEvent -> competitiveMode = competitive.isSelected());
        networkBox.getChildren().addAll(buttonRow1, buttonRow2, competitive);
    }

    /**
     * Set the dimensions for the button.
     *
     * @param button    a Button object that needs dimensions set
     */
    private void setButton(Button button) {
        button.setMaxWidth(150);
        button.setMinWidth(150);
    }


    /**
     * This method sets the dimensions for the stage
     */
    private void setStage() {
        this.setMaxWidth(500);
        this.setMinWidth(500);
        this.setMaxHeight(150);
        this.setMinHeight(150);
    }

    /**
     * This is a getter method to determine if the game is
     * in competitive mode (uses a timer for each player's moves)
     *
     * @return a boolean, true if in competitive mode, else false
     */
    public boolean isCompetitiveMode() {
        return competitiveMode;
    }
}