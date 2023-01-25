import model.User;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.*;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static utility.CredentialsParser.getDBCredentials;

public class MySQLConnectionTest {
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;

    private static Connection conn = null;

    @BeforeAll
    public static void makeConnection() throws SQLException {
        Map<String, String> credentials = getDBCredentials();
        conn = DriverManager.getConnection(credentials.get("url"));
        System.setOut(new PrintStream(outContent));
    }

    @Test
    @DisplayName("user 테이블 삽입 테스트")
    public void insertUserIntoTable() throws SQLException {
        PreparedStatement pstmt = null;
        User user = new User.UserBuilder()
                .setUserId("gildong").setPassword("1234")
                .setEmail("gildong@asdf.com").setName("Hong Gildong").build();

        try {
            String sql = "INSERT INTO users VALUES (default, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getEmail());

            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
        }
        ResultSet result = null;

        try {
            String sql = "SELECT * FROM users";

            pstmt = conn.prepareStatement(sql);
            result = pstmt.executeQuery();
        } finally {
            if(result != null && result.next()) {
                String userId = result.getString("userId");
                String name = result.getString("name");

                System.out.print(userId + " " + name);
                assertThat(outContent.toString()).isEqualTo("gildong Hong Gildong");
            }
        }
    }

    @AfterEach
    public void clearDatabase() {
        PreparedStatement pstmt = null;

        try {
            String sql = "DELETE FROM users WHERE userId=?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "gildong");
            pstmt.executeQuery();
        } catch (Exception e) {
        }
    }

    @AfterAll
    public static void endConnection() throws SQLException {
        if(conn != null)
            conn.close();
        System.setOut(originalOut);
    }
}
