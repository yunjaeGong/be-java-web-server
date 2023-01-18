package dto;

import java.time.LocalTime;
import java.util.UUID;

import static java.time.LocalTime.now;

public class Session {
    protected final LocalTime createDate;
    protected String sessionId;
    protected String userId;

    protected Session(String userId) {
        this.userId = userId;
        this.createDate = now();
        sessionId = String.valueOf(UUID.randomUUID());
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserId() {
        return userId;
    }
}
