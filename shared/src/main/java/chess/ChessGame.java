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
        this.board = new ChessBoard();
        this.board.resetBoard();
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
        ChessBoard boardCopy = new ChessBoard(board);

        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();

        boardCopy.addPiece(endPos, piece);
        boardCopy.addPiece(startPos, null);

        return (!isInCheckHelper(boardCopy, piece.getTeamColor()));
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
            board.addPiece(startPos, null);
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
       return isInCheckHelper(board, teamColor);
    }

    private boolean isInCheckHelper(ChessBoard copyBoard, TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(copyBoard, teamColor);
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = copyBoard.getPiece(position);

                if(piece != null && piece.getTeamColor() != teamColor) {
                    //attacking moves against teamColor
                    Collection<ChessMove> moves = piece.pieceMoves(copyBoard, position);
                    for (ChessMove move : moves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
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
        if (isInCheck(teamColor)) { //there are opposite-color pieces that can attack our king
            //can our pieces protect our king?

        }
        return false;
    }

    private boolean isInCheckmateHelper(ChessBoard copyBoard, TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(copyBoard, teamColor);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = copyBoard.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(copyBoard, position);
                    for (ChessMove move : moves) {
                        ChessPosition startPos = move.getStartPosition();
                        ChessPosition endPos = move.getEndPosition();

                        copyBoard.addPiece(endPos, piece);
                        copyBoard.addPiece(startPos, null);
                        if (!isInCheckHelper(copyBoard, teamColor)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public ChessPosition getKingPosition(ChessBoard copyBoard, TeamColor teamColor) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = copyBoard.getPiece(position);

                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return position;
                }
            }
        }
        return null;
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
