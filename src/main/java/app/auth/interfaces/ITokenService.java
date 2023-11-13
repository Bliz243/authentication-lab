package app.auth.interfaces;

public interface ITokenService {
    String generateToken(String username);

    boolean validateToken(String token);

    String refreshToken(String oldToken);

    String getUsername(String token);

    String getToken(String username);

    void storeToken(String username, String token);

    void removeToken(String username);

    void clearTokens();
}
