package model;


import model.Pieces.*;
import view.ChessGUIView.compoundKey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * This class represents the model in the MVC format of the program.
 * It will handle most of the logic behind the game play. Specifically,
 * it will determine which moves are valid/invalid and which player's turn it
 * is. It will be observed by the view (ChessGUIView) and as a result,
 * will notify the GUI to ensure the game is properly displayed to the user.
 */
public class ChessModel extends Observable {
    private static final int WIDTH_HEIGHT = 8;
    private static final String white = "white";
    private static final String black = "black";
    private boolean whiteTurn = true;
    private List<List<Piece>> boardGrid;
    List<List<String>> savedGame = new ArrayList<>();
    private Map<String, Set<Piece>> pieceMap = new HashMap<>();
    private Map<String, Piece> kingMap = new HashMap<>();
    private boolean myTurn = true;

    /**
     * The constructor will initialize a grid and a map of game pieces
     * to track the game progress.
     *
     * @throws IOException  throw exception if program fails.
     */
    public ChessModel() throws IOException {
        initGrid();
        initPieceMap();
    }

    /**
     * This method will create maps to track the game pieces.
     */
    private void initPieceMap() {
        pieceMap = new HashMap<>();
        kingMap = new HashMap<>();
        pieceMap.put(white, new HashSet<>());
        pieceMap.put(black, new HashSet<>());
        boardGrid.forEach(list -> list.stream().filter(Objects::nonNull).forEach(piece -> {
            if (piece.getColor()) {
                pieceMap.get(white).add(piece);
                if (piece.getShortName().equals("K"))
                    kingMap.put(white, piece);
            } else {
                pieceMap.get(black).add(piece);
                if (piece.getShortName().equals("K"))
                    kingMap.put(black, piece);
            }
        }));
    }

    /**
     * This method will save a game so that it can be accessed later.
     * The game is saved in a Sting. Game pieces are stored
     * and their x,y coords, color, and name are assigned to each piece.
     *
     * @return  a String containing info about the status of the game
     */
    public String saveGame() {
        StringBuilder gameData = new StringBuilder();
        gameData.append(String.valueOf(whiteTurn) + "\n");
        for (List<Piece> row : boardGrid) {
            for (Piece piece : row) {
                if (piece != null) {
                    gameData.append(piece.getRow());
                    gameData.append(" ");
                    gameData.append(piece.getColumn());
                    gameData.append(" ");
                    gameData.append(piece.getShortName());
                    gameData.append(" ");
                    gameData.append(piece.getColor());
                    gameData.append("\n");
                }
            }
        }

        return gameData.toString();
    }

    /**
     * A getter method to determine if is is the player in questions turn
     * to make a move.
     *
     * @return  a boolean, true if it is the player's turn, else false
     */
    public boolean isMyTurn() {
    	return myTurn;
    }

    /**
     * A method to swap the current player turn status.
     * If myTurn = true, becomes false.
     * If myTurn = false, becomes true.
     */
    public void flipMyTurn() {
    	this.myTurn = !myTurn;
    }

    /**
     * This method will load a previously saved game. The game file
     * is read and the pieces placed on the board and game status is restored.
     *
     * @param gameFile      a File the contains info about the game status
     * @throws IOException  throw exception if file is not found
     */
    public void loadGame(File gameFile, boolean isWhite) throws IOException {
        if (gameFile == null) {
            myTurn = isWhite;
            initGrid();
            
        } else {
            initBlankGrid();
            BufferedReader br = new BufferedReader(new FileReader(gameFile));
            String line = br.readLine();
            while (line != null) {
                List<String> partitioned = Arrays.asList(line.split(" "));
                savedGame.add(partitioned);
                line = br.readLine();
            }

            for (List<String> s : savedGame) {
            	if (s.size() == 1) whiteTurn = Boolean.valueOf(s.get(0));
                if (s.size() == 4) {
                    Piece toAdd = null;
                    int i = Integer.valueOf(s.get(0));
                    int j = Integer.valueOf(s.get(1));
                    switch (s.get(2)) {
                        case "Kn" -> toAdd = new Knight(i, j, Boolean.valueOf(s.get(3)));
                        case "K" -> toAdd = new King(i, j, Boolean.valueOf(s.get(3)));
                        case "B" -> toAdd = new Bishop(i, j, Boolean.valueOf(s.get(3)));
                        case "P" -> toAdd = new Pawn(i, j, Boolean.valueOf(s.get(3)));
                        case "Q" -> toAdd = new Queen(i, j, Boolean.valueOf(s.get(3)));
                        case "R" -> toAdd = new Rook(i, j, Boolean.valueOf(s.get(3)));
                    }
                    boardGrid.get(i).add(j, toAdd);
                }
            }
            br.close();
        } 
        initPieceMap();
        setChanged();
        notifyObservers("reset");
    }

