package model.Pieces;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents the Rook game piece.
 */
public class Rook extends Piece{
	private int row;
	private int col;
	private String icon;
	private final boolean isWhite;
	private boolean madeFirstMove;

	/**
	 * Initialize position (row, col), isWhite (determine which player's piece),
	 * icon (set right color game piece), madeFirstMove.
	 *
	 * @param row		an int
	 * @param col		an int
	 * @param isWhite	a boolean, true if piece is white, else false
	 */
	public Rook(int row, int col, boolean isWhite) {
		this.row = row;
		this.col = col;
		this.isWhite = isWhite;
		this.icon = (isWhite) ? "\u2656" : "\u265C";
		this.madeFirstMove = false;
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
	 * @return	a String, R for Rook
	 */
	public String getShortName() {
		return "R";
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
		for (int i = row + 1; inBounds(i, col); i++) {
			moveSet.add(Arrays.asList(i, col));
		}
		for (int i = row - 1; inBounds(i, col); i--) {
			moveSet.add(Arrays.asList(i, col));
		}
		for (int i = col + 1; inBounds(row, i); i++) {
			moveSet.add(Arrays.asList(row, i));
		}
		for (int i = col - 1; inBounds(row, i); i--) {
			moveSet.add(Arrays.asList(row, i));
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
	public boolean getMadeFirstMove(){
		return madeFirstMove;
	}
}
