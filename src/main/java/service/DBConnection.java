package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import static utility.CredentialsParser.getDBCredentials;

public class DBConnection {
    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);

    private static Connection conn = null;

    private DBConnection() {}

    public static Connection getInstance() {
        Map<String, String> credentials = getDBCredentials();
        try {
            conn = DriverManager.getConnection(credentials.get("url"));
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return conn;
    }
}
