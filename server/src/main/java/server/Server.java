package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.json.JavalinGson;
import model.RegisterRequest;
import model.RegisterResult;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserDAO userDAO;
    private final UserService userService;
    private final AuthDAO authDAO;

    public Server() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jsonMapper(new JavalinGson());
        });

        // Register your endpoints and exception handlers here.

        // CLEAR APPLICATION
        javalin.delete("/db", ctx -> {
            userDAO.clearUsers();
            GameDAO.clearGames();  //MAKE SURE CLEARGAMES AND CLEARAUTH ARE NOT STATIC!!!!!!!!!
            authDAO.clearAuths();

            ctx.status(200);
            ctx.json(Map.of());
        });

        // REGISTER USER
        javalin.post("/user", ctx -> {
            RegisterRequest request = ctx.bodyAsClass(RegisterRequest.class);
            try {
                RegisterResult result = userService.register(request);
                ctx.status(200).json(result);
            } catch (RuntimeException e) {
                if (e.getMessage().contains("Bad Request")) {
                    ctx.status(400).json(Map.of("message", "Error: bad request"));
                } else {
                    ctx.status(403).json(Map.of("message", "Error: already taken"));
                }
            }
        });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
