import db.UserDatabase;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class UserServiceTest {

    @BeforeEach
    private void cleanUpDatabase() {
        UserDatabase.clear();
    }

    @Test
    public void Given_DuplicateUserId_When_SignUpUser_Then_IllegalStateException() {
        // given
        User user1 = new User.UserBuilder().setUserId("aaaa").setName("bbbb").setEmail("aaaa.gmail.com").setPassword("1234").build();
        User user2 = new User.UserBuilder().setUserId("aaaa").setName("bbbb").setEmail("aaaa.gmail.com").setPassword("1234").build();

        UserService.signUpUser(user1);

        // when
        assertThatThrownBy(() -> UserService.signUpUser(user2)).isInstanceOf(IllegalStateException.class)
                // then
                .hasMessageContaining("중복된 아이디입니다.");
    }

    @Test
    public void Given_NonExistingUserId_When_FindByUserId_Then_IllegalArgumentException() {
        // given
        User user1 = new User.UserBuilder().setUserId("aaaa").setName("bbbb").setEmail("aaaa.gmail.com").setPassword("1234").build();

        // when
        assertThatThrownBy(() -> UserService.findUserById(user1.getUserId()))
                .isInstanceOf(IllegalArgumentException.class)
                // then
                .hasMessageContaining("해당 유저가 존재하지 않습니다.");
    }

    @Test
    public void Given_ExistingUserId_When_FindByUserId_Then_ExpectedUser() {
        // given
        User user1 = new User.UserBuilder().setUserId("aaaa").setName("bbbb").setEmail("aaaa.gmail.com").setPassword("1234").build();

        // when
        UserService.signUpUser(user1);

        // then
        assertThat(UserService.findUserById(user1.getUserId()))
                .isInstanceOf(User.class)
                .isEqualTo(user1);
    }

    @Test
    public void Given_ExistingUser_When_LoginUser_Then_Success() {
        // given
        User existingUser = new User.UserBuilder().setUserId("a").setPassword("a").build();
        UserService.signUpUser(existingUser);

        // when
        boolean loginSuccess = UserService.loginUser("a", "a");

        // then
        assertThat(loginSuccess).isTrue();
    }

    @Test
    public void Given_NonExistingUser_When_LoginUser_Then_Fail() {
        // given, when
        assertThatThrownBy(() -> UserService.loginUser("a", "a"))
                .isInstanceOf(IllegalArgumentException.class)
                // then
                .hasMessageContaining("해당 유저가 존재하지 않습니다.");
    }
}