    /**
     * The method builds a grid to track game progress. That is,
     * pieces and their location on the game board.
     */
    private void initGrid() {
        boardGrid = new ArrayList<>();
        for (int i = 0; i < WIDTH_HEIGHT; i++)
            boardGrid.add(new ArrayList<>());
        for (int i = 0; i < WIDTH_HEIGHT; i++)
            for (int j = 0; j < WIDTH_HEIGHT; j++)
                boardGrid.get(i).add(j, checkCoordinate(i, j));
    }

    /**
     * Similar to the above method, but grid is empty.
     */
    private void initBlankGrid() {
        boardGrid = new ArrayList<>();
        for (int i = 0; i < WIDTH_HEIGHT; i++)
            boardGrid.add(new ArrayList<>());
        for (int i = 0; i < WIDTH_HEIGHT; i++)
            for (int j = 0; j < WIDTH_HEIGHT; j++)
                boardGrid.get(i).add(j, null);
    }

    /**
     * This method will ensure the game pieces are placed in the right
     * block on the game board. See initGrid() above.
     *
     * @param i     an int representing an x coord
     * @param j     an int representing an y coord
     * @return      a game Piece object
     */
    private Piece checkCoordinate(int i, int j) {
        // black pawn
        if (i == 1)
            return new Pawn(i, j, false);
        // white pawn
        if (i == 6)
            return new Pawn(i, j, true);
        // black rook
        if (i == 0 && j == 0 || i == 0 && j == 7)
            return new Rook(i, j, false);
        // white rook
        if (i == 7 && j == 0 || i == 7 && j == 7)
            return new Rook(i, j, true);
        // black knight 
        if (i == 0 && j == 1 || i == 0 && j == 6)
            return new Knight(i, j, false);
        // white knight
        if (i == 7 && j == 6 || i == 7 && j == 1)
            return new Knight(i, j, true);
        // black bishop
        if (i == 0 && j == 2 || i == 0 && j == 5)
            return new Bishop(i, j, false);
        if (i == 7 && j == 2 || i == 7 && j == 5)
            return new Bishop(i, j, true);
        // black queen
        if (i == 0 && j == 3)
            return new Queen(i, j, false);
        // white queen
        if (i == 7 && j == 3)
            return new Queen(i, j, true);
        // black king
        if (i == 0 && j == 4)
            return new King(i, j, false);
        if (i == 7 && j == 4)
            return new King(i, j, true);
        // empty space
        return null;

    }

    /**
     * A getter for the status of the white player's turn, which
     * also indicates the black player's turn. If true, it is
     * white player, if false, black player.
     *
     * @return  a boolean indicating the status of the white player's turn
     */
    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    /**
     * This method will set the white player's turn status to true/false.
     * Indicating that it is or is NOT the white player's turn to move.
     *
     * @param turn  a boolean, set the status of the white player's turn
     */
    public void setWhiteTurn(boolean turn) {
    	whiteTurn = turn;
    }

    /**
     * This method will determine if the first click (choose the piece the
     * player wants to move) is legal or not. For example, if it is the white
     * player's turn and the click on a white pawn, return true. If it is
     * the white player's turn and they try to move a black pawn, return false.
     * The type compoundKey stores the x,y coordinates of the player's click.
     * See ChessGUIView for details on compoundKey.
     *
     * @param coordinates   a compoundKey storing x,y coords
     * @return              a boolean, true if legal, else false
     */
    public boolean legalFirstClick(compoundKey coordinates) {
        int i = coordinates.i();
        int j = coordinates.j();
        Piece piece = boardGrid.get(i).get(j);
        if (piece == null)
            return false;
        return piece.getColor() == whiteTurn;
    }

    /**
     * Check the first and second click coordinates to see if there are any
     * horizontal restrictions preventing the player from making the proposed move.
     *
     * @param possibleMoves     a Set of Lists of moves the player could make
     * @param firstClickCoords  a compoundKey with the x,y coords of the first click
     * @param secondClickCoords a compoundKey with the x,y coords of the second click
     * @return                  a boolean, return true if the move can be made, else false
     */
    private boolean horizontalCheck(Set<List<Integer>> possibleMoves, compoundKey firstClickCoords,
                                    compoundKey secondClickCoords) {
        boolean clearPath = true;
        boolean left = secondClickCoords.j() < firstClickCoords.j();
        int firstI = firstClickCoords.i();
        int secondI = secondClickCoords.i();
        int secondJ = secondClickCoords.j();

        for (List<Integer> move : possibleMoves) {
            if (move.get(0) == secondI && move.get(1) == secondJ || move.get(0) != firstI) continue;
            if (left) {
                if (move.get(1) > firstClickCoords.j() || move.get(1) < secondClickCoords.j()) continue;
                else if (boardGrid.get(firstI).get(move.get(1)) != null) {
                    clearPath = false;
                }
            } else { // right
                if (move.get(1) < firstClickCoords.j() || move.get(1) > secondClickCoords.j()) continue;
                else if (boardGrid.get(firstI).get(move.get(1)) != null) {
                    clearPath = false;
                }
            }
        }
        return clearPath;
    }

