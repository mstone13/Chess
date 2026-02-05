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
        Collection<ChessMove> legalMoves = new ArrayList<>();
        if (board.getPiece(startPosition) == null) {
            return legalMoves;
        } else {
            ChessPiece piece = board.getPiece(startPosition);
            Collection<ChessMove> pieceMoves = piece.pieceMoves(board, startPosition);
            for (ChessMove move : pieceMoves) {
                if (checkMoves(move, piece)) {
                    legalMoves.add(move);
                }
            }
        }
        return legalMoves;
    }

    public boolean checkMoves(ChessMove move, ChessPiece piece) {
        ChessBoard boardCopy = new ChessBoard(getBoard());
        boardCopy.addPiece(move.getEndPosition(), piece);
        return (!isInCheck(piece.getTeamColor()));
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPos = move.getStartPosition();
        Collection<ChessMove> legalMoves = validMoves(startPos);
        if (legalMoves.contains(move)) {
            ChessPosition endPos = move.getEndPosition();
            ChessPiece piece = board.getPiece(startPos);
            board.addPiece(endPos, piece);
        } else {
            throw new InvalidMoveException("Illegal Move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        boolean inCheck = false;

        ChessPosition kingPosition = getKingPosition(teamColor);
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++) {

                ChessPosition position = new ChessPosition(row, col);
                if (board.getPiece(position) != null && board.getPiece(position).getTeamColor() != teamColor) {
                    //there is an actual piece that is the attacking color (opposite from teamColor)
                    ChessBoard boardCopy = new ChessBoard(getBoard());
                    if (kingInDanger(boardCopy, position, kingPosition)) {
                        inCheck = true;
                    }
                    //will return TRUE if king is in danger, FALSE if king is safe
                }
            }
        }
        return inCheck;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
//        if (isInCheck(teamColor)) {
//            for (int row = 0; row < 8; row++) {
//                for (int col = 0; col < 8; col++) {
//
//
//                    ChessPosition position = new ChessPosition(row, col);
//                    if (board.getPiece(position) != null && board.getPiece(position).getTeamColor() == teamColor) {
//                        //there is an actual piece that is the DEFENDING color (same as teamColor)
//                        ChessBoard boardCopy = new ChessBoard(getBoard());
//                        return kingInDanger(boardCopy, position); // ONLY check, not Checkmate yet
//                        //will return TRUE if king is in danger, FALSE if king is safe
//                    }
//                }
//            }
//        }
        return false;
    }

    public ChessPosition getKingPosition(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                if (board.getPiece(position) != null && board.getPiece(position).getPieceType() == ChessPiece.PieceType.KING && board.getPiece(position).getTeamColor() == teamColor) {
                    kingPosition = position;
                }
            }
        }
        return kingPosition;
    }

    public boolean kingInDanger(ChessBoard board, ChessPosition currentPosition, ChessPosition kingPosition) {
        boolean inDanger = false;

        Collection<ChessMove> possibleMoves = board.getPiece(currentPosition).pieceMoves(board, currentPosition);
        for (ChessMove move : possibleMoves) {
            if (move.getEndPosition() != null) {
                if (move.getEndPosition() == kingPosition) {
                    inDanger = true;
                }
            }
        }
        return inDanger;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
//        throw new RuntimeException("Not implemented");
        return false;
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
