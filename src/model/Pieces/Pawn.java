package model.Pieces;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * This class represents the Pawn game piece.
 */
public class Pawn extends Piece {
	private int row;
	private int col;
	private boolean madeFirstMove;
	private String icon;
	private final boolean isWhite;

	/**
	 * Initialize position (row, col), isWhite (determine which player's piece),
	 * icon (set right color game piece), madeFirstMove.
	 *
	 * @param row		an int
	 * @param col		an int
	 * @param isWhite	a boolean, true if piece is white, else false
	 */
	public Pawn(int row, int col, boolean isWhite) {
		this.row = row;
		this.col = col;
		this.madeFirstMove = false;
		this.isWhite = isWhite;
		this.icon = (isWhite) ? "\u2659" : "\u265F";
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
	 * @return	a String, P for Pawn
	 */
	public String getShortName() {
		return "P";
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
		if (icon.equals("\u2659")) {
			if (!madeFirstMove) {
				moveSet.add(Arrays.asList(row - 2, col));
			}
			moveSet.add(Arrays.asList(row - 1, col));
			moveSet.add(Arrays.asList(row - 1, col + 1));
			moveSet.add(Arrays.asList(row - 1, col - 1));
		} else {
			if (!madeFirstMove) {
				moveSet.add(Arrays.asList(row + 2, col));
			}
			moveSet.add(Arrays.asList(row + 1, col));
			moveSet.add(Arrays.asList(row + 1, col + 1));
			moveSet.add(Arrays.asList(row + 1, col - 1));
		}
		moveSet.removeIf(move -> !inBounds(move.get(0), move.get(1)));
		return moveSet;
	}

	/**
	 * a getter method for madeFirstMove
	 * @return	a boolean
	 */
	public boolean madeFirstMove() {
		return madeFirstMove;
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

}
