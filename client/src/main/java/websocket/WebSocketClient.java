package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class WebSocketClient {

    private Session session;
    private final Gson gson = new Gson();

    public void connect(String url) {
        // connect to server websocket endpoint
    }

    public void sendCommand(UserGameCommand command) {
//        session.getRemote.sendString(gson.toJson(command));
    }

    public void onMessage(String message) {
        ServerMessage msg = gson.fromJson(message, ServerMessage.class);

        switch (msg.getServerMessageType()) {
            case LOAD_GAME -> handleLoadGame(msg);
            case NOTIFICATION -> handleNotification(msg);
            case ERROR -> handleError(msg);
        }
    }

    public void handleLoadGame(ServerMessage msg) {}

    public void handleNotification(ServerMessage msg) {}

    public void handleError(ServerMessage msg) {}
}
