package service;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public static void signUpUser(User user) throws IllegalStateException {
        Database.findUserById(user.getUserId())
                .ifPresent(a->{
                    throw new IllegalStateException("중복된 아이디입니다. 다른 아이디를 사용해주세요.");
                });

        Database.addUser(user);
    }

    public static boolean loginUser(String userId, String password) {
        return UserService.findUserById(userId)
                .getPassword()
                .equals(password);
    }

    public static User findUserById(String userId) throws IllegalArgumentException {
        return Database.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
    }
}
