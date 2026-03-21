package client;

import communicator.ClientCommunicator;
import facade.ServerFacade;
import model.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class ChessClient {
    private static boolean signedIn = false;
    private static Scanner scanner = new Scanner(System.in);
    private final ServerFacade facade;

    public ChessClient() {
        ClientCommunicator communicator = new ClientCommunicator("http://localhost:8080"); //mm maybe wrong?
        this.facade = new ServerFacade(communicator);
    }


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
                case "1" -> printHelp(out, signedIn);
//                case "3" -> logout(out);
//                case "4" -> createGame();
//                case "5" -> listGames();
//                case "6" -> playGame();
//                case "7" -> observeGame();
            }
        } else {
            switch (result) {
                case "1" -> printHelp(out, signedIn);
                case "3" -> login(out);
                case "4" -> register(out);
            }
        }
    }

    public void login(PrintStream out) {
        // function to let user login
        out.println("Input username: ");
        String username = scanner.nextLine();

        out.println("Input password: ");
        String password = scanner.nextLine();

        UserResult result = facade.login(username, password);
        out.println("Welcome " + result.username + "!!");

    }

    public void register(PrintStream out) {
        out.println("Input username: ");
        String username = scanner.nextLine();

        out.println("Input password: ");
        String password = scanner.nextLine();

        out.println("Input email: ");
        String email = scanner.nextLine();

        UserResult result = facade.register(username, password, email);
        out.println("Hello there, " + result.username);
    }

    public void printHelp(PrintStream out, boolean signedIn){
        // print help statements
        if (signedIn) {
            out.println("""
                    Quit: Exit the program
                    Logout: Return to sign-in menu
                    Create Game: Start new game
                    List Games: See a list of all created games
                    Play Game: Join and play an existing game
                    Observe Game: Join and observe an existing game
                    """);
        } else {
            out.println("""
                    Quit: Exit the program
                    Login: Sign into your existing user
                    Register: Create a new user
                    """);
        }
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
