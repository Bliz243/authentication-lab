package app.auth;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenService {

    private SecureRandom random = new SecureRandom();

    public String generateToken(String username) {
        byte[] randomBytes = new byte[24];
        random.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }

    public boolean validateToken(String username, String token) {
        String storedToken = TokenStorage.getInstance().getToken(username);
        return storedToken != null && storedToken.equals(token);
    }

    public String refreshToken(String token) {
        byte[] randomBytes = new byte[24];
        random.nextBytes(randomBytes);
        return Base64.getEncoder().encodeToString(randomBytes);
    }
}
