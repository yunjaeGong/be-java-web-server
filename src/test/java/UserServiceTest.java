import db.Database;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class UserServiceTest {
    private static UserService userService = new UserService();

    @BeforeEach
    private void cleanUpDatabase() {
        Database.clear();
    }

    @Test
    public void Given_DuplicateUserId_When_SignUpUser_Then_IllegalStateException() {
        // given
        User user1 = new User.UserBuilder().setUserId("aaaa").setName("bbbb").setEmail("aaaa.gmail.com").setPassword("1234").build();
        User user2 = new User.UserBuilder().setUserId("aaaa").setName("bbbb").setEmail("aaaa.gmail.com").setPassword("1234").build();

        userService.signUpUser(user1);

        // when
        assertThatThrownBy(() -> userService.signUpUser(user2)).isInstanceOf(IllegalStateException.class)
                // then
                .hasMessageContaining("중복된 아이디입니다.");
    }

    @Test
    public void Given_NonExistingUserId_When_FindByUserId_Then_IllegalArgumentException() {
        // given
        User user1 = new User.UserBuilder().setUserId("aaaa").setName("bbbb").setEmail("aaaa.gmail.com").setPassword("1234").build();

        // when
        assertThatThrownBy(() -> userService.findUserById(user1.getUserId()))
                .isInstanceOf(IllegalArgumentException.class)
                // then
                .hasMessageContaining("해당 유저가 존재하지 않습니다.");
    }

    @Test
    public void Given_ExistingUserId_When_FindByUserId_Then_ExpectedUser() {
        // given
        User user1 = new User.UserBuilder().setUserId("aaaa").setName("bbbb").setEmail("aaaa.gmail.com").setPassword("1234").build();

        // when
        userService.signUpUser(user1);

        // then
        assertThat(userService.findUserById(user1.getUserId()))
                .isInstanceOf(User.class)
                .isEqualTo(user1);
    }
}
