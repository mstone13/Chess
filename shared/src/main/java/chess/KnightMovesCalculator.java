package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMovesCalculator implements ChessPiece.PieceMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        List<ChessPosition> validMoves = new ArrayList<>();

        List<int[]> possibleMoves = new ArrayList<>();
        possibleMoves.add(new int[] {1,2});
        possibleMoves.add(new int[] {1,-2});
        possibleMoves.add(new int[] {2,1});
        possibleMoves.add(new int[] {2,-1});
        possibleMoves.add(new int[] {-1,2});
        possibleMoves.add(new int[] {-1,-2});
        possibleMoves.add(new int[] {-2,1});
        possibleMoves.add(new int[] {-2,-1});

        int row = position.getRow();;
        int col = position.getColumn();

        for (int[] move : possibleMoves) {
            int tempRow = row + move[0];
            int tempCol = col + move[1];
            if (tempRow > 0 && tempRow < 9 && tempCol > 0 && tempCol < 9) {
                ChessPosition tempPosition = new ChessPosition(tempRow, tempCol);
                if (board.getPiece(tempPosition) == null) {
                    validMoves.add(tempPosition);
                } else if (board.getPiece(tempPosition).getTeamColor() != piece.getTeamColor()){
                    validMoves.add(tempPosition);
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
