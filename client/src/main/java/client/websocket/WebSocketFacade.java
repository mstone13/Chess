package client.websocket;

import com.google.gson.Gson;
import io.javalin.router.Endpoint;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import jakarta.websocket.*;
import websocket.messages.ServerMessage;

public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageObserver observer;

    public WebSocketFacade(String url, ServerMessageObserver observer) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.observer = observer;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    observer.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }
}
