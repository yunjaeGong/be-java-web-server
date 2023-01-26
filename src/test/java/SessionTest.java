import db.UserDatabase;
import dto.SessionCookie;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.SessionService;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SessionTest {

    @BeforeEach
    private void clearUserDatabase() {
        UserDatabase.clear();
    }

    @Test
    public void Given_User_When_CreateSession_Then_CreateSession() {
        // given
        User existingUser = new User.UserBuilder().setUserId("a").setPassword("a").build();
        // UserService.signUpUser(existingUser);

        // when
        SessionCookie session = SessionService.createSession(existingUser.getUserId(), existingUser.getUsername(), Map.of());

        assertThat(session.toString());

        System.out.print(session);
    }

}
