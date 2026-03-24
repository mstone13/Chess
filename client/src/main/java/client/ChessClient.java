package client;

import communicator.ClientCommunicator;
import facade.ServerFacade;
import model.*;
import ui.ChessBoard;

import java.awt.*;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChessClient {
    private static boolean signedIn = false;
    private String authToken = null;
    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade facade;

    public ChessClient() {
        ClientCommunicator communicator = new ClientCommunicator("http://localhost:8080");
        this.facade = new ServerFacade(communicator);
    }

    public void run() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.println("Welcome to the 240 Chess Menu!");
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

        if (signedIn) {
            switch (result) {
                case "1" -> printHelp(out);
                case "3" -> logout(out);
                case "4" -> createGame(out);
                case "5" -> listGames(out);
                case "6" -> joinGame(out);
                case "7" -> observeGame(out);
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
                case "4" -> register(out);
            }
        }
    }

    public UserResult login(PrintStream out) {
        try {
            out.println("Input username: ");
            String username = scanner.nextLine();

            out.println("Input password: ");
            String password = scanner.nextLine();

            UserResult result = facade.login(username, password);
            out.println("Welcome " + result.username + "!!");
            return result;
        } catch (Exception e) {
            out.println("Login failed: " + e.getMessage());
        }
        return null;
    }

    public void register(PrintStream out) {
        try {
            out.println("Input username: ");
            String username = scanner.nextLine();

            out.println("Input password: ");
            String password = scanner.nextLine();

            out.println("Input email: ");
            String email = scanner.nextLine();

            UserResult result = facade.register(username, password, email);
            out.println("Hello there, " + result.username);
        } catch (Exception e){
            out.println("Registration failed: " + e.getMessage());
        }
    }

    public void logout(PrintStream out) {
        facade.logout(authToken);
        this.authToken = null;
        signedIn = false;

        out.println("Logged out successfully.");
    }

    public void createGame(PrintStream out) {
        try {
            out.println("Input game name: ");
            String gameName = scanner.nextLine();
            facade.createGame(authToken, gameName);
            out.println("Game '" + gameName + "' created successfully.");
        } catch (Exception e) {
            out.println("Failed to create game: " + e.getMessage());
        }
    }

    public void listGames(PrintStream out) {
        try {
            ListGamesResult result = facade.listGames(authToken);
            int counter = 1;
            for (GameData game : result.games()) {
                out.println(counter + ": " + game.gameName());
                counter++;
            }
        } catch (Exception e) {
            out.println("Failed to list games: " + e.getMessage());
        }
    }

    public void joinGame(PrintStream out) {
        try {
            out.println("Input game number: ");
            int gameNum = Integer.parseInt(scanner.nextLine());

            String playerColor;
            while (true) {
                out.println("Input player color (black or white):");
                playerColor = scanner.nextLine();

                if (playerColor.equalsIgnoreCase("white") || playerColor.equalsIgnoreCase("black")) {
                    break;
                } else {
                    out.println("Please input a valid chess player color.");
                }
            }

            facade.joinGame(authToken, gameNum, playerColor.toUpperCase());
            ChessBoard.run(); //edit chessBoard to flip depending on the player color

        } catch (Exception e) {
            out.println("Failed to join game: " + e.getMessage());
        }

    }

    public void observeGame(PrintStream out) {
        try {
            out.println("Input game number: ");
            int gameNum = Integer.parseInt(scanner.nextLine());

            ListGamesResult result = facade.listGames(authToken);

            ChessBoard.run(); //again, edit chessboard to match the specific game

        } catch (Exception e) {
            out.println("Failed to observe game: " + e.getMessage());
        }
    }

    public void printHelp(PrintStream out){
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
