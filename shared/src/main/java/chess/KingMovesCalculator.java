package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KingMovesCalculator implements ChessPiece.PieceMovesCalculator {
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        List<ChessPosition> validMoves = new ArrayList<>();

        int row = position.getRow();
        int col = position.getColumn();

        List<int[]> possibleMoves = new ArrayList<>();
        possibleMoves.add(new int[] {-1, -1});
        possibleMoves.add(new int[] {-1, 0});
        possibleMoves.add(new int[] {-1, 1});
        possibleMoves.add(new int[] {0, -1});
        possibleMoves.add(new int[] {0, 1});
        possibleMoves.add(new int[] {1, -1});
        possibleMoves.add(new int[] {1, 1});
        possibleMoves.add(new int[] {1, 0});

        for (int[] move : possibleMoves) {
            int addRow = move[0];
            int addCol = move[1];
            ChessPosition tempPosition = new ChessPosition(row + addRow, col + addCol);
            if (row + addRow < 1 || row + addRow > 8 || col + addCol < 1 || col + addCol > 8){
                continue;
            }
            else if (board.getPiece(tempPosition) == null) {
                validMoves.add(tempPosition);
            } else if (board.getPiece(tempPosition).getTeamColor() != piece.getTeamColor()) {
                validMoves.add(tempPosition);
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
