package server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        // CLEAR APPLICATION
        javalin.delete("/db", ctx -> {

            UserDAO.clearUsers();
            GameDAO.clearGames();
            AuthDAO.clearAuth();

            ctx.status(200);
            ctx.json(Map.of());
        });

        javalin.post("/user", ctx -> {

        });

        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
