package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, Session session) {
        connections.computeIfAbsent(gameID, i -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void remove(Integer gameID, Session session) {
        Set<Session> sessions = connections.get(gameID);
        if (session != null) {
            sessions.remove(session);
        } if (sessions.isEmpty()) {
            connections.remove(gameID);
        }
    }

    public void broadcast(Integer gameID, Session excludeSession, String msg) throws IOException {
        Set<Session> sessions = connections.get(gameID);

        for (Session session: sessions) {
            if (session.isOpen()) {
                if (!session.equals(excludeSession)) {
                    session.getRemote().sendString(msg);
                }
            }
        }
    }
}