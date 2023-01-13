package db;

import com.google.common.collect.Maps;

import model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Database {
    private static Map<String, User> users = new HashMap<>();

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static Optional<User> findUserById(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public static Optional<Collection<User>> findAll() {
        return Optional.of(users.values());
    }

    public static void clear() {
        users = new HashMap<>();
    }
}
