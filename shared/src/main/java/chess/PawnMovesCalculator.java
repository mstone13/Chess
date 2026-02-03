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

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            int forwardOne = row + 1;
            if (forwardOne < 9) {
                ChessPosition tempForwardOnePos = new ChessPosition(forwardOne, col);
                if (board.getPiece(tempForwardOnePos) == null) {
                    validMoves.add(tempForwardOnePos);
                    if (row == 2) {
                        ChessPosition tempForwardTwoPos = new ChessPosition(row + 2, col);
                        if (board.getPiece(tempForwardTwoPos) == null) {
                            validMoves.add(tempForwardTwoPos);
                        }
                    }
                }
            }

            int diagRightCol = col + 1;
            if (forwardOne < 9 && diagRightCol < 9) {
                ChessPosition tempDiagRightPos = new ChessPosition(forwardOne, diagRightCol);
                if (board.getPiece(tempDiagRightPos) != null && board.getPiece(tempDiagRightPos).getTeamColor() != piece.getTeamColor()) {
                    validMoves.add(tempDiagRightPos);
                }
            }

            int diagLeftCol = col - 1;
            if (forwardOne < 9 && diagLeftCol > 0) {
                ChessPosition tempDiagLeftPos = new ChessPosition(forwardOne, diagLeftCol);
                if (board.getPiece(tempDiagLeftPos) != null && board.getPiece(tempDiagLeftPos).getTeamColor() != piece.getTeamColor()) {
                    validMoves.add(tempDiagLeftPos);
                }
            }
        }

        else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            int forwardOne = row - 1;
            if (forwardOne > 0) {
                ChessPosition tempForwardOnePos = new ChessPosition(forwardOne, col);
                if (board.getPiece(tempForwardOnePos) == null) {
                    validMoves.add(tempForwardOnePos);
                    if (row == 7) {
                        ChessPosition tempForwardTwoPos = new ChessPosition(row - 2, col);
                        if (board.getPiece(tempForwardTwoPos) == null) {
                            validMoves.add(tempForwardTwoPos);
                        }
                    }
                }
            }

            int diagRightCol = col + 1;
            if (forwardOne > 0 && diagRightCol < 9) {
                ChessPosition tempDiagRightPos = new ChessPosition(forwardOne, diagRightCol);
                if (board.getPiece(tempDiagRightPos) != null && board.getPiece(tempDiagRightPos).getTeamColor() != piece.getTeamColor()) {
                    validMoves.add(tempDiagRightPos);
                }
            }

            int diagLeftCol = col - 1;
            if (forwardOne > 0 && diagLeftCol > 0) {
                ChessPosition tempDiagLeftPos = new ChessPosition(forwardOne, diagLeftCol);
                if (board.getPiece(tempDiagLeftPos) != null && board.getPiece(tempDiagLeftPos).getTeamColor() != piece.getTeamColor()) {
                    validMoves.add(tempDiagLeftPos);
                }
            }
        }

        List<ChessPiece.PieceType> pieceTypes = new ArrayList<>();
        pieceTypes.add(ChessPiece.PieceType.QUEEN);
        pieceTypes.add(ChessPiece.PieceType.ROOK);
        pieceTypes.add(ChessPiece.PieceType.BISHOP);
        pieceTypes.add(ChessPiece.PieceType.KNIGHT);

        Collection<ChessMove> finalList = new ArrayList<>();
        for (ChessPosition validMove : validMoves) {
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && validMove.getRow() == 8 || piece.getTeamColor() == ChessGame.TeamColor.BLACK && validMove.getRow() == 1){
                for (ChessPiece.PieceType type : pieceTypes) {
                    ChessMove move = new ChessMove(position, validMove, type);
                    finalList.add(move);
                }
            } else {
                ChessMove move = new ChessMove(position, validMove, null);
                finalList.add(move);
            }
        }
        return finalList;
    }
}