    /**
     * Check the first and second click coordinates to see if there are any
     * vertical restrictions preventing the player from making the proposed move.
     *
     * @param possibleMoves     a Set of Lists of moves the player could make
     * @param firstClickCoords  a compoundKey with the x,y coords of the first click
     * @param secondClickCoords a compoundKey with the x,y coords of the second click
     * @return                  a boolean, return true if the move can be made, else false
     */
    private boolean verticalCheck(Set<List<Integer>> possibleMoves, compoundKey firstClickCoords,
                                  compoundKey secondClickCoords) {
        boolean clearPath = true;
        boolean up = secondClickCoords.i() < firstClickCoords.i();
        int firstJ = firstClickCoords.j();
        int secondI = secondClickCoords.i();
        int secondJ = secondClickCoords.j();

        for (List<Integer> move : possibleMoves) {
            if (move.get(0) == secondI && move.get(1) == secondJ || move.get(1) != firstJ) continue;
            if (up) {
                if (move.get(0) > firstClickCoords.i() || move.get(0) < secondClickCoords.i()) continue;
                else if (boardGrid.get(move.get(0)).get(firstJ) != null) {
                    clearPath = false;
                }
            } else { // down
                if (move.get(0) < firstClickCoords.i() || move.get(0) > secondClickCoords.i()) continue;
                else if (boardGrid.get(move.get(0)).get(firstJ) != null) {
                    clearPath = false;
                }
            }
        }
        return clearPath;
    }

    /**
     * This method will check to see if the pawn in question can make the proposed move.
     *
     * @param firstClickCoords  a compoundKey with the x,y coords of the first click
     * @param secondClickCoords a compoundKey with the x,y coords of the second click
     * @return                  a boolean, true if move can be made, else false
     */
    private boolean pawnCheck(compoundKey firstClickCoords, compoundKey secondClickCoords) {
        boolean clearPath = true;
        boolean right = secondClickCoords.j() > firstClickCoords.j();
        boolean left = secondClickCoords.j() < firstClickCoords.j();


        Piece pawn = boardGrid.get(firstClickCoords.i()).get(firstClickCoords.j());
        Piece secondSpot = boardGrid.get(secondClickCoords.i()).get(secondClickCoords.j());

        // if the pawn is moving left or right, we already know it's also moving vertically based on its
        // possibleMoves checked previously
        if ((left || right) && secondSpot == null) {
            clearPath = false;
        } else if ((left || right) && secondSpot != null) {
            return secondSpot.getColor() != pawn.getColor();
        }
        
        // this is a blocked vertical move
        if ((!left && !right) && secondSpot != null) {
            clearPath = false;
        }

        // Check if there is a piece in front of the pawn on its first move.
        if (!((Pawn) pawn).madeFirstMove()) {
			Piece middleSpot = pawn.getColor() ?
					boardGrid.get(secondClickCoords.i() + 1).get(secondClickCoords.j()) :
					boardGrid.get(secondClickCoords.i() - 1).get(secondClickCoords.j());
			if (middleSpot != null && middleSpot != pawn) clearPath = false;
		}

		return clearPath;
    }

    /**
     * Check the first and second click coordinates to see if there are any
     * vertical restrictions preventing the player from making the proposed move.
     *
     * @param possibleMoves     a Set of Lists of moves the player could make
     * @param firstClickCoords  a compoundKey with the x,y coords of the first click
     * @param secondClickCoords a compoundKey with the x,y coords of the second click
     * @return                  a boolean, return true if the move can be made, else false
     */
    private boolean diagonalCheck(Set<List<Integer>> possibleMoves, compoundKey firstClickCoords,
                                  compoundKey secondClickCoords) {
        boolean clearPath = true;
        boolean right = secondClickCoords.j() > firstClickCoords.j();
        boolean left = secondClickCoords.j() < firstClickCoords.j();
        boolean down = secondClickCoords.i() > firstClickCoords.i();
        boolean up = secondClickCoords.i() < firstClickCoords.i();
        int secondI = secondClickCoords.i();
        int secondJ = secondClickCoords.j();

        for (List<Integer> move : possibleMoves) {
            if (move.get(0) == secondI && move.get(1) == secondJ) continue;
            if (up && left) {
                if (move.get(0) > firstClickCoords.i() || move.get(0) < secondClickCoords.i() ||
                        move.get(1) > firstClickCoords.j() || move.get(1) < secondClickCoords.j()) continue;
                else if (boardGrid.get(move.get(0)).get(move.get(1)) != null) {
                    clearPath = false;
                }
            } else if (up && right) {
                if (move.get(0) > firstClickCoords.i() || move.get(0) < secondClickCoords.i() ||
                        move.get(1) < firstClickCoords.j() || move.get(1) > secondClickCoords.j()) continue;
                else if (boardGrid.get(move.get(0)).get(move.get(1)) != null) {
                    clearPath = false;
                }
            } else if (down && left) {
                if (move.get(0) < firstClickCoords.i() || move.get(0) > secondClickCoords.i() ||
                        move.get(1) > firstClickCoords.j() || move.get(1) < secondClickCoords.j()) continue;
                else if (boardGrid.get(move.get(0)).get(move.get(1)) != null) {
                    clearPath = false;
                }
            } else if (down && right) {
                if (move.get(0) < firstClickCoords.i() || move.get(0) > secondClickCoords.i() ||
                        move.get(1) < firstClickCoords.j() || move.get(1) > secondClickCoords.j()) continue;
                else if (boardGrid.get(move.get(0)).get(move.get(1)) != null) {
                    clearPath = false;
                }
            }
        }
        return clearPath;
    }

