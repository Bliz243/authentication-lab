package app.auth;

public class PasswordVerifier {

    private PasswordStorage passwordStorage;
    private SecurePasswordService securePasswordService;

    public PasswordVerifier(PasswordStorage passwordStorage) {
        this.passwordStorage = passwordStorage;
        this.securePasswordService = new SecurePasswordService();
    }

    public boolean verifyPassword(String username, String providedPassword) {
        String storedPasswordHash = passwordStorage.getPassword(username);
        if (storedPasswordHash == null) {
            return false;
        }

        boolean[] requiresUpdate = new boolean[1];
        try {
            return securePasswordService.verifyPassword(providedPassword, storedPasswordHash, requiresUpdate);
        } catch (Exception e) {
            return false;
        }
    }
}
