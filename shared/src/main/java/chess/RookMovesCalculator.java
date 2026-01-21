package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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