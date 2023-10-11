package app.auth;

public class PasswordVerifier {

    private PasswordStorage passwordStorage;

    public PasswordVerifier(PasswordStorage passwordStorage) {
        this.passwordStorage = passwordStorage;
    }

    public boolean verifyPassword(String username, String providedPassword) {
        String storedPassword = passwordStorage.getPassword(username);
        return storedPassword != null && storedPassword.equals(providedPassword);
    }
}
