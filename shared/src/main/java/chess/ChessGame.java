package chess;

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
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
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
        ChessPiece piece = board.getPiece(startPos);
        if (piece != null && piece.getTeamColor() == teamTurn) {

                Collection<ChessMove> legalMoves = validMoves(startPos);
                if (legalMoves.contains(move)) {
                    ChessPosition endPos = move.getEndPosition();

                    board.addPiece(endPos, piece);
                    board.addPiece(startPos, null);

                    boolean whitePromotion = (endPos.getRow() == 8 && piece.getTeamColor() == TeamColor.WHITE);
                    boolean blackPromotion = (endPos.getRow() == 1 && piece.getTeamColor() == TeamColor.BLACK);
                    boolean pieceTypePawn = (piece.getPieceType() == ChessPiece.PieceType.PAWN);

                    if (pieceTypePawn && whitePromotion || pieceTypePawn && blackPromotion) {
                            ChessPiece.PieceType promotionType = move.getPromotionPiece();
                            ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(), promotionType);
                            board.addPiece(endPos, promotedPiece);
                    }

                    teamTurn = changeTeamTurn(piece.getTeamColor());

                } else {
                    throw new InvalidMoveException("Illegal Move");
                }
        } else if (piece == null || piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Null piece or wrong team turn");
        }
    }

    public TeamColor changeTeamTurn (TeamColor teamTurn) {
        if (teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        } else {
            teamTurn = TeamColor.WHITE;
        }
        return teamTurn;
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

    private boolean isInCheckHelper(ChessBoard board, TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(board, teamColor);
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if(piece != null && piece.getTeamColor() != teamColor) {
                    //attacking moves against teamColor
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
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
            return isInCheckmateHelper(board, teamColor);
        }
        return false;
    }

    private boolean isInCheckmateHelper(ChessBoard board, TeamColor teamColor) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);
                    for (ChessMove move : moves) {
                        ChessBoard copyBoard = new ChessBoard(board);
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
        if (!isInCheck(teamColor)) {
            List<ChessMove> outOfCheckMoves = new ArrayList<>();

            for (int row = 1; row < 9; row++) {
                for (int col = 1; col < 9; col++) {

                    ChessPosition position = new ChessPosition(row, col);
                    ChessPiece piece = board.getPiece(position);
                    if (piece != null && piece.getTeamColor() == teamColor) {
                        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, position);
                        for (ChessMove move : pieceMoves) {
                            ChessBoard copyBoard = new ChessBoard(board);
                            ChessPosition startPos = move.getStartPosition();
                            ChessPosition endPos = move.getEndPosition();

                            copyBoard.addPiece(endPos, piece);
                            copyBoard.addPiece(startPos, null);
                            if (!isInCheckHelper(copyBoard, teamColor)) {
                                outOfCheckMoves.add(move);
                            }
                        }
                    }
                }
            }
            return outOfCheckMoves.isEmpty();
        }
        return false;
    }


//    public Collection<ChessMove> getPieceMoves (int row, int col) {
//
//    }
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


//OTHER OPTIMIZATION AND STYLE CHANGES:
//--COMBINE 'ADD PIECE' ALL INTO ONE FUNCTION
//--SIMPLIFY OTHER LARGE NESTS
//--POSSIBLY COMBINE CODE FROM ROOK/BISHOP/QUEEN CLASSES
//--SIMPLIFY PAWNMOVESCALCULATOR