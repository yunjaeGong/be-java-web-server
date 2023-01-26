package db;

import dto.Session;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionDatabase {

    private static Map<String, Session> sessions = new HashMap<>();

    public static Optional<Session> findSessionBySid(String sid) {
        return Optional.ofNullable(sessions.get(sid));
    }

    public static void addSession(Session s) {
        sessions.put(s.getSessionId(), s);
    }

    public static Optional<Collection<Session>> findAll() {
        return Optional.of(sessions.values());
    }

    public static void clear() {
        sessions = new HashMap<>();
    }
}
