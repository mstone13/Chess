package chess;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BishopMovesCalculator implements ChessPiece.PieceMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        List<ChessPosition> validMoves = new ArrayList<>();

        List<int[]> possibleMoves = new ArrayList<>();
        possibleMoves.add(new int[]{1, 1});
        possibleMoves.add(new int[]{-1, 1});
        possibleMoves.add(new int[]{1, -1});
        possibleMoves.add(new int[]{-1, -1});

        int row = position.getRow();
        int col = position.getColumn();

        for (int[] move : possibleMoves) {
            int tempRow = row;
            int tempCol = col;
            while (tempRow > 0 && tempRow < 9 && tempCol > 0 && tempCol < 9) {
                tempRow += move[0];
                tempCol += move[1];
                if (tempRow <= 0 || tempRow >= 9 || tempCol <= 0 || tempCol >= 9) {
                    break;
                } else {
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
            }
        }

        Collection<ChessMove> finalList = new ArrayList<>();
        for (ChessPosition validMove : validMoves) {
            ChessMove finalMove = new ChessMove(position, validMove, null);
            finalList.add(finalMove);
        }
        return finalList;
    }
}