    /**
     * This method will check to see if the piece in question can make the proposed move
     * or if there are restrictions blocking the path.
     *
     * @param firstClickCoords  a compoundKey with the x,y coords of the first click
     * @param secondClickCoords a compoundKey with the x,y coords of the second click
     * @return                  a boolean, true if move can be made, else false
     */
    public boolean checkPath(compoundKey firstClickCoords, compoundKey secondClickCoords) {

        Piece firstPiece = boardGrid.get(firstClickCoords.i()).get(firstClickCoords.j());

        Set<List<Integer>> possibleMoves = firstPiece.moveSet();
        // we don't care about the destination - just the path in between

        String direction;
        if (firstClickCoords.i() == secondClickCoords.i()) direction = "horizontal";
        else if (firstClickCoords.j() == secondClickCoords.j()) direction = "vertical";
        else direction = "diagonal";

        boolean clearPath = true;

        if (firstPiece instanceof Pawn) clearPath = pawnCheck(firstClickCoords, secondClickCoords);
        else {
            switch (direction) {
                case "horizontal" -> clearPath = horizontalCheck(possibleMoves, firstClickCoords, secondClickCoords);
                case "vertical" -> clearPath = verticalCheck(possibleMoves, firstClickCoords, secondClickCoords);
                case "diagonal" -> {
                    if (firstPiece instanceof Queen) { // When moving diagonally, we only need to consider a limited
                        // subset of possible moves for a queen
                        possibleMoves = filterQueenMoves(possibleMoves, firstClickCoords);
                    } else if (firstPiece instanceof King) { // Same as above for king
                        filterKingMoves(possibleMoves, firstClickCoords);
                    }
                    clearPath = diagonalCheck(possibleMoves, firstClickCoords, secondClickCoords);
                }
            }
        }
        return clearPath;
    }

    /**
     * This method determines the possible moves that can be made by the Queen.
     *
     * @param possibleMoves     a Set of Integer Lists of possible moves
     * @param firstClickCoords  a compoundKey of x,y coords for the first click
     * @return                  an updated Set of Integer Lists of possible moves
     */
    public Set<List<Integer>> filterQueenMoves(Set<List<Integer>> possibleMoves, compoundKey firstClickCoords) {
        Piece firstPiece = boardGrid.get(firstClickCoords.i()).get(firstClickCoords.j());
        Bishop bishop = new Bishop(firstClickCoords.i(), firstClickCoords.j(), firstPiece.getColor());
        possibleMoves = bishop.moveSet();
        return possibleMoves;
    }

    /**
     * This method determines the possible moves that can be made by the King.
     * Note: If a king is moving diagonally, we don't need to check their horizontal or vertical move
     *
     * @param possibleMoves     a Set of Integer Lists of possible moves
     * @param firstClickCoords  a compoundKey of x,y coords for the first click
     */
    public void filterKingMoves(Set<List<Integer>> possibleMoves, compoundKey firstClickCoords) {
        possibleMoves.removeIf(move -> move.get(0) == firstClickCoords.i() || move.get(1) == firstClickCoords.j());
    }

