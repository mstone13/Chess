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

    public void run(GameData game, String playerColor) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        var result = "";

        while (!result.equals("3")){
            printMenu(out);
            result = scanner.nextLine();
            directAction(out, result, game, playerColor);
        }

        out.println("Exiting game. Thanks for playing!");
    }

    public void directAction(PrintStream out, String result, GameData game, String playerColor) {
        switch (result) {
            case "1" -> printHelp(out);
            case "2" -> ui.ChessBoard.run(game, playerColor.toUpperCase(), null, null);
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

    public void printHelp(PrintStream out) {
        out.println(SET_TEXT_BOLD);

        out.print("""
                >> Redraw chess board: Redraws the board of the chess game being played/observed.
                >> Leave: Exit the game and return to the menu.
                >> Make move: Input a move to make provided it is your turn.
                >> Resign: Admit defeat and forfeit the game.
                >> Highlight legal moves: Legal moves for a selected piece are highlighted on the board.
                """);

        out.print(RESET_TEXT_BOLD_FAINT);
    }

    public void printMenu(PrintStream out) {
        out.println();
        out.print("""
                >> 1. Help
                >> 2. Redraw chess board
                >> 3. Leave
                >> 4. Make move
                >> 5. Resign
                >> 6. Highlight legal moves
                """);
    }
}
