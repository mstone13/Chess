package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        PieceType type = piece.getPieceType();
//        if (type == PieceType.ROOK){
        return new RookMovesCalculator().pieceMoves(board, myPosition);

    }

    public interface PieceMovesCalculator{
        Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
    }

}

class RookMovesCalculator implements ChessPiece.PieceMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        ChessPiece piece = board.getPiece(position);

        List<ChessPosition> validMoves = new ArrayList<>();

        int row = position.getRow();
        int tempRow = row;
        int col = position.getColumn();
        int tempCol = col;

        while (tempRow < 8) {
            tempRow++;
            ChessPosition tempPosition = new ChessPosition(tempRow, col);
            if (board.getPiece(tempPosition) == null) {
                validMoves.add(tempPosition);
            } else if (board.getPiece(tempPosition).getTeamColor() != piece.getTeamColor()) {
                validMoves.add(tempPosition);
                break;
            } else {
                break;
            }
        }
        tempRow = row;
        while (tempRow > 1) {
            tempRow--;
            ChessPosition tempPosition = new ChessPosition(tempRow, col);
            if (board.getPiece(tempPosition) == null) {
                validMoves.add(tempPosition);
            } else if (board.getPiece(tempPosition).getTeamColor() != piece.getTeamColor()) {
                validMoves.add(tempPosition);
                break;
            } else {
                break;
            }
        }
        while (tempCol < 8) {
            tempCol++;
            ChessPosition tempPosition = new ChessPosition(row, tempCol);
            if (board.getPiece(tempPosition) == null) {
                validMoves.add(tempPosition);
            } else if (board.getPiece(tempPosition).getTeamColor() != piece.getTeamColor()) {
                validMoves.add(tempPosition);
                break;
            } else {
                break;
            }
        }
        tempCol = col;
        while (tempCol > 1) {
            tempCol--;
            ChessPosition tempPosition = new ChessPosition(row, tempCol);
            if (board.getPiece(tempPosition) == null) {
                validMoves.add(tempPosition);
            } else if (board.getPiece(tempPosition).getTeamColor() != piece.getTeamColor()) {
                validMoves.add(tempPosition);
                break;
            } else {
                break;
            }
        }
        Collection<ChessMove> finalList = new ArrayList<>();
        for (ChessPosition validMove : validMoves) {
            ChessMove move = new ChessMove(position, validMove, null);
            finalList.add(move);
        }
        return finalList;
    }
}

