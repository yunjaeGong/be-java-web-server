package db;

import dto.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.DBConnection;

import java.sql.*;
import java.util.*;

public class SessionDatabase {
    private static final Logger logger = LoggerFactory.getLogger(SessionDatabase.class);
    private static final Connection con = DBConnection.getInstance();

    public static Optional<Session> findSessionBySid(String sid) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Session session;

        try {
            String sql = "SELECT * FROM sessions where sid = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, sid);
            rs = pstmt.executeQuery();

            if(rs == null || !rs.next())
                return Optional.empty();

            session = new Session(rs.getString("sid"),
                    rs.getString("userId"),
                    rs.getString("userName"),
                    rs.getTimestamp("createDate").toLocalDateTime());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(session);
    }

    public static void addSession(Session s) {
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO sessions VALUES (?, ?, ?, ?)";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, s.getSessionId());
            pstmt.setString(2, s.getUserId());
            pstmt.setString(3, s.getUserName());
            pstmt.setTimestamp(4, Timestamp.valueOf(s.getCreateDate()));

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<Collection<Session>> findAll() {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Session> sessions = new ArrayList<>();

        try {
            String sql = "SELECT * FROM sessions";
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();

            sessions.add(new Session(rs.getString("sessionId"),
                    rs.getString("userId"),
                    rs.getString("userName"),
                    rs.getTimestamp("createDate").toLocalDateTime())
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(sessions);
    }

    public static void clear() {
        PreparedStatement pstmt = null;

        try {
            String sql = "TRUNCATE sessions";
            pstmt = con.prepareStatement(sql);

            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
