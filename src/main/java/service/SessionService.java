package service;

import db.SessionDatabase;
import dto.Session;
import dto.SessionCookie;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SessionService {
    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

    private static final Map<String, String> options = new HashMap<>();

    public static SessionCookie createSession(String username, Map<String, String> options) throws IllegalStateException {
        SessionService.resetOptions();

        SessionService.options.putAll(options);

        SessionCookie session = new SessionCookie(username, Map.copyOf(SessionService.options));

        return session;
    }

    public static Session findSessionBySID(String sid) {
        return SessionDatabase.findSessionBySid(sid)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
    }

    private static void resetOptions() {
        options.clear();
        options.put("path", "/");
    }
}
