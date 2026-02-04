package chess;

import org.junit.jupiter.api.Test;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board;

    public ChessGame() {
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(teamTurn);
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessBoard myBoard = getBoard();
        ChessPiece piece = myBoard.getPiece(startPosition);

        Collection<ChessMove> legalMoves = new ArrayList<>();
        if (piece == null) {
            return legalMoves;
        } else {
            Collection<ChessMove> possibleMoves = piece.pieceMoves(myBoard, startPosition);
            if (isInCheck(piece.getTeamColor())) {
                //better get out of check nerd
            } else {
                for (ChessMove move : possibleMoves) {
                    //deep copy board
                    ChessBoard boardCopy = new ChessBoard(myBoard);

                    //check each move in possibleMoves on the copy board, if legal, add to legalMoves
                }
            }
        }

        return null;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessBoard myBoard = getBoard();
        ChessBoard boardCopy = new ChessBoard(myBoard);

        TeamColor attackColor = TeamColor.WHITE;
        if (teamColor == TeamColor.WHITE) {
            attackColor = TeamColor.BLACK;
        }

        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = boardCopy.getPiece(position);
                if (piece.getTeamColor() == attackColor) {
                   Collection<ChessMove> possibleMoves = piece.pieceMoves(boardCopy, position);
                   for (ChessMove move : possibleMoves) {
                       return boardCopy.getPiece(move.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING;
                   }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
