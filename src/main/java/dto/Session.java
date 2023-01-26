package dto;

import java.time.LocalTime;
import java.util.UUID;

import static java.time.LocalTime.now;

public class Session {
    protected String sessionId;
    protected final LocalTime createDate;
    protected String userId;
    protected String userName;

    protected Session(String userId, String userName) {
        this.sessionId = String.valueOf(UUID.randomUUID());
        this.userId = userId;
        this.userName = userName;
        this.createDate = now();
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserId() {
        return userId;
    }
}
