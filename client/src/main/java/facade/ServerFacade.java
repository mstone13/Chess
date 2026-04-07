package facade;
import client.websocket.ServerMessageObserver;
import client.websocket.WebSocketFacade;
import com.google.gson.Gson;
import communicator.ClientCommunicator;
import model.*;
import websocket.messages.ServerMessage;

import java.io.IOException;


public class ServerFacade {
    private final ClientCommunicator  communicator;
    private final Gson serializer = new Gson();
    private final WebSocketFacade ws;
    public ServerMessageObserver observer = new ServerMessageObserver() {
        @Override
        public void notify(ServerMessage message) {
            System.out.println("Received: " + message);
        }
    };

    public ServerFacade(ClientCommunicator communicator) throws Exception {
        String serverUrl = "http://localhost:8080";
        this.communicator = communicator;

        this.ws = new WebSocketFacade(serverUrl, observer);
    }

    public UserResult register(String username, String password, String email) {
        RegisterRequest request = new RegisterRequest(username, password, email);
        String jsonRequest = serializer.toJson(request);

        String jsonResponse = communicator.sendPostRequest("/user", jsonRequest, null);
        if (jsonResponse.contains("message")){
            String message;
            if (jsonResponse.contains("already taken")) {
                message = "already taken. Try a different username!";
            } else {
                message = "please enter a valid username, password, and email!";
            }
            throw new RuntimeException(message);
        }
        return serializer.fromJson(jsonResponse, UserResult.class);
    }

    public UserResult login(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);
        String jsonRequest = serializer.toJson(request);

        String jsonResponse = communicator.sendPostRequest("/session", jsonRequest, null);
        if (jsonResponse.contains("message")) {
            String message;
            if (jsonResponse.contains("unauthorized")) {
                message = "incorrect username or password. Try again.";
            } else {
                message = "please enter a valid username and password.";
            }
            throw new RuntimeException(message);
        }

        return serializer.fromJson(jsonResponse, UserResult.class);
    }

    public void logout(String authToken) {
        String jsonResponse = communicator.sendDeleteRequest("/session", authToken);

        if (jsonResponse.contains("message")) {
            String message;
            if (jsonResponse.contains("unauthorized")) {
                message = "please log in in order to log out.";
            } else {
                message = jsonResponse;
            }
            throw new RuntimeException(message);
        }
    }

    public ListGamesResult listGames(String authToken) {
        String jsonResponse = communicator.sendGetRequest("/game", authToken);

        if (jsonResponse.contains("message")) {
            String message;
            if (jsonResponse.contains("unauthorized")) {
                message = "you must log in to see the list of games!";
            } else {
                message = jsonResponse;
            }
            throw new RuntimeException(message);
        }

        return serializer.fromJson(jsonResponse, ListGamesResult.class);
    }

    public void createGame(String authToken, String gameName) {
        CreateGameRequest request = new CreateGameRequest(gameName);
        String jsonRequest = serializer.toJson(request);

        String jsonResponse = communicator.sendPostRequest("/game", jsonRequest, authToken);
        if (jsonResponse.contains("message")) {
            String message;
            if (jsonResponse.contains("unauthorized")) {
                message = "log in before creating a game!";
            } else {
                message = "please enter a valid game name.";
            }
            throw new RuntimeException(message);
        }
    }

    public void joinGame(String authToken, int gameNum, String playerColor) {
        JoinGameRequest request = new JoinGameRequest(playerColor, gameNum);
        String jsonRequest = serializer.toJson(request);

        String jsonResponse = communicator.sendPutRequest("/game", jsonRequest, authToken);
        if (jsonResponse.contains("message")) {
            String message;
            if (jsonResponse.contains("already taken")) {
                message = "player slot already taken.";
            } else {
                message = "please enter a valid game number and player color.";
            }
            throw new RuntimeException(message);
        }

        try {
            ws.connect(authToken, gameNum);
        } catch (IOException e) {
            throw new RuntimeException("Error: failed to send connect command", e);
        }
    }


    public void clearApplication() {
        communicator.sendDeleteRequest("/db", null);
    }


}
