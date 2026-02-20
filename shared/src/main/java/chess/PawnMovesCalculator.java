package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator implements ChessPiece.PieceMovesCalculator {
    public List<ChessPosition> validMoves = new ArrayList<>();

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
//        List<ChessPosition> validMoves = new ArrayList<>();

        int row = position.getRow();
        int col = position.getColumn();

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            moveForwardOne(board, piece,row + 1, col);
            diagonalKillCheck(board, piece, row + 1, col + 1);
            diagonalKillCheck(board, piece, row + 1, col - 1);
        }

        else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            moveForwardOne(board, piece, row - 1, col);
            diagonalKillCheck(board, piece, row - 1, col + 1);
            diagonalKillCheck(board, piece, row - 1, col - 1);
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

    void moveForwardOne(ChessBoard board, ChessPiece piece, int row, int col) {
        if (isOnBoard(row, col)) {
            ChessPosition tempPosForwardOne = new ChessPosition(row, col);
            if (board.getPiece(tempPosForwardOne) == null) {
                validMoves.add(tempPosForwardOne);
                moveForwardTwo(board, piece, row, col);
            }
        }
    }

    void moveForwardTwo(ChessBoard board, ChessPiece piece, int row, int col) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE && row - 1 == 2) {
            ChessPosition tempPosForwardTwo = new ChessPosition(row + 1, col);
            if (board.getPiece(tempPosForwardTwo) == null) {
                validMoves.add(tempPosForwardTwo);
            }
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK && row + 1 == 7) {
            ChessPosition tempPosForwardTwo = new ChessPosition(row - 1, col);
            if (board.getPiece(tempPosForwardTwo) == null) {
                validMoves.add(tempPosForwardTwo);
            }
        }
    }

    void diagonalKillCheck(ChessBoard board, ChessPiece piece, int row, int col) {
        if (isOnBoard(row, col)) {
            ChessPosition diagonalMove = new ChessPosition(row, col);
            if (board.getPiece(diagonalMove) != null &&
                board.getPiece(diagonalMove).getTeamColor() != piece.getTeamColor()){
                validMoves.add(diagonalMove);
            }
        }
    }

    boolean isOnBoard(int row, int col) {
        return (row < 9 && row > 0 && col < 9 && col > 0);
    }
}