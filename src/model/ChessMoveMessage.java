package model;

import model.ChessModel.Move;
import view.ChessGUIView.compoundKey;

import java.io.Serializable;
import java.util.*;

/**
 * This class will serve as an object that is passed to observers (ChessGUIView) of
 * the model. It will contain information about move coordinates, game status,
 * and game pieces. This will ensure the game is displayed correctly.
 */
public class ChessMoveMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    public ChessModel.Move firstMove;
    public ChessModel.Move secondMove;
    public boolean isGameOver;
    public boolean castleMove;
    public ChessModel.Move kingNewPosition;
    public ChessModel.Move rookNewPosition;
    public Set<Move> moveSet = new HashSet<>();

    /**
     * The constructor that assigns values to the relevant parameters.
     * See ChessModel for details on Move objects.
     *
     * @param firstMove         a Move containing x,y coordinates and string representing the game piece
     * @param secondMove        a Move containing x,y coordinates and string representing the game piece
     * @param isGameOver        a boolean indicating if the game is over
     * @param castleMove        a boolean indicating a castle move
     * @param kingNewPosition   a Move containing x,y coordinates and string representing the king
     * @param rookNewPosition   a Move containing x,y coordinates and string representing the rook
     */
    public ChessMoveMessage(Move firstMove, Move secondMove, boolean isGameOver, boolean castleMove,
                            Move kingNewPosition,Move rookNewPosition) {
        this.firstMove = firstMove;
        this.secondMove = secondMove;
        this.isGameOver = isGameOver;
        this.kingNewPosition = kingNewPosition;
        this.castleMove = castleMove;
        this.rookNewPosition = rookNewPosition;
        moveSet.addAll(Arrays.asList(firstMove, secondMove, kingNewPosition, rookNewPosition));
        moveSet.removeIf(Objects::isNull);
    }

    /**
     * A method to swap and access the x, y coordinates of the first move.
     * See ChessGUIView for details on compoundKey.
     *
     * @return  a compoundKey with x,y coords of first move
     */
    public compoundKey firstMoveCoordinateKey() {
        return new compoundKey(firstMove.x(), firstMove.y());
    }

    /**
     * A method to swap and access the x, y coordinates of the second move.
     * See ChessGUIView for details on compoundKey.
     *
     * @return  a compoundKey with x,y coords of second move
     */
    public compoundKey secondMoveCoordinateKey() {
        return new compoundKey(secondMove.x(), secondMove.y());
    }

    /**
     * A getter for the isGameOver boolean.
     *
     * @return  a boolean indicating if the game is over
     */
    public boolean isGameOver() {
        return isGameOver;
    }
    /**
     * A getter for the moveSet, a Set of Move objects.
     * Move objects contain x,y coords and the String piece.
     * See ChessModel for Move object info.
     *
     * @return  a set of Move objects.
     */
    public Set<Move> getMoveSet(){
        return moveSet;
    }

}
