import javafx.application.Application;
import model.empty;
import view.ChessGUIView;
/**
 * CSC335
 * Due: 2021-12-08
 * Team 12
 * Team Project: Chess
 * Jeff Wiederkehr, Chris Herrera, Dylan Snavely, Jason Earl
 *
 * This program enables user's to play a standard game of chess,
 * both locally and networked. The program is implemented using a standard
 * Model-View-Controller (MVC) setup. In addition to the basic game, there are
 * 2 WOW factors for this program:
 *              1) A timer that tracks each player's total move time for the game. (3 mins = 180 secs)
 *              2) Graphics indicating valid moves on the board by changing the color of the block (green).
 *
 * The class below, Chess, will initialize the game when run. This class will
 * launch the ChessGUIView which will initialize the model (ChessModel) and
 * controller (ChessController).
 */
public class Chess {
    public static void main(String[] args) {
        Application.launch(ChessGUIView.class, args);
    }
}
