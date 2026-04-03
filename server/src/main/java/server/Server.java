package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import io.javalin.json.JavalinGson;
import service.GameService;
import service.UserService;

import java.util.Map;

public class Server {

    private Javalin javalin;
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserService userService;
    private GameService gameService;

    public Server() {
        try {
            DatabaseManager dbManager = new DatabaseManager();
            dbManager.configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to configure database", e);
        }

        initializeDAOs();
        initializeServices();
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
        // CLEAR APPLICATION
        javalin.delete("/db", ctx -> {
            userDAO.clearUsers();
            gameDAO.clearGames();
            authDAO.clearAuths();

            ctx.status(200);
            ctx.json(Map.of());
        });

        // REGISTER USER
        javalin.post("/user", ctx -> {
            RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
            try {
                UserResult result = userService.register(request);
                ctx.status(200).json(result);
            } catch (AlreadyTakenException e) {
                ctx.status(403).json(Map.of("message", "Error: already taken"));
            } catch (RuntimeException e) {
                ctx.status(400).json(Map.of("message", "Error: bad request"));
            }
        });

        // USER LOGIN
        javalin.post("/session", ctx -> {
            LoginRequest request = ctx.bodyAsClass(LoginRequest.class);
            try {
                UserResult result = userService.login(request);
                ctx.status(200).json(result);
            } catch (RuntimeException e) {
                runtimeExceptionCatch(ctx, e);
            }
        });

        // USER LOGOUT
        javalin.delete("/session", ctx -> {
            String authToken = ctx.header("Authorization");
            try {
                userService.logout(authToken);
                ctx.status(200).json(Map.of());
            } catch (RuntimeException e) {
                ctx.status(401).json(Map.of("message", "Error: unauthorized"));
            }
        });

        // LIST GAMES
        javalin.get("/game", ctx -> {
            String authToken = ctx.header("Authorization");
            try {
                ListGamesResult result = gameService.listGames(authToken);
                ctx.status(200).json(result);
            } catch (RuntimeException e) {
                ctx.status(401).json(Map.of("message", "Error: unauthorized"));
            } catch (DataAccessException e) {
                ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
            }
        });

        // CREATE GAME
        javalin.post("/game", ctx -> {
            String authToken = ctx.header("Authorization");
            CreateGameRequest request = ctx.bodyAsClass(CreateGameRequest.class);
            try {
                CreateGameResult result = gameService.createGame(authToken, request);
                ctx.status(200).json(result);
            } catch (RuntimeException e) {
                runtimeExceptionCatch(ctx, e);
            }
        });

        // JOIN GAME
        javalin.put("/game", ctx -> {
            String authToken = ctx.header("Authorization");
            JoinGameRequest request = ctx.bodyAsClass(JoinGameRequest.class);
            try {
                gameService.joinGame(authToken, request);
                ctx.status(200).json(Map.of());
            } catch (AlreadyTakenException e) {
                ctx.status(403).json(Map.of("message", "Error: already taken"));
            } catch (RuntimeException e) {
                runtimeExceptionCatch(ctx, e);
            }
        });
    }

    public void runtimeExceptionCatch(Context ctx, RuntimeException e) {
        if (e.getMessage().contains("Bad Request")) {
            ctx.status(400).json(Map.of("message", "Error: bad request"));
        } else {
            ctx.status(401).json(Map.of("message", "Error: unauthorized"));
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