    /**
     * This method  will determine if the second attempted move is legal.
     *
     * @param firstClickCoords  a compoundKey of x,y coords for the first click
     * @param secondClickCoords a compoundKey of x,y coords for the second click
     * @return                  a boolean, true if move is legal, else false
     */
    public boolean legalSecondMove(compoundKey firstClickCoords, compoundKey secondClickCoords) {
    	
    	Piece firstPiece = boardGrid.get(firstClickCoords.i()).get(firstClickCoords.j());
        Piece secondPiece = boardGrid.get(secondClickCoords.i()).get(secondClickCoords.j());
        Set<List<Integer>> possibleMoves = firstPiece.moveSet();
        String enemyColor = whiteTurn ? black : white;
        String color = whiteTurn ? white : black;
        
        boolean castle = isCastlePossible(firstPiece, secondPiece, firstClickCoords, secondClickCoords, color,  enemyColor);
        
        if (firstPiece instanceof Pawn) return pawnCheck(firstClickCoords, secondClickCoords);
        
        if (!possibleMoves.contains(Arrays.asList(secondClickCoords.i(), secondClickCoords.j())) ||
                (secondPiece != null && firstPiece.getColor() == secondPiece.getColor())) {
            return castle;
        }
        return true;
    }




    /**
     * If both the first and second proposed clicks are deemed valid, make the move.
     *
     * @param firstClickCoords      a compoundKey storing the coordinates where the player first clicked
     * @param secondClickCoords     a compoundKey storing the coordinates where the player clicked second
     * @return                      a boolean, true if move is legal, else false
     */
    public boolean makeMove(compoundKey firstClickCoords, compoundKey secondClickCoords) {
        boolean clearPath = false;
        boolean check = false;
        String enemyColor = whiteTurn ? black : white;
        String color = whiteTurn ? white : black;
        Piece firstPiece = boardGrid.get(firstClickCoords.i()).get(firstClickCoords.j());
        Piece secondPiece = boardGrid.get(secondClickCoords.i()).get(secondClickCoords.j());
        Set<List<Integer>> possibleMoves = firstPiece.moveSet();

        // need to check castle here
        boolean castle = checkCastle(firstPiece, secondPiece, firstClickCoords, secondClickCoords, color, enemyColor);
        if (castle) {
            whiteTurn = !whiteTurn;
            return true; //true
        }

        if (!possibleMoves.contains(Arrays.asList(secondClickCoords.i(), secondClickCoords.j())) ||
                (secondPiece != null && firstPiece.getColor() == secondPiece.getColor())) {
            return false; //false
        }

        if (firstPiece instanceof Knight)
            clearPath = true;
        else
            clearPath = checkPath(firstClickCoords, secondClickCoords);

        if (clearPath) {
            executeMove(firstClickCoords, secondClickCoords, firstPiece, secondPiece);
            check = inCheck(color, enemyColor);
            // if we are in check we need to undo the move and return false
            if (check) {
                // undo move
                undoMove(firstClickCoords, secondClickCoords, firstPiece, secondPiece, enemyColor);
                return false; //false
            }
            // sets the first piece to a queen if pawn promoted
            firstPiece = checkPromotePawn(firstPiece, secondClickCoords);
            Move before = new Move(firstClickCoords.i(), firstClickCoords.j(), "");
            Move after = new Move(secondClickCoords.i(), secondClickCoords.j(), firstPiece.getIcon());

            // see if our move put the opponent in checkmate
            boolean checkMate = false;
            if (inCheck(enemyColor, color)) {
                checkMate = checkCheckMate(enemyColor, color);
            }
            
            
            setChanged();
            notifyObservers(new ChessMoveMessage(before, after, checkMate, false, null, null));
            
            
            whiteTurn = !whiteTurn;
            return true; //true
        }
        return false; //false
    }


    /**
     * This method will send the move info for a networked game, which operates
     * differently from the local version. It will check the first and second click
     * coords, then return a MoveMessage which contains info about the attempted
     * moves, the pieces involved, and the game status (see ChessMoveMessage)
     *
     * @param firstClickCoords  a compoundKey with x,y coords of first click
     * @param secondClickCoords a compoundKey with x,y coords of second click
     * @return                  a ChessMoveMessage with info about the attempted move
     */
    public ChessMoveMessage networkedMove(compoundKey firstClickCoords, compoundKey secondClickCoords) {
        boolean clearPath = false;
        boolean check = false;
        String enemyColor = whiteTurn ? black : white;
        String color = whiteTurn ? white : black;
        Piece firstPiece = boardGrid.get(firstClickCoords.i()).get(firstClickCoords.j());
        Piece secondPiece = boardGrid.get(secondClickCoords.i()).get(secondClickCoords.j());
        Set<List<Integer>> possibleMoves = firstPiece.moveSet();

        // todo fix networked castle
        // need to check castle here
        boolean castle = checkCastle(firstPiece, secondPiece, firstClickCoords, secondClickCoords, color, enemyColor);
        if (castle) {
            whiteTurn = !whiteTurn;
            return null; //true
        }


        if (!possibleMoves.contains(Arrays.asList(secondClickCoords.i(), secondClickCoords.j())) ||
                (secondPiece != null && firstPiece.getColor() == secondPiece.getColor())) {
            return null; //false
        }

        if (firstPiece instanceof Knight)
            clearPath = true;
        else
            clearPath = checkPath(firstClickCoords, secondClickCoords);

        if (clearPath) {
            executeMove(firstClickCoords, secondClickCoords, firstPiece, secondPiece);
            check = inCheck(color, enemyColor);
            // if we are in check we need to undo the move and return false
            if (check) {
                // undo move
                undoMove(firstClickCoords, secondClickCoords, firstPiece, secondPiece, enemyColor);
                return null; //false
            }
            // sets the first piece to a queen if pawn promoted
            firstPiece = checkPromotePawn(firstPiece, secondClickCoords);
            Move before = new Move(firstClickCoords.i(), firstClickCoords.j(), "");
            Move after = new Move(secondClickCoords.i(), secondClickCoords.j(), firstPiece.getIcon());

            // see if our move put the opponent in checkmate
            boolean checkMate = false;
            if (inCheck(enemyColor, color)) {
                checkMate = checkCheckMate(enemyColor, color);
            }
            
            whiteTurn = !whiteTurn;
            setChanged();
            notifyObservers(new ChessMoveMessage(before, after, checkMate, false, null, null));
            return new ChessMoveMessage(before, after, checkMate, false, null, null); //true
        }
        return null; //false
    }

