package client;

import facade.ServerFacade;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import communicator.ClientCommunicator;
import model.UserResult;

import static ui.EscapeSequences.*;

public class GameMenuClient {
    private static boolean signedIn = false;
    private final ServerFacade facade;
    private String authToken = null;
    private final Scanner scanner = new Scanner(System.in);


    public GameMenuClient() {
        String serverUrl = "http://localhost:8080";
        ClientCommunicator communicator = new ClientCommunicator(serverUrl);
        this.facade = new ServerFacade(communicator);
    }


    public void run() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(SET_TEXT_BOLD);
        out.println("Welcome to the GAME Menu!");
        out.print(RESET_TEXT_BOLD_FAINT);
        var result = "";

        while (!result.equals("2")) {
            printMenu(out);
            result = scanner.nextLine();
            directAction(out, result);
        }

        out.println("Exiting the menu. Come back soon :)");
    }

    public void directAction(PrintStream out, String result) {
        UserResult loginResult;
        UserResult registerResult;

        if (signedIn) {
            switch (result) {
                case "1" -> printHelp(out);
                case "3" -> logout(out);
                case "4" -> printGameOptions(out);
                case "5" -> chooseGame(out);
            }
        } else {
            switch (result) {
                case "1" -> printHelp(out);
                case "3" -> {
                    loginResult = login(out);
                    if (loginResult != null) {
                        authToken = loginResult.authToken;
                        signedIn = true;
                    }
                }
                case "4" -> {
                    registerResult = register(out);
                    if (registerResult != null) {
                        authToken = registerResult.authToken;
                        signedIn = true;
                    }
                }
            }
        }
    }

    public void printGameOptions(PrintStream out) {
        out.println(SET_TEXT_BOLD);
        out.println("""
                Game Options:
                ... CHESS
                ... GAME 2
                ... GAME 3
                ... GAME 4
                """);
        out.print(RESET_TEXT_BOLD_FAINT);
    }

    public void chooseGame(PrintStream out) {
        var chosenGame = "";
        printGameOptions(out);
        out.println("Choose a game to play:");
        out.print(SET_TEXT_COLOR_RED + "Enter 5 to cancel and return to menu");
        out.println(RESET_TEXT_COLOR);
        chosenGame = scanner.nextLine();

        switch (chosenGame) {
            case "1" -> runChess();
//            case "2" -> // second game!
//            case "3" -> // third game!
//            case "4" -> // woah fourth game
            case "5" -> out.println("Okay, back to the menu we go.");
            default -> out.println("Please enter an option 1-5");
        }
    }

    public void runChess() {
        ChessClient chessClient = new ChessClient();
        chessClient.run();
    }

    public UserResult register(PrintStream out) {
        try {
            out.print(SET_TEXT_BOLD);
            out.println("Input username: ");
            String username = scanner.nextLine();

            out.println("Input password: ");
            String password = scanner.nextLine();

            out.println("Input email: ");
            String email = scanner.nextLine();

            UserResult result = facade.register(username, password, email);
            out.print(SET_TEXT_BOLD);
            out.println("Hello there, " + result.username);
            out.print(RESET_TEXT_BOLD_FAINT);
            return result;
        } catch (Exception e){
            out.println("Registration failed: " + e.getMessage());
        }
        return null;
    }

    public void logout(PrintStream out) {
        try {
            facade.logout(authToken);
            this.authToken = null;
            signedIn = false;
            out.print(SET_TEXT_BOLD);
            out.println("Logged out successfully.");
            out.print(RESET_TEXT_BOLD_FAINT);
        } catch (Exception e) {
            throw new RuntimeException("Failed to log out: " + e.getMessage());
        }
    }

    public UserResult login(PrintStream out) {
        try {
            out.print(SET_TEXT_BOLD);
            out.println("Input username: ");
            String username = scanner.nextLine();

            out.println("Input password: ");
            String password = scanner.nextLine();

            UserResult result = facade.login(username, password);
            out.println("Welcome " + result.username + "!!");
            out.print(RESET_TEXT_BOLD_FAINT);
            return result;
        } catch (Exception e) {
            out.println("Login failed: " + e.getMessage());
        }
        return null;
    }

    public void printHelp(PrintStream out) {

    }

    public void printMenu(PrintStream out) {
        if (signedIn) {
            out.println("""
                    >> 1. Help
                    >> 2. Quit
                    >> 3. Logout
                    >> 4. See Game Options
                    >> 5. Choose Game to Play
                    """);
        } else {
            out.println("""
                    >> 1. Help
                    >> 2. Quit
                    >> 3. Login
                    >> 4. Register
                    """);
        }
    }
}
