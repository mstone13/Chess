package facade;
import com.google.gson.Gson;
import communicator.ClientCommunicator;
import model.*;


public class ServerFacade {
    //send requests and receive responses here
    private ClientCommunicator communicator;
    private final Gson serializer = new Gson();

    public ServerFacade(ClientCommunicator communicator){
        this.communicator = communicator;
    }

    public UserResult register(String username, String password, String email) {
        RegisterRequest request = new RegisterRequest(username, password, email);
        String jsonRequest = serializer.toJson(request);

        String jsonResponse = communicator.sendRequest("/user", jsonRequest);
        return serializer.fromJson(jsonResponse, UserResult.class);
    }

    public UserResult login(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);
        String jsonRequest = serializer.toJson(request);

        String jsonResponse = communicator.sendRequest("/session", jsonRequest);

        return serializer.fromJson(jsonResponse, UserResult.class);
    }

    public void logout() {}

    public void listGames() {}

    public void createGame() {}

    public void joinGame() {}

    public void clearApplication() {}


}
