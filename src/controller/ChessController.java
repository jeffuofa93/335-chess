package controller;

import javafx.application.Platform;
import model.ChessModel;
import model.ChessMoveMessage;
import view.ChessGUIView.compoundKey;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

/**
 * This class is the controller for the MVC setup. This class will facilitate
 * the communication between the view (ChessGUIView) and the model (ChessModel).
 * Note that the view is NOT called in this class. Rather, as the users interact
 * with the GUI, calls will be made from the GUI to the controller and then to
 * the model.
 */
public class ChessController {
    private final ChessModel model;
    private Socket connection;
    private boolean isServer = false;
    private boolean isConnected = false;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    /**
     * This constructor will be called by the view (ChessGUIView).
     * The controller (and model) will be initialized in the view.
     *
     * @param model     the model (ChessModel) that handles game logic
     */
    public ChessController(ChessModel model) {
        this.model = model;
    }


    /**
     * This method will be called by the view (ChessGUIView) to ensure
     * that it is the turn of the player (white/black) who is clicking on the game board.
     * This is to prevent unwanted / accidental clicks from changing the
     * status of the game.
     *
     * The boolean (isWhite) will be sent from the view to the controller. The return value
     * is another boolean that indicates if the model has approved the move
     * attempted by the player clicking on the board. True if it is the turn
     * of the player that clicked on the board, else false.
     *
     * @return          a boolean indicating it is or is NOT the player's turn to move
     */
    public boolean isMyTurn() {
        return model.isMyTurn();
    }

    /**
     * This method will set the whiteTurn boolean value in the model to the
     * boolean that is passed as a parameter.
     *
     * @param turn  a boolean to indicate if it is the white player's turn
     */
    public void setWhiteTurn(boolean turn) {
        model.setWhiteTurn(turn);
    }


    /**
     * This is a getter for the status of the white player's turn.
     *
     * @return a boolean indicating if it is the white player's turn
     */
    public boolean isWhiteTurn() {
        return model.isWhiteTurn();
    }


    /**
     * The players interact with the GUI game board by first clicking on the
     * piece they want to move, then clicking on the block the wish to move the piece to.
     * The method will determine if the first click made by the player is valid.
     * For example, if the white player clicks on a white pawn, the model will confirm
     * that the move is valid (true). But, if the white player attempts to move a black pawn,
     * then the model will declare the move invalid (false).
     *
     * This method will be called from the view, which sends the coordinates
     * of the block where the player clicked. The coordinates are stored as
     * a compoundKey (see ChessGUIView). The model will check the coordinates
     * to ensure the player is moving a game piece legally.
     *
     * @param coordinates   a compoundKey storing the coordinates where the player clicked
     * @return              a boolean, true if click is legal, else false
     */
    public boolean legalFirstClick(compoundKey coordinates) {
        return model.legalFirstClick(coordinates);
    }

    /**
     * This method is the second step in the process of verifying a legal move.
     * The method will be called by the view which will send the coordinates
     * of the piece the player wants to move and the coordinates of the block
     * that the player wishes to move the piece to.
     *
     * The coordinates will be sent to the model which will return a boolean
     * indicating if the move is legal (true) or not (false).
     *
     * @param firstClickCoords      a compoundKey storing the coordinates where the player first clicked
     * @param secondClickCoords     a compoundKey storing the coordinates where the player clicked second
     * @return                      a boolean, true if click is legal, else false
     */
    public boolean makeMove(compoundKey firstClickCoords, compoundKey secondClickCoords) {
        if (!isConnected) {
            model.makeMove(firstClickCoords, secondClickCoords);
        } else {
            ChessMoveMessage toSend = model.networkedMove(firstClickCoords, secondClickCoords);
            sendMessage(toSend);

        }
        return false;
    }

    /**
     * This method is similar to legalFirstClick. It will send the coordinates of the first and
     * second clicks to the model to verify they are valid moves.
     *
     * @param firstClickCoords  a compoundKey storing the coordinates where the player first clicked
     * @param secondClickCoords a compoundKey storing the coordinates where the player clicked second
     * @return                  a boolean, true if click is legal, else false
     */
    public boolean legalSecondMove(compoundKey firstClickCoords, compoundKey secondClickCoords) {
        return model.legalSecondMove(firstClickCoords, secondClickCoords);
    }

    /**
     * This method will aid in the construction of the game board. The view will call
     * this method as it constructs the board. The view will send the coordinates of
     * each block on the game board. This method will send those coordinates to the model
     * which will return a string the represents a game piece (pawn, king, queen, etc.).
     * Note that these strings are what create the visual pieces on the board.
     *
     * @param i     an integer representing the x coordinate
     * @param j     an integer representing the y coordinate
     * @return      a String representing a game piece
     */
    public String getPieceString(int i, int j) {
        return model.getPieceString(i, j);
    }

