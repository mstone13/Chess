package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<WsContext>> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void add(Integer gameID, WsContext ctx) {
        connections.computeIfAbsent(gameID, k -> ConcurrentHashMap.newKeySet()).add(ctx);
    }

    public void remove(Integer gameID, WsContext ctx) {
        Set<WsContext> sessions = connections.get(gameID);
        if (sessions != null) {
            sessions.remove(ctx);
            if (sessions.isEmpty()) {
                connections.remove(gameID);
            }
        }
    }

    public void broadcast(Integer gameID, WsContext excludeCtx, ServerMessage msg) throws IOException {
        Set<WsContext> sessions = connections.get(gameID);
        if (sessions == null) {
            return; }

        String json = gson.toJson(msg);

        for (WsContext ctx : sessions) {
            if (excludeCtx == null || !ctx.equals(excludeCtx)) {
                if (ctx.session.isOpen()) {
                    ctx.send(json);
                }
            }
        }
    }

    public void saveSession(Integer gameID, WsContext ctx) {
        add(gameID, ctx);
    }
}