    /**
     * Check if a castle move is possible.
     *
     * @param firstPiece        the first game Piece
     * @param secondPiece       the second game Piece
     * @param firstClickCoords  a compoundKey with x,y coords of first click
     * @param secondClickCoords a compoundKey with x,y coords of second click
     * @param color             a String representing the player piece color
     * @param enemyColor        a String representing the enemy piece color
     * @return                  a boolean, true if castle is possible, else false
     */
    private boolean checkCastle(Piece firstPiece, Piece secondPiece, compoundKey firstClickCoords,
                                compoundKey secondClickCoords, String color, String enemyColor) {

        if (!isCastlePossible(firstPiece,secondPiece,firstClickCoords,secondClickCoords,color,enemyColor))
            return false;
        
        Piece rook;
        Piece king;
        if (firstPiece instanceof Rook) {
            rook = firstPiece;
            king = secondPiece;
        } else {
            rook = secondPiece;
            king = firstPiece;
        }
        compoundKey rookCoords = new compoundKey(rook.getRow(), rook.getColumn());
        compoundKey kingCoords = new compoundKey(king.getRow(), king.getColumn());
        if (checkPath(rookCoords, kingCoords)) {
            int dist = Math.abs(rook.getColumn() - king.getColumn());
            boardGrid.get(king.getRow()).set(king.getColumn(), null);
            boardGrid.get(rook.getRow()).set(rook.getColumn(), null);
            if (dist == 3) {
                king.move(king.getRow(), king.getColumn() + 2);
                rook.move(rook.getRow(), rook.getColumn() - 2);
            } else {
                king.move(king.getRow(), king.getColumn() - 2);
                rook.move(rook.getRow(), rook.getColumn() + 3);
            }
            boardGrid.get(king.getRow()).set(king.getColumn(), king);
            boardGrid.get(rook.getRow()).set(rook.getColumn(), rook);
            boolean castleCheck = inCheck(color, enemyColor);
            if (castleCheck) {
                boardGrid.get(king.getRow()).set(king.getColumn(), null);
                boardGrid.get(rook.getRow()).set(rook.getColumn(), null);
                king.move(kingCoords.i(), kingCoords.j());
                rook.move(rookCoords.i(), rookCoords.j());
                boardGrid.get(king.getRow()).set(king.getColumn(), king);
                boardGrid.get(rook.getRow()).set(rook.getColumn(), rook);
                return false;
            }
            Move before = new Move(firstClickCoords.i(), firstClickCoords.j(), "");
            Move after = new Move(secondClickCoords.i(), secondClickCoords.j(), "");
            Move kingMove = new Move(king.getRow(), king.getColumn(), king.getIcon());
            Move rookMove = new Move(rook.getRow(), rook.getColumn(), rook.getIcon());
            setChanged();
            notifyObservers(new ChessMoveMessage(before, after, false, true, rookMove, kingMove));
            return true;
        }
        return false;

    }

    /**
     * A getter method for the status of the white player's turn.
     *
     * @return  a boolean, true if it is white's turn, else false
     */
    public boolean getWhiteTurn() {
    	return whiteTurn;
    }

