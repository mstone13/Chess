package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    //create an array (matrix)
    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }
    //add a piece to a certain position on the matrix
    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }
    //look in the matrix, return the piece in that position if there is one
    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //reset board to null
        for (int row = 1; row < 8; row++){
            for (int col = 1; col < 8; col++) {
                ChessPosition squarePosition = new ChessPosition(row, col);
                addPiece(squarePosition, null);
            }
        }
        for (int pawnCol = 1; pawnCol < 8; pawnCol ++){
            int whiteRow = 2;
            int blackRow = 7;
            //add white pawns to entire row
            ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            ChessPosition whitePawnPos = new ChessPosition(whiteRow, pawnCol);
            addPiece(whitePawnPos, whitePawn);

            //add black pawns to entire row
            ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            ChessPosition blackPawnPos = new ChessPosition(blackRow, pawnCol);
            addPiece(blackPawnPos, blackPawn);
        }

        List<ChessPiece.PieceType> pieceTypes = new ArrayList<>();
        pieceTypes.add(ChessPiece.PieceType.ROOK);
        pieceTypes.add(ChessPiece.PieceType.KNIGHT);
        pieceTypes.add(ChessPiece.PieceType.BISHOP);
        pieceTypes.add(ChessPiece.PieceType.QUEEN);
        pieceTypes.add(ChessPiece.PieceType.KING);
        pieceTypes.add(ChessPiece.PieceType.BISHOP);
        pieceTypes.add(ChessPiece.PieceType.KNIGHT);
        pieceTypes.add(ChessPiece.PieceType.ROOK);

        int col = 1;
        for (ChessPiece.PieceType type : pieceTypes){
            //add white pieces to entire row
            ChessPiece whitePiece = new ChessPiece(ChessGame.TeamColor.WHITE, type);
            ChessPosition whitePiecePos = new ChessPosition(1, col);
            addPiece(whitePiecePos, whitePiece);

            //add black pieces to entire row
            ChessPiece blackPiece = new ChessPiece(ChessGame.TeamColor.BLACK, type);
            ChessPosition blackPiecePos = new ChessPosition(8, col);
            addPiece(blackPiecePos, blackPiece);
            col++;
        }
    }
    //send pieces to their designated starting positions
}
