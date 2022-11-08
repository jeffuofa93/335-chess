package testing;

import controller.ChessController;
import model.ChessModel;
import model.ChessMoveMessage;
import model.Pieces.Piece;
import model.empty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import view.ChessGUIView;
import view.ChessGUIView.compoundKey;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class provides testing for all the non-gui non networked elements of the code base. The goal of the tests is to
 * provide insight and hopefully find areas of the program where the code could fell and give more confidence in the
 * established code base.
 */
public class ChessModelTests {
    private final Map<String, Integer> xrowMap = xRowMap();
    private final Map<Integer, Integer> yrowMap = yRowMap();

    /**
     * Plays a game of chess using the given sequence of moves. After the moves have been played, the state of the game
     * will be compared to that of a given save file.
     *
     * @param moves the sequence of moves, where each move is a pair of coordinates: an origin and a destination.
     */
    private String play(compoundKey[][] moves) {
        ChessModel model = null;
        try {
            model = new ChessModel();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        ChessController controller = new ChessController(model);

        for (compoundKey[] move : moves) {
            compoundKey from = move[0];
            compoundKey to = move[1];
            if (controller.legalFirstClick(from) && controller.legalSecondMove(from, to)) {
                String fromPiece = controller.getPieceString(from.i(), from.j());
                String toPiece = controller.getPieceString(to.i(), to.j());
                System.out.print("\n\n" + fromPiece + "[" + from.i() + "," + from.j() + "] -> " + toPiece + "[" +
                        to.i() + "," + to.j() + "]");
                controller.makeMove(from, to);
            } else {
                fail();
            }
        }
        return controller.saveGame();
    }

    /**
     * Compares the given save game string from ChessController to the contents of a save file, which serves as the
     * baseline.
     *
     * @param baseSaveFilename the name of the save file that the save string will be compared against.
     * @param saveString       the ChessController save string that typically comes from controller.saveGame().
     */
    private void compare(String baseSaveFilename, String saveString) {
        try {
            Scanner reader = new Scanner(new File(baseSaveFilename));
            StringBuilder sb = new StringBuilder();
            while (reader.hasNext()) {
                sb.append(reader.nextLine() + "\n");
            }
            String expectedState = sb.toString();
            assertEquals(expectedState, saveString);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * This method tests the castle operation in the model vs the expected board state at the end of the given moves
     */
    @Test
    public void testCastle() {
        // The sequence of moves starting with white
        ChessGUIView.compoundKey[][] moves = {
                {new compoundKey(7, 1), new compoundKey(5, 0)}, // white knight
                {new compoundKey(1, 0), new compoundKey(3, 0)}, // black pawn
                {new compoundKey(6, 1), new compoundKey(4, 1)}, // white pawn
                {new compoundKey(1, 1), new compoundKey(3, 1)}, // black pawn
                {new compoundKey(7, 2), new compoundKey(6, 1)}, // white bishop
                {new compoundKey(1, 2), new compoundKey(3, 2)}, // black pawn
                {new compoundKey(6, 2), new compoundKey(4, 2)}, // white pawn
                {new compoundKey(1, 3), new compoundKey(3, 3)}, // black pawn
                {new compoundKey(7, 3), new compoundKey(6, 2)}, // white queen
                {new compoundKey(1, 4), new compoundKey(3, 4)}, // black pawn
                {new compoundKey(7, 4), new compoundKey(7, 0)}, // white king castle
        };

        compare("src/testing/castle.txt", play(moves));
    }

    @Test
    public void amazon(){

        assertSame((int)10.75, 10.75);
    }

    /**
     * This method tests the capture of pieces using the compare assert and the play method
     */
    @Test
    public void testCapturePiece() {
        // The sequence of moves starting with white
        ChessGUIView.compoundKey[][] moves = {
                {new compoundKey(6, 3), new compoundKey(4, 3)}, // white pawn
                {new compoundKey(1, 4), new compoundKey(3, 4)}, // black pawn
                {new compoundKey(4, 3), new compoundKey(3, 4)}, // white pawn captures black pawn
                {new compoundKey(1, 3), new compoundKey(3, 3)}, // black pawn
                {new compoundKey(3, 4), new compoundKey(2, 4)}, // white pawn
                {new compoundKey(1, 5), new compoundKey(2, 4)}, // black pawn captures white pawn
        };

        compare("src/testing/capture.txt", play(moves));
    }

    /**
     * This method tests pawn promotion using the play method and compare assert
     */
    @Test
    public void testPromotePawn() {
        // The sequence of moves starting with white
        ChessGUIView.compoundKey[][] moves = {
                {new compoundKey(6, 2), new compoundKey(4, 2)}, // white pawn
                {new compoundKey(1, 1), new compoundKey(3, 1)}, // black pawn
                {new compoundKey(4, 2), new compoundKey(3, 1)}, // white pawn captures black pawn
                {new compoundKey(0, 1), new compoundKey(2, 2)}, // black knight
                {new compoundKey(3, 1), new compoundKey(2, 1)}, // white pawn
                {new compoundKey(1, 0), new compoundKey(3, 0)}, // black pawn
                {new compoundKey(2, 1), new compoundKey(1, 1)}, // white pawn
                {new compoundKey(3, 0), new compoundKey(4, 0)}, // black pawn
                {new compoundKey(1, 1), new compoundKey(0, 1)}, // white pawn
        };

        compare("src/testing/pawn.txt", play(moves));
    }

    /**
     * This method tests the checkmate functionality
     */
    @Test
    public void testCheckmate() {
        // The sequence of moves starting with white
        ChessGUIView.compoundKey[][] moves = {
                {new compoundKey(6, 5), new compoundKey(4, 5)}, // white pawn
                {new compoundKey(1, 4), new compoundKey(3, 4)}, // black pawn
                {new compoundKey(6, 4), new compoundKey(4, 4)}, // white pawn
                {new compoundKey(0, 3), new compoundKey(4, 7)}, // black queen checkmate
        };

        compare("src/testing/checkmate.txt", play(moves));
    }

    /**
     * This method tests the piece move set functionality specifically for the queen
     */
    @Test
    public void testMoveSet() {
        ChessModel model = null;
        try {
            model = new ChessModel();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        ChessController controller = new ChessController(model);

        controller.loadGame(new File("src/testing/castle.txt"), true);
        Set<compoundKey> kingMoveSet = Set.of(
                new compoundKey(7, 1)
        );
        assertEquals(kingMoveSet, controller.getValidMoveSet(new compoundKey(7, 2)));

        Set<compoundKey> queenMoveSet = Set.of(
                new compoundKey(5, 1),
                new compoundKey(4, 0),
                new compoundKey(5, 2),
                new compoundKey(5, 3),
                new compoundKey(4, 4),
                new compoundKey(3, 5),
                new compoundKey(2, 6),
                new compoundKey(1, 7),
                new compoundKey(7, 1)
        );
        assertEquals(queenMoveSet, controller.getValidMoveSet(new compoundKey(6, 2)));
    }

    /**
     * This method tests the networked move functionality
     */
    @Test
    public void testNetworkedMove() {
        ChessModel model = null;
        try {
            model = new ChessModel();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        ChessController controller = new ChessController(model);

        // The sequence of moves starting with white
        ChessGUIView.compoundKey[][] moves = {
                {new compoundKey(7, 1), new compoundKey(5, 0)}, // white knight
                {new compoundKey(1, 0), new compoundKey(3, 0)}, // black pawn
                {new compoundKey(6, 1), new compoundKey(4, 1)}, // white pawn
                {new compoundKey(1, 1), new compoundKey(3, 1)}, // black pawn
                {new compoundKey(7, 2), new compoundKey(6, 1)}, // white bishop
                {new compoundKey(1, 2), new compoundKey(3, 2)}, // black pawn
                {new compoundKey(6, 2), new compoundKey(4, 2)}, // white pawn
                {new compoundKey(1, 3), new compoundKey(3, 3)}, // black pawn
                {new compoundKey(7, 3), new compoundKey(6, 2)}, // white queen
                {new compoundKey(1, 4), new compoundKey(3, 4)}, // black pawn
                {new compoundKey(7, 4), new compoundKey(7, 0)}, // white king castle
        };

        for (compoundKey[] move : moves) {
            model.networkedMove(move[0], move[1]);
        }

        compare("src/testing/castle.txt", controller.saveGame());
    }

    /**
     * This method tests the ability to save and load a game
     */
    @Test
    public void testSaveLoadGame() {
        ChessController controller = null;
        try {
            ChessModel model = new ChessModel();
            controller = new ChessController(model);

            FileWriter saveFileWriter = new FileWriter("src/testing/save.txt");
            saveFileWriter.write(controller.saveGame());
            saveFileWriter.close();
        } catch (IOException e) {
            fail();
        }
        controller.loadGame(new File("src/testing/save.txt"), true);
    }

    /**
     * This method tests a variety of different turn logic in the controller and model by swapping turns and manually
     * changing the turn state
     *
     * @throws IOException
     */
    @Test
    public void testControllerTurn() throws IOException {
        ChessModel model = new ChessModel();
        ChessController controller = new ChessController(model);
        ChessGUIView.compoundKey[][] moves = {
                {new compoundKey(6, 0), new compoundKey(4, 0)}, // white pawn
                {new compoundKey(0, 1), new compoundKey(2, 2)},    // black knight
                {new compoundKey(6, 2), new compoundKey(4, 2)},    // white pawn
                {new compoundKey(2, 2), new compoundKey(4, 1)},    // black knight
                {new compoundKey(7, 3), new compoundKey(5, 1)},    // white queen
                {new compoundKey(1, 3), new compoundKey(3, 3)},    // black pawn
                {new compoundKey(5, 1), new compoundKey(4, 1)},    // white queen
                {new compoundKey(0, 2), new compoundKey(4, 6)},    // black bishop
        };
        boolean whiteTurn = true;
        assertFalse(controller.networked());
        for (ChessGUIView.compoundKey[] move : moves) {
            compoundKey from = move[0];
            compoundKey to = move[1];
            assertEquals(controller.isWhiteTurn(), whiteTurn);
            assertTrue(controller.isMyTurn());
            model.flipMyTurn();
            assertFalse(controller.isMyTurn());
            model.flipMyTurn();
            assertTrue(controller.legalFirstClick(from));
            assertTrue(controller.legalSecondMove(from, to));
            controller.setWhiteTurn(!whiteTurn);
            assertNotEquals(controller.isWhiteTurn(), whiteTurn);
            controller.setWhiteTurn(whiteTurn);
            controller.makeMove(from, to);
            whiteTurn = !whiteTurn;
        }
    }

    /**
     * This method tests the save game functionality against the given game string
     *
     * @throws IOException
     */
    @Test
    public void testSaveGame() throws IOException {
        ChessModel model = new ChessModel();
        ChessGUIView.compoundKey[][] moves = {
                {new compoundKey(6, 0), new compoundKey(4, 0)}, // white pawn
                {new compoundKey(0, 1), new compoundKey(2, 2)},    // black knight
                {new compoundKey(6, 2), new compoundKey(4, 2)},    // white pawn
                {new compoundKey(2, 2), new compoundKey(4, 1)},    // black knight
                {new compoundKey(7, 3), new compoundKey(5, 1)},    // white queen
                {new compoundKey(1, 3), new compoundKey(3, 3)},    // black pawn
                {new compoundKey(5, 1), new compoundKey(4, 1)},    // white queen
                {new compoundKey(0, 2), new compoundKey(4, 6)},    // black bishop
        };
        for (ChessGUIView.compoundKey[] move : moves) {
            compoundKey from = move[0];
            compoundKey to = move[1];
            model.makeMove(from, to);
        }
        String expectedGameBoard =
                """
                        true
                        0 0 R false
                        0 3 Q false
                        0 4 K false
                        0 5 B false
                        0 6 Kn false
                        0 7 R false
                        1 0 P false
                        1 1 P false
                        1 2 P false
                        1 4 P false
                        1 5 P false
                        1 6 P false
                        1 7 P false
                        3 3 P false
                        4 0 P true
                        4 1 Q true
                        4 2 P true
                        4 6 B false
                        6 1 P true
                        6 3 P true
                        6 4 P true
                        6 5 P true
                        6 6 P true
                        6 7 P true
                        7 0 R true
                        7 1 Kn true
                        7 2 B true
                        7 4 K true
                        7 5 B true
                        7 6 Kn true
                        7 7 R true
                        						""";
        assertEquals(expectedGameBoard, model.saveGame());
    }

    /**
     * This method tests the getFirstMove functionality for each piece
     *
     * @throws IOException
     */
    @Test
    public void testPieceMadeFirstMove() throws IOException {
        ChessModel model = new ChessModel();
        ChessController controller = new ChessController(model);
        ChessGUIView.compoundKey[][] moves = {
                {new compoundKey(6, 0), new compoundKey(4, 0)}, // white pawn
                {new compoundKey(0, 1), new compoundKey(2, 2)},    // black knight
                {new compoundKey(6, 2), new compoundKey(4, 2)},    // white pawn
                {new compoundKey(2, 2), new compoundKey(4, 1)},    // black knight
                {new compoundKey(7, 3), new compoundKey(5, 1)},    // white queen
                {new compoundKey(1, 3), new compoundKey(3, 3)},    // black pawn
                {new compoundKey(5, 1), new compoundKey(4, 1)},    // white queen
                {new compoundKey(0, 2), new compoundKey(4, 6)},    // black bishop
        };
        Set<compoundKey> movedPiece = new HashSet<>();
        for (ChessGUIView.compoundKey[] move : moves) {
            compoundKey from = move[0];
            compoundKey to = move[1];
            movedPiece.add(to);
        }

        for (ChessGUIView.compoundKey[] move : moves) {
            compoundKey from = move[0];
            compoundKey to = move[1];
            model.makeMove(from, to);
        }
        List<List<Piece>> boardGrid = model.getBoardGrid();
        for (int i = 0, boardGridSize = boardGrid.size(); i < boardGridSize; i++) {
            List<Piece> pieceList = boardGrid.get(i);
            for (int j = 0, pieceListSize = pieceList.size(); j < pieceListSize; j++) {
                Piece piece = pieceList.get(j);
                if (piece == null)
                    continue;
                compoundKey cur = new compoundKey(i, j);
                if (movedPiece.contains(cur))
                    assertTrue(piece.getMadeFirstMove());
                else
                    assertFalse(piece.getMadeFirstMove());
            }
        }
    }

    /**
     * This method checks first that the player cannot make a move if the move puts the player in check and secondly it
     * tests the functionality of the ChessMoveMessage by representing the moves as ChessMoveMessages
     *
     * @throws IOException
     */
    @Test
    public void testInvalidMoveIntoCheckMoveMessage() throws IOException {
        ChessModel model = new ChessModel();
        List<ChessMoveMessage> moves = new ArrayList<>(Arrays.asList(
                new ChessMoveMessage(new ChessModel.Move(yrowMap.get(2), xrowMap.get("F"), ""),
                        new ChessModel.Move(yrowMap.get(4), xrowMap.get("F"), ""), false, false, null, null),

                new ChessMoveMessage(new ChessModel.Move(yrowMap.get(7), xrowMap.get("E"), ""),
                        new ChessModel.Move(yrowMap.get(5), xrowMap.get("E"), ""), false, false, null, null),

                new ChessMoveMessage(new ChessModel.Move(yrowMap.get(2), xrowMap.get("G"), ""),
                        new ChessModel.Move(yrowMap.get(3), xrowMap.get("G"), ""), false, false, null, null),

                new ChessMoveMessage(new ChessModel.Move(yrowMap.get(8), xrowMap.get("D"), ""),
                        new ChessModel.Move(yrowMap.get(4), xrowMap.get("H"), ""), false, false, null, null),
                // invalid move, move into check
                new ChessMoveMessage(new ChessModel.Move(yrowMap.get(3), xrowMap.get("G"), ""),
                        new ChessModel.Move(yrowMap.get(4), xrowMap.get("G"), ""), false, false, null, null)
        ));
        boolean moveResult = true;
        for (ChessMoveMessage move : moves) {
            moveResult = model.makeMove(move.firstMoveCoordinateKey(), move.secondMoveCoordinateKey());
        }
        assertFalse(moveResult);
    }

    /**
     * This method tests the functionality of the ChessMoveMessage by creating a basic move and testing the moveSet from
     * the move message
     *
     * @throws IOException
     */
    @Test
    public void testMoveMessage() throws IOException {
        ChessModel model = new ChessModel();
        ChessController controller = new ChessController(model);
        ChessMoveMessage message = new ChessMoveMessage(new ChessModel.Move(yrowMap.get(2), xrowMap.get("F"), ""),
                new ChessModel.Move(yrowMap.get(4), xrowMap.get("F"), ""), false, false, null, null);
        compoundKey firstMove = null;
        compoundKey secondMove = null;
        for (ChessModel.Move move : message.getMoveSet()) {
            System.out.println(move);
            if (firstMove == null) {
                firstMove = new compoundKey(move.x(), move.y());
                assertTrue(controller.legalFirstClick(firstMove));
            } else {
                secondMove = new compoundKey(move.x(), move.y());
                controller.setWhiteTurn(true);
                assertTrue(controller.legalSecondMove(firstMove, secondMove));
            }

        }
    }

    /**
     * This method creates a map to convert standard chess x coordinates to our board
     *
     * @return - Map to convert the coordinates
     */
    private Map<String, Integer> xRowMap() {
        Map<String, Integer> xRow = new HashMap<>();
        xRow.put("A", 0);
        xRow.put("B", 1);
        xRow.put("C", 2);
        xRow.put("D", 3);
        xRow.put("E", 4);
        xRow.put("F", 5);
        xRow.put("G", 6);
        xRow.put("H", 7);
        return xRow;
    }

    /**
     * This method creates a map to convert standard chess move y coordinates to our board
     *
     * @return - Map
     */
    private Map<Integer, Integer> yRowMap() {
        Map<Integer, Integer> yrowMap = new HashMap<>();
        yrowMap.put(8, 0);
        yrowMap.put(7, 1);
        yrowMap.put(6, 2);
        yrowMap.put(5, 3);
        yrowMap.put(4, 4);
        yrowMap.put(3, 5);
        yrowMap.put(2, 6);
        yrowMap.put(1, 7);
        return yrowMap;
    }


}