    /**
     * Check if a castle move is possible.
     *
     * @param firstPiece        the first game Piece
     * @param secondPiece       the second game Piece
     * @param firstClickCoords  a compoundKey with x,y coords of first click
     * @param secondClickCoords a compoundKey with x,y coords of second click
     * @param color             a String representing the player piece color
     * @param enemyColor        a String representing the enemy piece color
     * @return                  a boolean, true if castle is possible, else false
     */
    private boolean isCastlePossible(Piece firstPiece, Piece secondPiece, compoundKey firstClickCoords,
                                     compoundKey secondClickCoords, String color, String enemyColor) {
        return ((secondPiece != null) &&
                (firstPiece.getColor() == secondPiece.getColor()) &&
                (!firstPiece.getMadeFirstMove() && !secondPiece.getMadeFirstMove()) &&
                (((firstPiece instanceof King) && (secondPiece instanceof Rook)) ||
                        ((firstPiece instanceof Rook) && (secondPiece instanceof King))) &&
                !(inCheck(color, enemyColor)));
    }

    /**
     * Place a game piece on the board.
     *
     * @param secondPiece   a game Piece
     * @param color         a String, the color of the piece
     */
    private void restorePieceInMap(Piece secondPiece, String color) {
        if (secondPiece == null)
            return;
        pieceMap.get(color).add(secondPiece);
    }

    /**
     * Check if a pawn can be promoted.
     *
     * @param firstPiece        a game Piece, check if pawn
     * @param secondClickCoords a compoundKey with x,y coords of second click
     * @return                  a Piece, may become a Queen
     */
    private Piece checkPromotePawn(Piece firstPiece, compoundKey secondClickCoords) {
        if (firstPiece instanceof Pawn && firstPiece.moveSet().isEmpty()) {
            firstPiece = new Queen(firstPiece.getRow(), firstPiece.getColumn(), firstPiece.getColor());
            boardGrid.get(secondClickCoords.i()).set(secondClickCoords.j(), firstPiece);
            mapPawnToQueen(firstPiece);
        }
        return firstPiece;
    }

    /**
     * Undo the last move done.
     *
     * @param firstClickCoords      a compoundKey with x,y coords of first click
     * @param secondClickCoords     a compoundKey with x,y coords of second click
     * @param firstPiece            a game Piece
     * @param secondPiece           a game Piece
     * @param enemyColor            a String, color of the player NOT making the move
     */
    private void undoMove(compoundKey firstClickCoords, compoundKey secondClickCoords, Piece firstPiece,
                          Piece secondPiece, String enemyColor) {
        firstPiece.move(firstClickCoords.i(), (firstClickCoords.j()));
        boardGrid.get(firstClickCoords.i()).set(firstClickCoords.j(), firstPiece);
        boardGrid.get(secondClickCoords.i()).set(secondClickCoords.j(), secondPiece);
        restorePieceInMap(secondPiece, enemyColor);

    }

    /**
     * Make the proposed move.
     *
     * @param firstClickCoords      a compoundKey with x,y coords of first click
     * @param secondClickCoords     a compoundKey with x,y coords of second click
     * @param firstPiece            a game Piece
     * @param secondPiece           a game Piece
     */
    private void executeMove(compoundKey firstClickCoords, compoundKey secondClickCoords, Piece firstPiece,
                             Piece secondPiece) {
        boardGrid.get(firstClickCoords.i()).set(firstClickCoords.j(), null);
        firstPiece.move(secondClickCoords.i(), secondClickCoords.j());
        boardGrid.get(secondClickCoords.i()).set(secondClickCoords.j(), firstPiece);
        // remove second piece from piece map
        removePiecefromMap(secondPiece);
    }

    /**
     * Determine if CheckMate is made.
     *
     * @param colorToCheck  a String to check enemy color
     * @param currentColor  a String to check current player color
     * @return              a boolean, true if Checkmate, else false
     */
    private boolean checkCheckMate(String colorToCheck, String currentColor) {
        // High level we are checking the opposite player
        // first need to get all possible moves from the enemy pieces
        List<completeMove> validMoves = new ArrayList<>();
        for (Piece piece : pieceMap.get(colorToCheck)) {
            for (List<Integer> move : piece.moveSet()) {
                compoundKey startMove = new compoundKey(piece.getRow(), piece.getColumn());
                compoundKey possibleMove = new compoundKey(move.get(0), move.get(1));
                Piece firstPiece = boardGrid.get(startMove.i()).get(startMove.j());
                Piece secondPiece = boardGrid.get(possibleMove.i()).get(possibleMove.j());
                if (secondPiece != null && firstPiece.getColor() == secondPiece.getColor())
                    continue;
                if (piece instanceof Knight || checkPath(startMove, possibleMove))
                    validMoves.add(new completeMove(startMove, possibleMove));
            }
        }
        // at this point we have every valid move added to the set now we need to check each move and see if we make it
        // does it take us out of check and if no move does then it's checkmate
//        boolean inCheck = true;
        for (completeMove move : validMoves) {
            // make move
            compoundKey start = move.start();
            compoundKey end = move.end();
            Piece startPiece = boardGrid.get(start.i()).get(start.j());
            Piece endPiece = boardGrid.get(end.i()).get(end.j());
            boardGrid.get(start.i()).set(start.j(), null);
            startPiece.move(end.i(), end.j());
            boardGrid.get(end.i()).set(end.j(), startPiece);
            removePiecefromMap(endPiece);
            // if move takes us out of check return false
//            if (!inCheck())
            if (!inCheck(colorToCheck, currentColor)) {
                startPiece.move(start.i(), start.j());
                boardGrid.get(start.i()).set(start.j(), startPiece);
                boardGrid.get(end.i()).set(end.j(), endPiece);
                restorePieceInMap(endPiece, currentColor);
                return false;
            } else {
                startPiece.move(start.i(), start.j());
                boardGrid.get(start.i()).set(start.j(), startPiece);
                boardGrid.get(end.i()).set(end.j(), endPiece);
                restorePieceInMap(endPiece, currentColor);
            }

        }
        return true;
    }

