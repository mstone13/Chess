package client;

import ui.ChessBoard;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;


public class ChessClient {
   private static final String EMPTY = "   ";

    public static void run() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.println("Welcome to the 240 Chess Menu!");
        String loggedOutMenu = """
                >> 1. Help
                >> 2. Login
                >> 3. Register
                >> 4. Quit
                """;

        out.print(loggedOutMenu);

        ChessBoard.run();
    }




}
