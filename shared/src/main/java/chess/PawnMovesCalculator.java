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
//            List<ChessPosition> legalWhiteMoves = whiteMovesCalculator(board, row, col);
//            validMoves.addAll(legalWhiteMoves);
            int forwardOne = row + 1;
            if (forwardOne < 9) {
                ChessPosition tempForwardOnePos = new ChessPosition(forwardOne, col);
                if (board.getPiece(tempForwardOnePos) == null) {
                    validMoves.add(tempForwardOnePos);
                    ChessPosition tempForwardTwoPos = new ChessPosition(row + 2, col);
                    if (row == 2 && board.getPiece(tempForwardTwoPos) == null) {
                        validMoves.add(tempForwardTwoPos);
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
            boolean whitePromotion = (piece.getTeamColor() == ChessGame.TeamColor.WHITE && validMove.getRow() == 8);
            boolean blackPromotion = (piece.getTeamColor() == ChessGame.TeamColor.BLACK && validMove.getRow() == 1);
            if (whitePromotion || blackPromotion){
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

//    public List<ChessPosition> whiteMovesCalculator(ChessBoard board, int row, int col) {
//        //forward one
//        //forward two
//        //left diagonal kill
//        //right diagonal kill
//        List<ChessPosition> whiteMoves = new ArrayList<>();
//
//        List<int[]> possibleForwardMoves = new ArrayList<>(List.of(new int[] {1, 0}, new int[] {2, 0}));
//        List<int[]> possibleKillMoves = new ArrayList<>(List.of(new int[] {1, 1}, new int[]{1, -1}));
//
//        ChessPosition initialPosition = new ChessPosition(row, col);
//        ChessPiece initialPiece = board.getPiece(initialPosition);
//
//        for (int [] move : possibleForwardMoves) {
//            row = move[0];
//            col = move[1];
//            if (checkMove(board, row, col)) {
//                ChessPosition forwardOnePos = new ChessPosition(row, col);
//                whiteMoves.add(forwardOnePos);
//            }
//        }
//
//        for (int [] move : possibleKillMoves) {
//            row = move[0];
//            col = move[1];
//            if (checkKillMove(board, row, col, initialPiece)) {
//                ChessPosition forwardOnePos = new ChessPosition(row, col);
//                whiteMoves.add(forwardOnePos);
//            }
//        }
//
//        return whiteMoves;
//    }
//
//    public boolean checkMove(ChessBoard board, int row, int col) {
//        boolean canMove = false;
//        if (row < 9 && row > 0) {
//            ChessPosition tempPosition = new ChessPosition(row, col);
//            if (board.getPiece(tempPosition) == null) {
//                canMove = true;
//            }
//        }
//        return canMove;
//    }
//
//    public boolean checkKillMove(ChessBoard board, int row, int col, ChessPiece initialPiece) {
//        boolean canMove = false;
//        if (row < 9 && col < 9 && row > 0 && col > 0) {
//            ChessPosition tempPosition = new ChessPosition(row, col);
//            boolean notSameTeam = board.getPiece(tempPosition).getTeamColor() != initialPiece.getTeamColor();
//            if (board.getPiece(tempPosition) != null && notSameTeam) {
//                canMove = true;
//            }
//        }
//        return canMove;
//    }

//    int diagRightCol = col + 1;
//            if (forwardOne < 9 && diagRightCol < 9) {
//                ChessPosition tempDiagRightPos = new ChessPosition(forwardOne, diagRightCol);
//                if (board.getPiece(tempDiagRightPos) != null && board.getPiece(tempDiagRightPos).getTeamColor() != piece.getTeamColor()) {
//                    validMoves.add(tempDiagRightPos);
//                }
//            }

}