package websocket;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson serializer = new Gson();
    private AuthDAO authDAO;

    public WebSocketHandler() {
        authDAO = new SQLAuthDAO();
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        int gameID = -1;

        try {
            UserGameCommand command = serializer.fromJson(ctx.message(), UserGameCommand.class);
            gameID = command.getGameID();
//            String username = getUsername(command.getAuthToken());
            connections.saveSession(gameID, ctx); // (in connection manager)

            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getAuthToken(), command.getGameID(), ctx);
//                case MAKE_MOVE -> makeMove(session, username, (MakeMoveCommand) command);
                case LEAVE -> leave(command.getAuthToken(), command.getGameID(), ctx);
//                case RESIGN -> resign (session, username, (ResignCommand) command);
            }
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");

        connections.connections.values().forEach(set -> set.remove(ctx));
    }

    public void connect(String authToken, int gameID, WsContext ctx) throws IOException, DataAccessException {
        String username = authDAO.getAuth(authToken).username();

        connections.add(gameID, ctx);
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        message.setMessage(username + " has connected to the game.");
        connections.broadcast(gameID, ctx, message);
    }

    private void leave(String authToken, int gameID, WsContext ctx) throws DataAccessException, IOException {
        String username = authDAO.getAuth(authToken).username();

        connections.remove(gameID, ctx);
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        message.setMessage(username + " has left the game.");
        connections.broadcast(gameID, ctx, message);
    }
}
