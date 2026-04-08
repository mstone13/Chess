package client.websocket;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;

import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageObserver observer;
    private boolean connected = false;

    public WebSocketFacade(String url, ServerMessageObserver observer) throws Exception {
        url = url.replace("http", "ws");
        URI socketURI = new URI(url + "/ws");
        this.observer = observer;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, socketURI);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        this.connected = true;

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
//                System.out.println("Raw WS message: " + message);
                ServerMessage serverMsg = new Gson().fromJson(message, ServerMessage.class);
                observer.notify(serverMsg);
            }
        });
    }

    public void connect(String authToken, Integer gameID) throws IOException {
//        sendCommand(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
        try {
            Gson gson = new Gson();

            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            String json = gson.toJson(command);

            this.session.getBasicRemote().sendText(json);
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID) throws IOException {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID));
    } //maybe change this? see specs. requires extra field?

    public void leave(String authToken, Integer gameID) throws IOException {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
    }

    public void resign(String authToken, Integer gameID) throws IOException {
        sendCommand(new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID));
    }

    public void sendCommand(UserGameCommand command) throws IOException {
        if (!connected || session == null) {
            throw new IllegalStateException("WebSocket is not connected yet!");
        }

        Gson gson = new Gson();
        String json = gson.toJson(command);
        session.getBasicRemote().sendText(json);
    }
}
