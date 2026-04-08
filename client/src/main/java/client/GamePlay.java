package client;

import chess.*;
import model.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.RESET_TEXT_BOLD_FAINT;
import static ui.EscapeSequences.SET_TEXT_BOLD;

public class GamePlay {
    private final Scanner scanner = new Scanner(System.in);

    public GamePlay() {
        //maybe initialize the communicator/facade here??
    }

    public String run(GameData game, String playerColor, boolean playing) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        var result = "";

        while (!result.equals("2") && !result.equals("5")){
            printMenu(out, playing);
            result = scanner.nextLine();
            directAction(out, result, game, playerColor, playing);
        }

        if (result.equals("5")) {
            return "RESIGN";
        } else {
            return "LEAVE";
        }
    }

    public void directAction(PrintStream out, String result, GameData game, String playerColor, boolean playing) {
        switch (result) {
            case "1" -> printHelp(out, playing);
            case "3" -> ui.ChessBoard.run(game, playerColor.toUpperCase(), null, null);
            case "4" -> makeMove();
//            case "5" -> resign(out, game, playerColor);
            case "6" -> highlightLegalMoves(out, game, playerColor);
        }
    }

    public void highlightLegalMoves(PrintStream out, GameData game, String playerColor) {
        out.println("Enter the row of a piece [1-8]: ");
        int row = Integer.parseInt(scanner.nextLine());

        out.println("Enter the column of a piece [a-h]: ");
        char input = scanner.nextLine().toLowerCase().charAt(0);
        int col = input - 'a' + 1;


        ChessPosition pos = new ChessPosition(row, col);
        Collection<ChessMove> legalMoves = new ChessGame().validMoves(pos);

        ui.ChessBoard.run(game, playerColor, legalMoves, pos);
    }

    public void makeMove() {}

//    public void resign(PrintStream out, GameData game, String playerColor) {
//        out.print(SET_TEXT_BOLD);
//        out.println("Are you sure you want to forfeit the game? [Yes/No] ");
//        String answer = scanner.nextLine();
//
//        if (answer.equalsIgnoreCase("yes")) {
//            String opponentName;
//            if (playerColor.equalsIgnoreCase("white")) {
//                opponentName = game.blackUsername();
//            } else {
//                opponentName = game.whiteUsername();
//            }
//            out.println("You have forfeit the game '" + game.gameName() + "' to " + opponentName + ".");
//
//        } else if (answer.equalsIgnoreCase("no")) {
//            out.println("Oh. Okay, keep playing then. You got this!");
//        }
//        out.print(RESET_TEXT_BOLD_FAINT);
//    }

    public void printHelp(PrintStream out, boolean playing) {
        out.println(SET_TEXT_BOLD);

        if (playing) {
            out.print("""
                >> Redraw chess board: Redraws the board of the chess game being played/observed.
                >> Leave: Exit the game and return to the menu.
                >> Make move: Input a move to make provided it is your turn.
                >> Resign: Admit defeat and forfeit the game.
                >> Highlight legal moves: Legal moves for a selected piece are highlighted on the board.
                """);
        } else {
            out.print("""
                    >> Leave: Pretty much you can only go back to the menu.
                    >> Join a game if you want more to do.
                    """);
        }

        out.print(RESET_TEXT_BOLD_FAINT);
    }

    public void printMenu(PrintStream out, boolean playing) {
        out.println();
        if (playing) {
            out.print("""
                    >> 1. Help
                    >> 2. Leave
                    >> 3. Redraw chess board
                    >> 4. Make move
                    >> 5. Resign
                    >> 6. Highlight legal moves
                    """);
        } else {
            out.print("""
                    >> 1. Help
                    >> 2. Leave
                    """);
        }
    }
}
