package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.router.Endpoint;
import model.*;
import io.javalin.json.JavalinGson;
import service.GameService;
import service.UserService;
import websocket.WebSocketHandler;

import java.util.Map;

public class Server {

    private Javalin javalin;
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserService userService;
    private GameService gameService;

    private final WebSocketHandler webSocketHandler;

    public Server() {
        try {
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to configure database", e);
        }


        initializeDAOs();
        initializeServices();

        webSocketHandler = new WebSocketHandler(gameService, authDAO, gameDAO);

        initializeJavalin();
        registerEndpoints();

    }

    private void initializeDAOs() {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();
    }

    private void initializeServices() {
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO, userDAO);
    }

    private void initializeJavalin() {
        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        });

        javalin.exception(DataAccessException.class, (e, ctx) -> {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        });

        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: Internal Server Error"));
        });
    }

    // Register your endpoints and exception handlers here.

    private void registerEndpoints() {
        javalin.delete("/db", ctx -> {
            try {
                userDAO.clearUsers();
                gameDAO.clearGames();
                authDAO.clearAuths();
                ctx.status(200);
                ctx.json(Map.of());
            } catch (DataAccessException e) {
                ctx.status(500).json(Map.of("message", "Error: internal server error"));
            }
        });

        javalin.post("/user", ctx -> {
            RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
            try {
                UserResult result = userService.register(request);
                ctx.status(200).json(result);
            } catch (DataAccessException e) {
                ctx.status(500).json(Map.of("message", "Error: internal server error"));
            } catch (AlreadyTakenException e) {
                ctx.status(403).json(Map.of("message", "Error: already taken"));
            } catch (RuntimeException e) {
                ctx.status(400).json(Map.of("message", "Error: bad request"));
            }
        });

        javalin.post("/session", ctx -> {
            LoginRequest request = ctx.bodyAsClass(LoginRequest.class);
            try {
                UserResult result = userService.login(request);
                ctx.status(200).json(result);
            } catch (DataAccessException e) {
                ctx.status(500).json(Map.of("message", "Error: internal server error"));
            }
            catch (RuntimeException e) {
                runtimeExceptionCatch(ctx, e);
            }
        });

        javalin.delete("/session", ctx -> {
            String authToken = ctx.header("Authorization");
            try {
                userService.logout(authToken);
                ctx.status(200).json(Map.of());
            } catch (Exception e) {
                handleException(ctx, e);}
        });

        javalin.get("/game", ctx -> {
            String authToken = ctx.header("Authorization");
            try {
                ListGamesResult result = gameService.listGames(authToken);
                ctx.status(200).json(result);
            } catch (Exception e) {
                handleException(ctx, e);}
        });

        javalin.post("/game", ctx -> {
            String authToken = ctx.header("Authorization");
            CreateGameRequest request = ctx.bodyAsClass(CreateGameRequest.class);
            try {
                CreateGameResult result = gameService.createGame(authToken, request);
                ctx.status(200).json(result);
            } catch (DataAccessException e) {
                ctx.status(500).json(Map.of("message", "Error: internal server error"));
            } catch (RuntimeException e) {
                runtimeExceptionCatch(ctx, e);}
        });

        javalin.put("/game", ctx -> {
            try {
                String authToken = ctx.header("Authorization");
                JoinGameRequest request = ctx.bodyAsClass(JoinGameRequest.class);
                if (request.gameID == null || request.playerColor == null || !validPlayerColor(request.playerColor)) {
                    ctx.status(400).json(Map.of("message", "Error: bad request"));
                    return;
                }
                AuthData auth = authDAO.getAuth(authToken);
                if (auth == null) {
                    ctx.status(401).json(Map.of("message", "Error: unauthorized"));
                }
                String username = authDAO.getAuth(authToken).username();
                GameData game = gameDAO.getGame(request.gameID);

                if ("RESIGN".equalsIgnoreCase(request.playerColor)) {
                    gameService.resign(authToken, request.gameID);
                } else if (request.playerColor == null) {
                    gameService.leaveGame(authToken, request.gameID);
                } else if ((request.playerColor.equalsIgnoreCase("WHITE") && username.equals(game.whiteUsername())) ||
                        (request.playerColor.equalsIgnoreCase("BLACK") && username.equals(game.blackUsername()))) {
                    gameService.leaveGame(authToken, request.gameID);
                } else {
                    gameService.joinGame(authToken, request);
                }
                ctx.status(200).json(Map.of());
            } catch (Exception e) {
                handleException(ctx, e);}
        });
    }

    private void handleException(Context ctx, Exception e) {
        if (e instanceof DataAccessException) {
            ctx.status(500).json(Map.of("message", "Error: internal server error"));
        } else if (e instanceof AlreadyTakenException) {
            ctx.status(403).json(Map.of("message", "Error: already taken"));
        } else if (e instanceof IllegalArgumentException) {
            ctx.status(400).json(Map.of("message", "Error: bad request"));
        } else {
            ctx.status(401).json(Map.of("message", "Error: unauthorized"));
        }
    }

    public boolean validPlayerColor(String playerColor) {
        return (playerColor.equalsIgnoreCase("WHITE") || playerColor.equalsIgnoreCase("BLACK")
                || playerColor.equalsIgnoreCase("RESIGN"));
    }

    public void runtimeExceptionCatch(Context ctx, RuntimeException e) {
        if (e.getMessage().contains("Bad Request")) {
            ctx.status(400).json(Map.of("message", "Error: bad request"));
        } else {
            ctx.status(401).json(Map.of("message", "Error: unauthorized"));
        }
    }



    public int run(int desiredPort) {
        javalin = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        }).start(desiredPort);

        registerEndpoints();

        javalin.ws("/ws", ws -> {
            ws.onConnect(webSocketHandler);
            ws.onMessage(webSocketHandler);
            ws.onClose(webSocketHandler);
        });

        System.out.println("Server running on port " + desiredPort);
        return javalin.port();
    }


    public void stop() {
        javalin.stop();
    }
}
