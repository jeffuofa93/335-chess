package view;

import controller.ChessController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.ChessModel;
import model.ChessMoveMessage;

import java.io.IOException;
import java.util.*;


/**
 * This class will enable the users to click on a GUI to play the game.
 * This will serve as the users primary way of interacting with the program.
 * It will display the board and it pieces. Additionally, it will enable user
 * to make use of the various features (save/load game, competitive mode, etc.).
 */
public class ChessGUIView extends Application implements Observer {
    private final GridPane grid = new GridPane();
    private ChessController controller;
    private static final int WIDTH_HEIGHT = 8;
    private final Map<compoundKey, Label> nodeMap = new HashMap<>();
    private final Map<compoundKey, VBox> boxMap = new HashMap<>();
    private Set<compoundKey> movedSquares = new HashSet<>();
    private final List<String> horizontalLabels = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H"
    ));
    private final List<String> verticalLabels = new ArrayList<>(Arrays.asList("8", "7", "6", "5", "4", "3", "2", "1"));
    private OptionsMenu optionsMenu;
    private boolean firstClick = false;
    private compoundKey firstClickCoords;
    private compoundKey secondClickCoords;
    private boolean canClick = true;
    private VBox timerBox;
    private Timeline timelineWhite;
    private final Label timerLabelWhite = new Label();
    private final DoubleProperty timeWhite = new SimpleDoubleProperty();
    private Timeline timelineBlack;
    private final Label timerLabelBlack = new Label();
    private final DoubleProperty timeBlack = new SimpleDoubleProperty();
    private final int timeControl = 180;
    private Stage stage;


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This method is required as a result of the class extending Application.
     * This method will position the board, pieces, menus, etc. so the users
     * can interact with the game as a GUI. Both the model and controller
     * are initialized in this method. Additionally, the GUI is set as
     * an observer of the model to ensure the game is updated and displayed
     * properly.
     *
     * @param stage         a window containing all the objects in the JavaFX program
     * @throws IOException  throw exception if start(stage) fails
     */
    @Override
    public void start(Stage stage) throws IOException {
        ChessModel model = new ChessModel();
        controller = new ChessController(model);
        model.addObserver(this);
        optionsMenu = new OptionsMenu(controller);
        BorderPane window = new BorderPane();
        Scene scene = new Scene(window);
        MenuBar menuBar = createMenu(stage);
        window.setTop(menuBar);
        setTimers();
        window.setLeft(timerBox);
        BorderPane.setMargin(timerBox, new Insets(0, 0, 0, 50));
        setGrid();
        setCenterPane(window);
        setStage(stage);
        scene.getStylesheets().add("style.css");
        this.stage = stage;
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Create the MenuBar to hold the New Game, Load Game, Save Game, etc. options.
     *
     * @param stage     the main Stage to set up
     * @return menuBar  gives players access to various game features
     */
    private MenuBar createMenu(Stage stage) {
        MenuBar menuBar = new MenuBar();
        Menu menu = new Menu("File");
        MenuItem newGame = new MenuItem("Menu");
        newGame.setOnAction(actionEvent -> optionsMenu.showAndWait());
        menu.getItems().add(newGame);
        menuBar.getMenus().add(menu);
        return menuBar;
    }

    /**
     * This method will set the dimensions for the stage.
     *
     * @param stage     the stage whose dimensions need to be set
     */
    private void setStage(Stage stage) {
        stage.setMaxWidth(910);
        stage.setMinWidth(910);
        stage.setMaxHeight(940);
        stage.setMinHeight(940);
    }

    /**
     * This method will initialize the timers that are used in competitive mode.
     * The players must ensure they do not run out the clock while making their
     * moves, respectively, else the game will end.
     */
    private void setTimers() {
        setTimerLabel(true, timerLabelWhite, timeWhite);
        setTimerLabel(false, timerLabelBlack, timeBlack);
        timelineWhite = new Timeline(new KeyFrame(Duration.millis(1000), actionEvent -> {
            timeWhite.set(timeWhite.intValue() - 1);
            if (timeWhite.intValue() <= 0 && optionsMenu.isCompetitiveMode()) {
                timelineWhite.stop();
                timelineBlack.stop();
                gameOver("OUT OF TIME");
            }
        }));
        timelineWhite.setCycleCount(Timeline.INDEFINITE);
        timelineBlack = new Timeline(new KeyFrame(Duration.millis(1000), actionEvent -> {
            timeBlack.set(timeBlack.intValue() - 1);
            if (timeBlack.intValue() <= 0 && optionsMenu.isCompetitiveMode()) {
                timelineBlack.stop();
                timelineWhite.stop();
                gameOver("OUT OF TIME");
            }
        }));
        timelineBlack.setCycleCount(Timeline.INDEFINITE);
        Label timerTitle = new Label("Time Left");
        timerTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        HBox whiteTimeRow = setTimerRow(true, timerLabelWhite);
        HBox blackTimeRow = setTimerRow(false, timerLabelBlack);
        setTimerBox(timerTitle, whiteTimeRow, blackTimeRow);
    }

    /**
     * This method will set the format to display the timers that are
     * used in competitive mode.
     *
     * @param title     a Label to indicate the white/black timers
     * @param whiteRow  an Hbox to display the white users time
     * @param blackRow  an Hbox to display the black users time
     */
    private void setTimerBox(Label title, HBox whiteRow, HBox blackRow) {
        timerBox = new VBox();
        timerBox.setAlignment(Pos.CENTER);
        timerBox.getChildren().addAll(title, whiteRow, blackRow);
        timerBox.setManaged(false);
        timerBox.setVisible(false);
    }

    /**
     * This method will set the format to display the timers for each
     * player. White timer in red, black timer in blue.
     *
     * @param isWhite   a boolean to differentiate between white/black player time
     * @param label     a label to display the color of the white/black player
     * @param time      a DoubleProperty to display the players time
     */
    private void setTimerLabel(boolean isWhite, Label label, DoubleProperty time) {
        label.managedProperty().bind(label.visibleProperty());
        time.set(timeControl);
        label.textProperty().bind(time.asString());
        label.setTextFill(isWhite ? Color.RED : Color.BLUE);
        label.setFont(new Font("Arial", 24));
        label.setAlignment(Pos.CENTER);
    }

    /**
     * This method will create/display text so players can identify
     * which timer belongs to which player.
     * @param isWhite       a boolean to indicate white/black player
     * @param timerLabel    a Label to display the white/black player time
     * @return              an HBox to display the labels
     */
    private HBox setTimerRow(boolean isWhite, Label timerLabel) {
        HBox row = new HBox();
        Label title;
        if (isWhite)
            title = new Label("White Time");
        else
            title = new Label("Black Time");
        title.setFont(new Font("Arial", 24));
        row.setSpacing(5);
        row.getChildren().addAll(title, timerLabel);
        return row;
    }

    /**
     * This method will set up the game board which is displayed.
     *
     * @param window    a BorderPane that will be shown in the stage
     */
    private void setCenterPane(BorderPane window) {
        VBox mainBox = new VBox();
        HBox topRow = new HBox();
        HBox midRow = new HBox();
        HBox bottomRow = new HBox();
        mainBox.setAlignment(Pos.CENTER);
        mainBox.getChildren().addAll(topRow, midRow, bottomRow);
        setRow(topRow, false);
        setRow(bottomRow, true);
        setMidRow(midRow);
        window.setCenter(mainBox);
    }

    /**
     * This method sets up the rows that will be displayed in the GUI.
     *
     * @param row           an HBox representing a row
     * @param isTopBorder   a boolean indicating if the row is the top border
     */
    private void setRow(HBox row, boolean isTopBorder) {
        row.setAlignment(Pos.CENTER);
        addLabelsHorizontal(row, isTopBorder);
    }

    /**
     * Set the horizontal labels (A, B, C, ...) so the users can easily
     * determine where they want/need to click.
     *
     * @param row          an HBox representing a row
     * @param isTopBorder  a boolean indicating if the row is the top border
     */
    private void addLabelsHorizontal(HBox row, boolean isTopBorder) {
        for (String labelText : horizontalLabels) {
            Label label = new Label(labelText);
            label.setAlignment(Pos.CENTER);
            label.setMinWidth(101);
            label.setMaxWidth(101);
            label.setFont(new Font("Arial", 24));
            if (isTopBorder)
                label.getStyleClass().add("topBorder");
            else
                label.getStyleClass().add("bottomBorder");
            row.getChildren().add(label);
        }
    }

    /**
     * Set the vertical labels (1, 2, 3, ...) so the users can easily
     * determine where they wan't need to click.
     *
     * @param column            an VBox representing a column
     * @param isRightBorder     a boolean indicating if the column is the right border
     */
    private void addLabelsVertical(VBox column, boolean isRightBorder) {
        for (String labelText : verticalLabels) {
            Label label = new Label(labelText);
            label.setAlignment(Pos.CENTER_LEFT);
            label.setMinHeight(100);
            label.setMaxHeight(100);
            label.setFont(new Font("Arial", 24));
            if (isRightBorder)
                label.getStyleClass().add("rightBorder");
            else
                label.getStyleClass().add("leftBorder");
            column.getChildren().add(label);
        }

    }

    /**
     * This method will set the middle row in the stage.
     *
     * @param row   an HBox representing a row
     */
    private void setMidRow(HBox row) {
        row.setAlignment(Pos.CENTER);
        VBox leftColumn = new VBox();
        VBox rightColumn = new VBox();
        leftColumn.setAlignment(Pos.CENTER);
        rightColumn.setAlignment(Pos.CENTER);
        grid.setAlignment(Pos.CENTER);
        addLabelsVertical(leftColumn, true);
        addLabelsVertical(rightColumn, false);
        row.getChildren().addAll(leftColumn, grid, rightColumn);
    }

    /**
     * This method will create the black and white grid of blocks
     * that make up the chess board. Additionally, it will place
     * the game pieces on the board.
     */
    private void setGrid() {
        for (int i = 0; i < WIDTH_HEIGHT; i++) {
            for (int j = 0; j < WIDTH_HEIGHT; j++) {
                VBox box = new VBox();
                setGridBox(box);
                addGridBoxEvent(box, i, j);
                Label label = new Label();
                label.setFont(new Font("Serif", 60));
                String piece = controller.getPieceString(i, j);
                setWhiteBlackBoxColor(i, j, box);
                label.setText(piece);
                box.getChildren().add(label);
                grid.add(box, j, i);
                nodeMap.put(new compoundKey(i, j), label);
                boxMap.put(new compoundKey(i, j), box);
            }
        }
    }

    /**
     * If the users wish to start a new game, this method
     * will be called through the update method to reset the
     * board to a new game.
     */
    private void resetGrid() {
        for (int i = 0; i < WIDTH_HEIGHT; i++) {
            for (int j = 0; j < WIDTH_HEIGHT; j++) {
                compoundKey key = new compoundKey(i, j);
                Label label = nodeMap.get(key);
                String piece = controller.getPieceString(i, j);
                label.setText(piece);
            }
        }
    }

    /**
     * This method will set the black and white colors for the blocks
     * that make up the chess board. Each block is a VBox.
     *
     * @param i     an int representing the x coord of a block
     * @param j     an int representing the y coord of a block
     * @param box   a VBox that will have its color set
     */
    private void setWhiteBlackBoxColor(int i, int j, VBox box) {
        if (i % 2 == 0)
            box.setBackground(j % 2 == 0 ? new Background(new BackgroundFill(Color.WHITE, null, null)) :
                    new Background(new BackgroundFill(Color.DARKGREY, null, null)));
        else
            box.setBackground(j % 2 == 0 ? new Background(new BackgroundFill(Color.DARKGREY, null, null)) :
                    new Background(new BackgroundFill(Color.WHITE, null, null)));
    }

    /**
     * Add events to the blocks on the game board so the users can click
     * to make moves.
     *
     * @param box   a VBox representing a block on the board, gets an event handler
     * @param i     an int representing the x coord of a block
     * @param j     an int representing the y coord of a block
     */
    private void addGridBoxEvent(VBox box, int i, int j) {
        box.setOnMouseClicked(mouseEvent -> {
            if (!controller.isMyTurn())
                return;
            if (controller.networked()) {
                if (!canClick) {
                    return;
                }
            }
            if (!firstClick) {
                firstClickCoords = new compoundKey(i, j);
                boolean validMove = controller.legalFirstClick(firstClickCoords);
                if (validMove) {
                    movedSquares = controller.getValidMoveSet(firstClickCoords);
                    movedSquares.forEach(move -> boxMap.get(move)
                            .setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, null, null))));
                    firstClick = true;
                } else {
                    inValidMoveMsg();
                }
            } else { // second click here
                secondClickCoords = new compoundKey(i, j);
                boolean validMove = controller.legalSecondMove(firstClickCoords, secondClickCoords);
                if (validMove) {
                    controller.makeMove(firstClickCoords, secondClickCoords);
                    canClick = false;
                }
                movedSquares.forEach(move -> setWhiteBlackBoxColor(move.i(), move.j(), boxMap.get(move)));
                // reset coordinates no matter what
                firstClickCoords = secondClickCoords = null;
                // reset to first click no matter what
                firstClick = false;
            }
        });
    }

    /**
     * An alert to let the user know that their attempted move was NOT valid.
     * This will be called in the block's event handler.
     */
    public void inValidMoveMsg() {
        Alert inValidMsg = new Alert(Alert.AlertType.INFORMATION);
        inValidMsg.setContentText("Move is NOT valid, please try again.");
        inValidMsg.show();
    }

    /**
     * Set the dimensions for a VBox.
     *
     * @param box   a VBox representing a block on the game board
     */
    private void setGridBox(VBox box) {
        box.setStyle("-fx-border-color: black");
        box.setMaxHeight(100);
        box.setMaxWidth(100);
        box.setMinHeight(100);
        box.setMinWidth(100);
        box.setAlignment(Pos.CENTER);
    }

    /**
     * This method is required as part of the class implementing Observer.
     * This will ensure when player moves are made in the model, the
     * GUI will display the game pieces in the correct location.
     *
     * @param o     the observable object (model)
     * @param arg   the argument passed to notify the observers
     */
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            if (arg.equals("reset")) {
                firstClickCoords = secondClickCoords = null;
                firstClick = false;
                timeWhite.set(timeControl);
                timeBlack.set(timeControl);
                controller.setWhiteTurn(true);
                grid.getChildren().forEach(node -> node.setDisable(false));
                resetGrid();
                if (optionsMenu.isCompetitiveMode()) {
                    timerBox.setVisible(true);
                    timerBox.setManaged(true);
                    timelineWhite.play();
                    timelineBlack.stop();
                    stage.setMaxWidth(1210);
                    stage.setMinWidth(1210);
                } else {
                    stage.setMaxWidth(910);
                    stage.setMinWidth(910);
                    timerBox.setVisible(false);
                    timerBox.setManaged(false);
                }
            }
        } else {
            ChessMoveMessage moveInfo = (ChessMoveMessage) arg;
            for (ChessModel.Move move : moveInfo.getMoveSet())
                nodeMap.get(new compoundKey(move.x(), move.y())).setText(move.piece());
            if (timelineWhite.getStatus() == Animation.Status.RUNNING) {
                timelineWhite.stop();
                timelineBlack.play();
            } else {
                timelineWhite.play();
                timelineBlack.stop();
            }
            if (moveInfo.isGameOver())
                gameOver("CHECKMATE");
            firstClick = false;
            if (controller.networked())
                canClick = true;
        }
    }

    private void gameOver(String message) {
        popupEventAlert(message, Alert.AlertType.CONFIRMATION);
        grid.getChildren().forEach(node -> node.setDisable(true));
    }


    /**
     * This record is used to make a unique key to use in our node map for
     * each i,j index in the 2d grid pane. This is to prevent issues where
     * 2,3 and 3,2 would overwrite each other in the map.
     */
    public record compoundKey(int i, int j) {
    }

    /**
     * An alert used to notify players of Checkmate, the game is over.
     * @param message       a String containing "CHECKMATE"
     * @param alertType     an alertType confirmation
     */
    private void popupEventAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setAlertType(alertType);
        alert.setContentText(message);
        alert.show();
    }
}