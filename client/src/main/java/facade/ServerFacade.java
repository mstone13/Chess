package facade;
import com.google.gson.Gson;
import communicator.ClientCommunicator;
import model.*;


public class ServerFacade {
    private final ClientCommunicator  communicator;
    private final Gson serializer = new Gson();

    public ServerFacade(ClientCommunicator communicator){
        this.communicator = communicator;
    }

    public UserResult register(String username, String password, String email) {
        RegisterRequest request = new RegisterRequest(username, password, email);
        String jsonRequest = serializer.toJson(request);

        String jsonResponse = communicator.sendRequest("/user", jsonRequest, null);
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

        String jsonResponse = communicator.sendRequest("/session", jsonRequest, null);
        if (jsonResponse.contains("message")) {
            String message;
            if (jsonResponse.contains("unauthorized")) {
                message = "incorrect username or password. Try again";
            } else {
                message = "please enter a valid username and password.";
            }
            throw new RuntimeException(message);
        }

        return serializer.fromJson(jsonResponse, UserResult.class);
    }

    public void logout(String authToken) {
        String jsonRequest = serializer.toJson(authToken);
        communicator.sendRequest("/session", jsonRequest, authToken);
        assert authToken == null;
    }

    public void listGames() {}

    public CreateGameResult createGame(String authToken, String gameName) {
        CreateGameRequest request = new CreateGameRequest(gameName);
        String jsonRequest = serializer.toJson(request);

        String jsonResponse = communicator.sendRequest("/game", jsonRequest, authToken);
        if (jsonResponse.contains("message")) {
            String message;
            if (jsonResponse.contains("unauthorized")) {
                message = "log in before creating a game!";
            } else {
                message = "please enter a valid game name.";
            }
            throw new RuntimeException(message);
        }

        return serializer.fromJson(jsonResponse, CreateGameResult.class);
    }

    public void joinGame() {}

    public void clearApplication() {}


}
