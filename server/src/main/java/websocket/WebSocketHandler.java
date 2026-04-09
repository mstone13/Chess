package websocket;

import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final Gson serializer = new Gson();
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private Gson gson;
    private GameService gameService;

    public WebSocketHandler(GameService gameService, AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.gameService = gameService;
        gson = new Gson();
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
            connections.saveSession(gameID, ctx);

            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getAuthToken(), command.getGameID(), ctx);
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                    try {
                        makeMove(moveCommand.getAuthToken(), moveCommand.getGameID(), moveCommand.getMove(), ctx);
                    } catch (InvalidMoveException e) {
                        sendError(ctx, e.getMessage());
                    }
                }
                case LEAVE -> leave(command.getAuthToken(), command.getGameID(), ctx);
                case RESIGN -> resign(command.getAuthToken(), command.getGameID(), ctx);
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
        String playerColor;
        GameData gameData = gameDAO.getGame(gameID);

        if (username.equals(gameData.blackUsername())) {
            playerColor = "black player";
        } else {
            playerColor = "white player";
        }

        connections.add(gameID, ctx);
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        message.setMessage(username + " has connected to the game as the " + playerColor);
        connections.broadcast(gameID, ctx, message);
    }

    private void leave(String authToken, int gameID, WsContext ctx) throws DataAccessException, IOException {
        String username = authDAO.getAuth(authToken).username();

        connections.remove(gameID, ctx);
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        message.setMessage(username + " has left the game.");
        connections.broadcast(gameID, ctx, message);
    }

    private void resign(String authToken, int gameID, WsContext ctx) throws DataAccessException, IOException {
        String username = authDAO.getAuth(authToken).username();

        connections.remove(gameID, ctx);
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        message.setMessage(username + " has forfeit the game.");
        connections.broadcast(gameID, ctx, message);
    }

    private void makeMove(String authToken, int gameID, ChessMove move, WsContext ctx) throws DataAccessException,
            InvalidMoveException, IOException {
        String username = authDAO.getAuth(authToken).username();

        gameService.makeMove(authToken, gameID, move);

        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        message.setMessage(username + " moved from " + move.getStartPosition() + " to " + move.getEndPosition() +
                (move.getPromotionPiece() != null ? ", promoting to " + move.getPromotionPiece() : ""));
        connections.broadcast(gameID, ctx, message);

        GameData updatedGame = gameDAO.getGame(gameID);

        ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        loadGame.setGame(updatedGame);
        connections.broadcast(gameID, null, loadGame);
    }

    private void sendError(WsContext ctx, String message) throws IOException {
        ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        error.setMessage(message);
        ctx.send(gson.toJson(error));
    }
}
