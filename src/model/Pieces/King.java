package model.Pieces;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents the King game piece.
 */
public class King extends Piece{
	private int row;
	private int col;
	private final String icon;
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
	public King(int row, int col, boolean isWhite) {
		this.row = row;
		this.col = col;
		this.isWhite = isWhite;
		this.madeFirstMove = false;
		this.icon = (isWhite) ? "\u2654" : "\u265A";
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
		final Integer[][] moves = {
				{row, col + 1},
				{row, col - 1},
				{row + 1, col},
				{row + 1, col + 1},
				{row + 1, col - 1},
				{row - 1, col},
				{row - 1, col + 1},
				{row - 1, col - 1}
		};
		Set<List<Integer>> moveSet = new HashSet<>();
		// Make sure these moves are within the bounds of the board.
		for (Integer[] move : moves) {
			if (inBounds(move[0], move[1])) {
				moveSet.add(Arrays.asList(move));
			}
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
	 * a getter method for short name
	 * @return	a String, K for king
	 */
	public String getShortName() {
		return "K";
	}

	/**
	 * a getter method for madeFirstMove
	 * @return	a boolean
	 */
	public boolean getMadeFirstMove(){
		return madeFirstMove;
	}
}