    /**
     * This method will enable users to load a previous game. The method will
     * be called by the view which will send a File containing information about
     * saved game they wish to continue. From here, the model will be sent the
     * file and set up the game as per the File.
     *
     * @param gameFile      a File with info to load a previously saved game
     */
    public void loadGame(File gameFile, boolean isWhite) {
        //	model.setLoadGame();
        try {
            model.loadGame(gameFile, isWhite);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // TODO Auto-generated method stub

    }

    /**
     * This method will enable users to save a game that can later be
     * re-loaded and played.
     *
     * @return      a String containing info about the saved game
     */
    public String saveGame() {
        return model.saveGame();
    }

    /**
     * This method will be called from the view which sends the coordinates
     * of the player's first clicked block. From here, the model will
     * receive the coordinates and return them as a Set.
     *
     * @param firstClick    a compoundKey storing the coordinates where the player first clicked
     * @return              a Set of compoundKeys with coordinates
     */
    public Set<compoundKey> getValidMoveSet(compoundKey firstClick) {
        return model.getValidMoves(firstClick);
    }

    /**
     * This method will start the server so the users can play on a network.
     */
    public void startServer() {
        System.out.println("startServer");
        System.out.println(this);
        try {

            ServerSocket server = new ServerSocket(4000);
            //This blocks, and that's bad. But it's only a short block
            //so we won't solve it. Once the client connects, it will
            //unblock.
            connection = server.accept();

            //When we reach here, we have a client, so grab the
            //streams to do our message sending/receiving
            oos = new ObjectOutputStream(connection.getOutputStream());
            ois = new ObjectInputStream(connection.getInputStream());

            isServer = true;
            isConnected = true;
            model.flipMyTurn();
//			model.setNetworked();
        } catch (IOException e) {
            System.err.println("Something went wrong with the network! " + e.getMessage());
        }
    }

    /**
     * This method will start the client so the users can play on a network.
     */
    public void startClient() {
        try {

            connection = new Socket("localhost", 4000);

            isServer = false;
            isConnected = true;
            //		model.setNetworked();
            oos = new ObjectOutputStream(connection.getOutputStream());
            ois = new ObjectInputStream(connection.getInputStream());

            //A thread represents code that can be done concurrently
            //to other code. We have the "main" thread where our program
            //is happily running its event loop, and now we
            //create a second thread whose entire job it is to send
            //our message to the other program and to block (wait) for
            //their response.
            Thread t = new Thread(() -> {
                try {
                    ChessMoveMessage otherMsg = (ChessMoveMessage) ois.readObject();

                    //The runLater method places an event on the main
                    //thread's event queue. All things that change UI
                    //elements must be done on the main thread.
                    if (otherMsg != null) {
                        Platform.runLater(() -> {
                            if (model.legalSecondMove(otherMsg.firstMoveCoordinateKey(),
									otherMsg.secondMoveCoordinateKey())) {
                                model.flipMyTurn();
                                model.makeMove(otherMsg.firstMoveCoordinateKey(), otherMsg.secondMoveCoordinateKey());
                            }

                        });
                    }
                    //We let the thread die after receiving one message.
                    //We'll create a new one on the next click.
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Something went wrong with serialization in startClient: " + e.getMessage());
                }
            });
            //This is when the thread begins running - so now.
            t.start();
            //Even though this method is done, the code of the thread
            //keeps doing its job. We've just allowed the event handler
            //to return and the event loop to keep running.
        } catch (IOException e) {
            System.err.println("Something went wrong with the network! " + e.getMessage());
        }
    }

    /**
     * Send the ChessMoveMessage containing info about the coordinates, pieces,
     * and game status.
     *
     * @param msg   a ChessMoveMessage
     */
    private void sendMessage(ChessMoveMessage msg) {
        if (!isConnected) {
            return;
        }

        //See the code above that does the same. Here we will send
        //a message and receive one in the new thread, avoiding
        //blocking the event loop.
        Thread t = new Thread(() -> {
            try {
                oos.writeObject(msg);
                oos.flush();
                //			System.out.println("outter sendMessage");
                //			System.out.println(this);
                ChessMoveMessage otherMsg = (ChessMoveMessage) ois.readObject();
                if (otherMsg != null) {
                    Platform.runLater(() -> {
                        System.out.println("sendMessage in controller");
                        //		model.setMyTurn(true);
                        if (model.legalSecondMove(otherMsg.firstMoveCoordinateKey(), otherMsg.secondMoveCoordinateKey())) {
                            model.makeMove(otherMsg.firstMoveCoordinateKey(), otherMsg.secondMoveCoordinateKey());
                        }
                        //		model.makeMove(otherMsg.firstMoveCoordinateKey(), otherMsg.secondMoveCoordinateKey());
                    });
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Something went wrong with serialization in sendMessage: " + e.getMessage());
            }

        });
        t.start();

    }

    /**
     * A getter to check if the game is networked.
     *
     * @return  a boolean, ture if networked, else false
     */
    public boolean networked() {
        return isConnected;
    }

}
