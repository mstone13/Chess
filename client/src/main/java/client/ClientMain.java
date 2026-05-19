package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        //run gameMenuClient
        //then run each client accordingly
        GameMenuClient gameMenuClient = new GameMenuClient();
        gameMenuClient.run();

//        ChessClient client = new ChessClient();
//        client.run();
    }
}
