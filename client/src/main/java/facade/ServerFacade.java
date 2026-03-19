package facade;
import communicator.ClientCommunicator;
import model.*;

public class ServerFacade {
    //send requests and receive responses here
    private ClientCommunicator communicator = new ClientCommunicator();

    public void register() {}

    public void login(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);

    }

    public void logout() {}

    public void listGames() {}

    public void createGame() {}

    public void joinGame() {}

    public void clearApplication() {}


}
