package app.auth;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenService {

    private SecureRandom random = new SecureRandom();

    public String generateToken(String username) {
        byte[] randomBytes = new byte[24];
        random.nextBytes(randomBytes);
        String token = Base64.getEncoder().encodeToString(randomBytes);
        String combined = username + ":" + token;
        return Base64.getEncoder().encodeToString(combined.getBytes());
    }

    public boolean validateToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");
            if (parts.length < 2)
                return false;
            String username = parts[0];
            String storedToken = TokenStorage.getInstance().getToken(username);
            storedToken = new String(Base64.getDecoder().decode(storedToken));
            return storedToken != null && storedToken.equals(decoded);
        } catch (Exception e) {
            return false;
        }
    }

    public String refreshToken(String oldToken) {
        try {
            String decoded = new String(Base64.getDecoder().decode(oldToken));
            String[] parts = decoded.split(":");
            if (parts.length < 2)
                return null;
            String username = parts[0];
            return generateToken(username);
        } catch (Exception e) {
            return null;
        }
    }

    public String getUsername(String token) {
        String decoded = new String(Base64.getDecoder().decode(token));
        String[] parts = decoded.split(":");
        return parts[0];
    }
}
