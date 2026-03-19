package client;

import facade.ServerFacade;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class ChessClient {
    private static boolean signedIn = false;
    private static Scanner scanner = new Scanner(System.in);
    private final ServerFacade facade = new ServerFacade();


    public void run() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.println("Welcome to the 240 Chess Menu!");
        printMenu(out);
        var result = "";

        while (!result.equals("2")) {
            result = scanner.nextLine();
            directAction(out, result);
        }

        out.println("Come back soon :)");
//        ChessBoard.run();
    }

    public void directAction(PrintStream out, String result) {
        if (signedIn) {
            switch (result) {
                case "1" -> printHelp();
                case "3" -> login(out);
//                case "4" -> createGame();
//                case "5" -> listGames();
//                case "6" -> playGame();
//                case "7" -> observeGame();
            }
        } else {
            switch (result) {
                case "1" -> printHelp();
//                case "3" -> logout();
//                case "4" -> register();
            }
        }
    }

    public void login(PrintStream out) {
        // function to let user login
        out.println("Input username: ");
        String username = scanner.nextLine();

        out.print("Input password: ");
        String password = scanner.nextLine();

        facade.login(username, password);
    }

    public void printHelp(){
        // print help statements
    }

    public void printMenu(PrintStream out) {
        if (signedIn) {
            out.print("""
            >> 1. Help
            >> 2. Quit
            >> 3. Logout
            >> 4. Create Game
            >> 5. List Games
            >> 6. Play Game
            >> 7. Observe Game
            """);
        } else {
            out.print( """
            >> 1. Help
            >> 2. Quit
            >> 3. Login
            >> 4. Register
            """);
        }
    }



}
