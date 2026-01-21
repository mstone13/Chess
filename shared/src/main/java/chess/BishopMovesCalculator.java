package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMovesCalculator implements ChessPiece.PieceMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        ChessPiece piece = board.getPiece(position);
        List<ChessPosition> validMoves = new ArrayList<>();

        int row = position.getRow();
        int tempRow = row;
        int col = position.getColumn();
        int tempCol = col;

        while (tempRow < 8 && tempCol < 8) {
            tempRow++;
            tempCol++;
            ChessPosition tempPosition = new ChessPosition(tempRow, tempCol);
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
        tempCol = col;
        while (tempRow > 1 && tempCol < 8) {
            tempRow--;
            tempCol++;
            ChessPosition tempPosition = new ChessPosition(tempRow, tempCol);
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
        tempCol = col;
        while (tempRow < 8 && tempCol > 1) {
            tempRow++;
            tempCol--;
            ChessPosition tempPosition = new ChessPosition(tempRow, tempCol);
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
        tempCol = col;
        while (tempRow > 1 && tempCol > 1) {
            tempRow--;
            tempCol--;
            ChessPosition tempPosition = new ChessPosition(tempRow, tempCol);
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
