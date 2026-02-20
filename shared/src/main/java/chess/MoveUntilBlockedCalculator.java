package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MoveUntilBlockedCalculator implements ChessPiece.PieceMovesCalculator {
    public List<ChessPosition> validMoves = new ArrayList<>();

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        List<int[]> possibleMoves = new ArrayList<>();

        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            //rook moves
            possibleMoves.add(new int[] {1,0});
            possibleMoves.add(new int[] {-1,0});
            possibleMoves.add(new int[] {0,1});
            possibleMoves.add(new int[] {0,-1});
        } else {
            //bishop moves
            possibleMoves.add(new int[]{1, 1});
            possibleMoves.add(new int[]{-1, 1});
            possibleMoves.add(new int[]{1, -1});
            possibleMoves.add(new int[]{-1, -1});
        }

        runMovesUntilBlocked(board, position, piece, possibleMoves);

        Collection<ChessMove> finalList = new ArrayList<>();
        for (ChessPosition validMove : validMoves) {
            ChessMove finalMove = new ChessMove(position, validMove,null);
            finalList.add(finalMove);
        }
        return finalList;
    }

    void runMovesUntilBlocked(ChessBoard board, ChessPosition position, ChessPiece piece, List<int[]> possibleMoves) {
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
    }
}