    /**
     * See if eney is in check.
     *
     * @param color         a String, the current player's color
     * @param enemyColor    a String, the enemy's color
     * @return              a boolean, true if in check, else false
     */
    private boolean inCheck(String color, String enemyColor) {
        Piece kingPiece = kingMap.get(color);
        compoundKey king = new compoundKey(kingPiece.getRow(), kingPiece.getColumn());
        for (Piece piece : pieceMap.get(enemyColor)) {
            compoundKey attackPiece = new compoundKey(piece.getRow(), piece.getColumn());
            if (checkClearPath(attackPiece, king)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a proposed move's path is clear to be made.
     *
     * @param attackPiece   a compoundKey, x,y coords of attackPiece
     * @param king          a compoundKey, x,y coords of king
     * @return              a boolean, true if path is clear, else false
     */
    private boolean checkClearPath(compoundKey attackPiece, compoundKey king) {
        Piece firstPiece = boardGrid.get(attackPiece.i()).get(attackPiece.j());
        Set<List<Integer>> possibleMoves = firstPiece.moveSet();
        if (!possibleMoves.contains(Arrays.asList(king.i(), king.j())))
            return false;
        return checkPath(attackPiece, king);
    }

    /**
     * Get the piece (represented by a string) at the given coordinates.
     *
     * @param i     an int, x coordinate
     * @param j     an int, y coordinate
     * @return      a String representing the game piece
     */
    public String getPieceString(int i, int j) {
        Piece piece = boardGrid.get(i).get(j);
        if (piece == null)
            return "";
        return piece.getIcon();
    }

    /**
     * Map the pawn to the queen.
     *
     * @param firstPiece    a game Piece
     */
    private void mapPawnToQueen(Piece firstPiece) {
        String color = firstPiece.getColor() ? white : black;
        pieceMap.get(color)
                .removeIf(piece -> piece.getRow() == firstPiece.getRow() && piece.getColumn() == firstPiece.getColumn());
        pieceMap.get(color).add(firstPiece);
    }

    /**
     * This method will remove a piece from the map
     *
     * @param piece     a game piece
     */
    private void removePiecefromMap(Piece piece) {
        if (piece == null)
            return;
        if (piece.getColor()) {
            pieceMap.get(white).removeIf(whitePiece -> whitePiece.getRow() == piece.getRow() &&
                    whitePiece.getColumn() == piece.getColumn());
        } else {
            pieceMap.get(black).removeIf(blackPiece -> blackPiece.getRow() == piece.getRow() &&
                    blackPiece.getColumn() == piece.getColumn());
        }
    }

    /**
     * Get a Set of compoundKeys representing valid moves that can be made from
     * the x,y coords of the first click.
     *
     * @param firstClick    a compoundKey, x,y coords of first click
     * @return              a Set of compoundKeys with valid moves
     */
    public Set<compoundKey> getValidMoves(compoundKey firstClick) {
        Piece firstPiece = boardGrid.get(firstClick.i()).get(firstClick.j());
        Set<compoundKey> moveSet = new HashSet<>();
        compoundKey startMove = new compoundKey(firstPiece.getRow(), firstPiece.getColumn());
        for (List<Integer> move : firstPiece.moveSet()) {
            compoundKey possibleMove = new compoundKey(move.get(0), move.get(1));
            Piece secondPiece = boardGrid.get(possibleMove.i()).get(possibleMove.j());
            if (secondPiece != null && firstPiece.getColor() == secondPiece.getColor())
                continue;
            if (firstPiece instanceof Knight || checkPath(startMove, possibleMove))
                moveSet.add(possibleMove);
        }
        return moveSet;


    }

    public List<List<Piece>> getBoardGrid(){
        return boardGrid;
    }

    /**
     * A way to store info about a piece and its coordinates.
     */
    public record Move(int x, int y, String piece) implements Serializable {
}

    /**
     * A way to store the start and end coordinates of a move.
     */
    private record completeMove(compoundKey start, compoundKey end) {
}


}