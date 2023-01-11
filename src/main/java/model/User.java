package model;

public class User {
    private String userId;
    private String password;
    private String name;
    private String email;

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    private User(UserBuilder builder) {
        this.userId = builder.userId;
        this.password = builder.password;
        this.name = builder.name;
        this.email = builder.email;
    }

    public User() {}

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }

    public static class UserBuilder {
        private String userId;
        private String password;
        private String name;
        private String email;

        public static UserBuilder builder() {
            return new UserBuilder();
        }

        public UserBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder setName(String name) {
            this.name = name;
            return this;

        }

        public UserBuilder setUserId(String userId) {
            this.userId = userId;
            return this;

        }

        public UserBuilder setPassword(String password) {
            this.password = password;
            return this;

        }

        public User build() {
            return new User(this);
        }
    }
}
