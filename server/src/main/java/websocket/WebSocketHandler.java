package websocket;

import chess.ChessGame;
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
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final Gson gson;
    private final GameService gameService;

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
        try {
            UserGameCommand command = serializer.fromJson(ctx.message(), UserGameCommand.class);

            if (command == null) {
                sendError(ctx, "Error: bad request");
                return;
            }

            Integer gameID = command.getGameID();
            if (gameID == null) {
                sendError(ctx, "Error: bad request");
                return;
            }

            connections.saveSession(gameID, ctx);

            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getAuthToken(), gameID, ctx);
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = gson.fromJson(ctx.message(), MakeMoveCommand.class);

                    if (moveCommand == null || moveCommand.getMove() == null) {
                        sendError(ctx, "Error: bad request");
                        return;
                    }

                    try {
                        makeMove(moveCommand.getAuthToken(), moveCommand.getGameID(), moveCommand.getMove(), ctx);
                    } catch (InvalidMoveException e) {
                        sendError(ctx, e.getMessage());
                    }
                }
                case LEAVE -> leave(command.getAuthToken(), gameID, ctx);
                case RESIGN -> resign(command.getAuthToken(), gameID, ctx);


                default -> sendError(ctx, "Error: bad request");
            }

        } catch (Exception e) {
            try {
                sendError(ctx, "Error: " + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");

        connections.connections.values().forEach(set -> set.remove(ctx));
    }

    public void connect(String authToken, int gameID, WsContext ctx) throws IOException, DataAccessException {
        var auth = authDAO.getAuth(authToken);

        if (auth == null) {
            sendError(ctx, "Error: unauthorized");
            return;
        }

        GameData gameData = gameDAO.getGame(gameID);

        if (gameData == null) {
            sendError(ctx, "Error: bad request");
            return;
        }

        String username = auth.username();
        String playerColor;


        if (username.equals(gameData.blackUsername())) {
            playerColor = "black player";
        } else if (username.equals(gameData.whiteUsername())){
            playerColor = "white player";
        } else {
            playerColor = "observer";
        }

        connections.add(gameID, ctx);
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        message.setMessage(username + " has connected to the game as the " + playerColor);
        connections.broadcast(gameID, ctx, message);

        ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        loadGame.setGame(gameData);
        ctx.send(gson.toJson(loadGame));
    }

    private void leave(String authToken, int gameID, WsContext ctx) throws DataAccessException, IOException {
        String username = authDAO.getAuth(authToken).username();
        GameData game = gameDAO.getGame(gameID);
        boolean isPlayer = username.equals(game.whiteUsername()) || username.equals(game.blackUsername());

        connections.remove(gameID, ctx);

        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        message.setMessage(username + " has left the game.");

        if (isPlayer) {
            gameService.leaveGame(authToken, gameID);
        }
        connections.broadcast(gameID, ctx, message);

    }

    private void resign(String authToken, int gameID, WsContext ctx) throws DataAccessException, IOException {
        ChessGame game = gameDAO.getGame(gameID).game();
        if (game.isFinished()) {
            sendError(ctx, "Error: You cannot resign from a game that is finished.");
        }

        gameService.resign(authToken, gameID);

        String username = authDAO.getAuth(authToken).username();

        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        message.setMessage(username + " has resigned from the game.");
        connections.broadcast(gameID, null, message);
    }

    private void makeMove(String authToken, int gameID, ChessMove move, WsContext ctx) throws DataAccessException,
            InvalidMoveException, IOException {
        String username = authDAO.getAuth(authToken).username();

        gameService.makeMove(authToken, gameID, move);

        GameData updatedGame = gameDAO.getGame(gameID);
        // send load_game to EVERYONE
        ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        loadGame.setGame(updatedGame);
        connections.broadcast(gameID, null, loadGame);

        //send move notification to other players ONLY
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        message.setMessage(username + " moved from " + move.getStartPosition() + " to " + move.getEndPosition() +
                (move.getPromotionPiece() != null ? ", promoting to " + move.getPromotionPiece() : ""));
        connections.broadcast(gameID, ctx, message);

        ChessGame game = updatedGame.game();
        ChessGame.TeamColor opponent = username.equals(updatedGame.whiteUsername())
                ? ChessGame.TeamColor.BLACK
                : ChessGame.TeamColor.WHITE;

        if (game.isInCheckmate(opponent)) {
            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            msg.setMessage("Checkmate!");
            connections.broadcast(gameID, null, msg);
        } else if (game.isInStalemate(opponent)) {
            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            msg.setMessage("Stalemate!");
            connections.broadcast(gameID, null, msg);
        } else if (game.isInCheck(opponent)) {
            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            msg.setMessage("Check.");
            connections.broadcast(gameID, null, msg);
        }

    }

    private void sendError(WsContext ctx, String message) throws IOException {
        ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        error.setErrorMessage(message);
        ctx.send(gson.toJson(error));
    }
}
