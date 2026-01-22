package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator implements ChessPiece.PieceMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        List<ChessPosition> validMoves = new ArrayList<>();

        int row = position.getRow();
        int col = position.getColumn();

        //white pawn movement
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {

            ChessPosition tempPositionForward = new ChessPosition(row + 1, col);
            if (board.getPiece(tempPositionForward) == null) {
                validMoves.add(tempPositionForward);
            }

            if (row == 2) {
                ChessPosition tempPositionDouble = new ChessPosition(row + 2, col);
                if (board.getPiece(tempPositionDouble) == null && board.getPiece(tempPositionForward) == null) {
                    validMoves.add(tempPositionDouble);
                }
            }

            if (col < 8) {
                ChessPosition tempPositionRightDiag = new ChessPosition(row + 1, col + 1);
                if (board.getPiece(tempPositionRightDiag) != null) {
                    if (board.getPiece(tempPositionRightDiag).getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(tempPositionRightDiag);
                    }
                }
            }

            if (col > 0) {
                ChessPosition tempPositionLeftDiag = new ChessPosition(row + 1, col - 1);
                if (board.getPiece(tempPositionLeftDiag) != null) {
                    if (board.getPiece(tempPositionLeftDiag).getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(tempPositionLeftDiag);
                    }
                }
            }
        }

        //black pawn movement
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {

            ChessPosition tempPositionForward = new ChessPosition(row - 1, col);
            if (board.getPiece(tempPositionForward) == null) {
                validMoves.add(tempPositionForward);
            }

            if (row == 7) {
                ChessPosition tempPositionDouble = new ChessPosition(row - 2, col);
                if (board.getPiece(tempPositionDouble) == null && board.getPiece(tempPositionForward) == null) {
                    validMoves.add(tempPositionDouble);
                }
            }

            if (col < 8) {
                ChessPosition tempPositionRightDiag = new ChessPosition(row - 1, col + 1);
                if (board.getPiece(tempPositionRightDiag) != null) {
                    if (board.getPiece(tempPositionRightDiag).getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(tempPositionRightDiag);
                    }
                }
            }

            if (col > 0) {
                ChessPosition tempPositionLeftDiag = new ChessPosition(row - 1, col - 1);
                if (board.getPiece(tempPositionLeftDiag) != null) {
                    if (board.getPiece(tempPositionLeftDiag).getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(tempPositionLeftDiag);
                    }
                }
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