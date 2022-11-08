package model.Pieces;

import java.util.List;
import java.util.Set;

/**
 * This class will ensure every game piece has the same methods and parameters.
 */
public abstract class Piece {
//	public abstract boolean isValidMove(int row, int col);

	/**
	 * Move the piece, update the row and column
	 * @param row	an int
	 * @param col	an int
	 */
	public abstract void move(int row, int col);

	/**
	 * Create a Set of Integer Lists indicating possible moves.
	 *
	 * @return	a Set of Integer Lists indicating possible moves
	 */
	public abstract Set<List<Integer>> moveSet();

	/**
	 * Ensure piece in on the board
	 * @param row	an int
	 * @param col	an int
	 * @return		a boolean, true if in bounds
	 */
	public static boolean inBounds(int row, int col) {
		return row >= 0 && row < 8 && col >= 0 && col < 8;
	}

	/**
	 * a getter method for row
	 * @return	an int
	 */
	public abstract int getRow();

	/**
	 * a getter method for column
	 * @return	an int
	 */
	public abstract int getColumn();

	/**
	 * a getter method for icon
	 * @return	a String, icon
	 */
	public abstract String getIcon();

	/**
	 * a getter method for short name
	 * @return	a String, Ex: P for Pawn
	 */
	public abstract String getShortName();

	/**
	 * a getter method for isWhite
	 * @return	a boolean, true if piece is white, else false
	 */
	public abstract boolean getColor();

	/**
	 * a getter method for madeFirstMove
	 * @return	a boolean
	 */
	public abstract boolean getMadeFirstMove();
}
