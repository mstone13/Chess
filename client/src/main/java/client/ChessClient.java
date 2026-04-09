package client;

import chess.ChessGame;
import client.websocket.ServerMessageObserver;
import client.websocket.WebSocketFacade;
import communicator.ClientCommunicator;
import facade.ServerFacade;
import model.*;
import ui.*;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient implements ServerMessageObserver {
    private static boolean signedIn = false;

    private String authToken = null;
    private String username = null;

    private final Scanner scanner = new Scanner(System.in);
    private final ServerFacade facade;
    private final WebSocketFacade webSocketFacade;
    private final GamePlay GamePlay;
    private GamePlay currentGamePlay = null;

    public ChessClient() throws Exception {
        String serverUrl = "http://localhost:8080";
        ClientCommunicator communicator = new ClientCommunicator(serverUrl);
        this.facade = new ServerFacade(communicator);
        this.GamePlay = new GamePlay();
        webSocketFacade = new WebSocketFacade(serverUrl, this);
    }

    public void run() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(SET_TEXT_BOLD);
        out.println("Welcome to the 240 Chess Menu!");
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
                        username = loginResult.username;
                    }
                }
                case "4" -> {
                    registerResult = register(out);
                    if (registerResult != null) {
                        authToken = registerResult.authToken;
                        signedIn = true;
                        username = registerResult.username;
                    }
                }

            }
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

    public void createGame(PrintStream out) {
        try {
            out.print(SET_TEXT_BOLD);
            out.println("Input game name: ");
            String gameName = scanner.nextLine();
            facade.createGame(authToken, gameName);
            out.println("Game '" + gameName + "' created successfully.");
            out.print(RESET_TEXT_BOLD_FAINT);
        } catch (Exception e) {
            out.println("Failed to create game: " + e.getMessage());
        }
    }

    public void listGames(PrintStream out) {
        try {
            out.println(SET_TEXT_BOLD);
            ListGamesResult result = facade.listGames(authToken);
            int counter = 1;
            if (result.games().isEmpty()) {
                out.println("Looks like no games have been created. Go ahead and create a game!");
            } else {
                out.println("CURRENT CHESS GAMES:");
                for (GameData game : result.games()) {
                    out.print(counter + ": " + game.gameName() + ". ");
                    out.print("(White player: " + game.whiteUsername() + ", ");
                    out.println("Black player: " + game.blackUsername() + ")");
                    counter++;
                }
            }
            out.print(RESET_TEXT_BOLD_FAINT);

        } catch (Exception e) {
            out.println("Failed to list games: " + e.getMessage());
        }
    }

    public void joinGame(PrintStream out) {
        try {
            out.print(SET_TEXT_BOLD);
            out.println("Input game number: ");
            int gameNum = Integer.parseInt(scanner.nextLine());

            String playerColor;
            while (true) {
                out.println("Input player color (black or white):");
                playerColor = scanner.nextLine();

                if (playerColor.equalsIgnoreCase("white")
                        || playerColor.equalsIgnoreCase("black")) {
                    break;
                } else {
                    out.println("Please input a valid chess player color.");
                }
            }

            facade.joinGame(authToken, gameNum, playerColor.toUpperCase());
            HashMap<Integer, GameData> orderedGames = orderedGameList(facade.listGames(authToken).games());

            out.print(RESET_TEXT_BOLD_FAINT);
            GameData game = orderedGames.get(gameNum);

            webSocketFacade.connect(authToken, gameNum);

            ChessBoard.run(game, playerColor.toUpperCase(), null, null);
            currentGamePlay = new GamePlay();
            String gamePlayAction = currentGamePlay.run(game, playerColor, true, webSocketFacade, authToken);

            gameplayCommand(gamePlayAction, gameNum, authToken);

        } catch (Exception e) {
            if (e.getMessage().contains("input")){
                String message = "please enter a valid game number and player color.";
                out.println("Failed to join game: " + message);
            } else {
                out.println("Failed to join game: " + e.getMessage());
            }
        }
    }

    public void observeGame(PrintStream out) {
        try {
            HashMap<Integer, GameData> orderedGames = orderedGameList(facade.listGames(authToken).games());

            out.print(SET_TEXT_BOLD);

            int gameNum;
            out.println("Input game number: ");

            try {
                gameNum = Integer.parseInt(scanner.nextLine());

            } catch (NumberFormatException e) {
                out.println("Failed to observe game: game number must be an integer");
                return;
            } catch (RuntimeException e) {
                return;
            }

            if (gameNum < 0 || gameNum > orderedGames.size()) {
                out.println("Please input a valid game number.");
            }

            GameData game = orderedGames.get(gameNum);
            out.print(RESET_TEXT_BOLD_FAINT);

            webSocketFacade.connect(authToken, gameNum);

            currentGamePlay = new GamePlay();

            ChessBoard.run(game, "white", null, null);
            currentGamePlay.run(game, null, false, webSocketFacade, authToken);


        } catch (Exception _) {}
    }

    public void gameplayCommand(String leaveCommand, int gameNum, String authToken) throws IOException {
        if (leaveCommand.equalsIgnoreCase("leave")) {
            facade.leaveGame(authToken, gameNum);
            webSocketFacade.leave(authToken, gameNum);
            System.out.println("You have left the game.");
        } else {
            facade.resign(authToken, gameNum);
            webSocketFacade.resign(authToken, gameNum);
            System.out.println("You have forfeit the game.");
        }
    }

    @Override
    public void notify(ServerMessage message) {
        PrintStream out = System.out;
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                GameData updatedGameData = message.getGame();
                currentGamePlay.updateGameData(updatedGameData);
                handleLoadGame(out, message);
            }
            case NOTIFICATION -> handleNotification(out, message);
            case ERROR -> handleError(out, message);
        }
    }

    public void handleLoadGame(PrintStream out, ServerMessage message) {
        GameData gameData = message.getGame();

        if (gameData == null) {
            out.println("No game data received.");
            return;
        }

        ChessGame game = gameData.game();
        ChessGame.TeamColor currentTurn = game.getTeamTurn();
        out.println(SET_TEXT_BOLD);
        out.println("Current board:" + RESET_TEXT_BOLD_FAINT);

        String playerColor;
        if (username.equalsIgnoreCase(gameData.whiteUsername())) {
            playerColor = "white";
        } else if (username.equalsIgnoreCase(gameData.blackUsername())) {
            playerColor = "black";
        } else {
            playerColor = "white";
        }

        ChessBoard.run(gameData, playerColor, null, null);
        out.println(SET_TEXT_COLOR_BLUE + "Turn: " + currentTurn);

        if (!game.canMove()) {
            out.println("The game is over!");
        }

        out.println(RESET_TEXT_COLOR);

    }

    public void handleNotification(PrintStream out, ServerMessage message) {
        out.println(SET_TEXT_COLOR_RED);
        out.println(">>> " + message.getMessage());
        out.print(RESET_TEXT_COLOR);
    }

    public void handleError(PrintStream out, ServerMessage message) {
        out.println(SET_TEXT_COLOR_RED);
        out.println(">> ERROR: " + message.getMessage());
        out.print(RESET_TEXT_COLOR);
    }

    public HashMap<Integer, GameData> orderedGameList(List<GameData> games) {
        HashMap<Integer, GameData> orderedGames = new HashMap<>();
        int counter = 1;
        for (GameData game : games){
            orderedGames.put(counter, game);
            counter++;
        }

        return orderedGames;
    }

    public void printHelp(PrintStream out){
        // print help statements
        out.println(SET_TEXT_BOLD);
        if (signedIn) {
            out.print("""
                    Quit: Exit the program.
                    Logout: Return to sign-in menu.
                    Create Game: Start new game. Simply input a game name!
                    List Games: See a list of all created games.
                    Play Game: Join and play an existing game. Input game number and choose a player color
                    (see list of games to find appropriate game number).
                    Observe Game: Observe an existing game. Input a game number.
                    """);
        } else {
            out.print("""
                    Quit: Exit the program.
                    Login: Sign into your existing user. Input your username and password.
                    Register: Create a new user. Input a username, password, and email.
                    """);
        }
        out.print(RESET_TEXT_BOLD_FAINT);
    }

    public void printMenu(PrintStream out) {
        out.println(RESET_BG_COLOR);
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
