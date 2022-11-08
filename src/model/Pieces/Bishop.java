package model.Pieces;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents the Bishop game piece.
 */
public class Bishop extends Piece {
	private int row;
	private int col;
	private String icon;
	private boolean isWhite;
	private boolean madeFirstMove;

	/**
	 * Initialize position (row, col), isWhite (determine which player's piece),
	 * icon (set right color game piece), madeFirstMove.
	 *
	 * @param row		an int
	 * @param col		an int
	 * @param isWhite	a boolean, true if piece is white, else false
	 */
	public Bishop(int row, int col, boolean isWhite) {
		this.row = row;
		this.col = col;
		this.isWhite = isWhite;
		this.icon = (isWhite) ? "\u2657" : "\u265D";
		this.madeFirstMove = false;
	}

	/**
	 * Move the piece, update the row and column
	 * @param row	an int
	 * @param col	an int
	 */
	@Override
	public void move(int row, int col) {
		this.row = row;
		this.col = col;
		this.madeFirstMove = true;
	}

	/**
	 * Create a Set of Integer Lists indicating possible moves.
	 *
	 * @return	a Set of Integer Lists indicating possible moves
	 */
	@Override
	public Set<List<Integer>> moveSet() {
		Set<List<Integer>> moveSet = new HashSet<>();
		for (int i = row + 1, j = col + 1; inBounds(i, j); i++, j++) {
			moveSet.add(Arrays.asList(i, j));
		}
		for (int i = row + 1, j = col - 1; inBounds(i, j); i++, j--) {
			moveSet.add(Arrays.asList(i, j));
		}
		for (int i = row - 1, j = col + 1; inBounds(i, j); i--, j++) {
			moveSet.add(Arrays.asList(i, j));
		}
		for (int i = row - 1, j = col - 1; inBounds(i, j); i--, j--) {
			moveSet.add(Arrays.asList(i, j));
		}
		return moveSet;
	}

	/**
	 * a getter method for icon
	 * @return	a String, icon
	 */
	@Override
	public String getIcon() {
		return icon;
	}

	/**
	 * a getter method for isWhite
	 * @return	a boolean, true if piece is white, else false
	 */
	@Override
	public boolean getColor() {
		return isWhite;
	}

	/**
	 * a getter method for madeFirstMove
	 * @return	a boolean
	 */
	@Override
	public boolean getMadeFirstMove() {
		return madeFirstMove;
	}

	/**
	 * a getter method for row
	 * @return	an int
	 */
	@Override
	public int getRow() {
		return row;
	}

	/**
	 * a getter method for column
	 * @return	an int
	 */
	@Override
	public int getColumn() {
		return col;
	}

	/**
	 * a getter method for short name
	 * @return	a String, B for bishop
	 */
	public String getShortName() {
		return "B";
	}

}
