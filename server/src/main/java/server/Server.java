package server;

import dataaccess.*;
import io.javalin.*;
import model.*;
import io.javalin.json.JavalinGson;
import service.GameService;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        });

        // Register your endpoints and exception handlers here.

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
            } catch (RuntimeException e) {
                ctx.status(400).json(Map.of("message", "Error: bad request"));
            } catch (AlreadyTakenException e) {
                ctx.status(403).json(Map.of("message", "Error: already taken"));
            }
        });

        // USER LOGIN
        javalin.post("/session", ctx -> {
           LoginRequest request = ctx.bodyAsClass(LoginRequest.class);
           try {
               UserResult result = userService.login(request);
               ctx.status(200).json(result);
           } catch (RuntimeException e) {
               if (e.getMessage().contains("Bad Request")) {
                   ctx.status(400).json(Map.of("message", "Error: bad request"));
               } else {
                   ctx.status(401).json(Map.of("message", "Error: unauthorized"));
               }
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
        });

        // CREATE GAME
        javalin.post("/game", ctx -> {
           String authToken = ctx.header("Authorization");
           CreateGameRequest request = ctx.bodyAsClass(CreateGameRequest.class);
           try {
               CreateGameResult result = gameService.createGame(authToken, request);
               ctx.status(200).json(result);
           } catch (RuntimeException e) {
               if (e.getMessage().contains("Bad Request")) {
                   ctx.status(400).json(Map.of("message", "Error: bad request"));
               } else {
                   ctx.status(401).json(Map.of("message", "Error: unauthorized"));
               }
            }
        });

        // JOIN GAME


    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
