package app.auth;

import java.util.HashMap;
import java.util.Map;

public class TokenStorage {
    private static final TokenStorage instance = new TokenStorage();

    private final Map<String, String> tokenMap = new HashMap<>();

    public TokenStorage() {
    }

    public static TokenStorage getInstance() {
        return instance;
    }

    public String getToken(String username) {
        return tokenMap.get(username);
    }

    public void storeToken(String username, String token) {
        tokenMap.put(username, token);
        // No need to call saveTokens() since we’re not writing to a file
    }

    public void removeToken(String username) {
        tokenMap.remove(username);
        // No need to call saveTokens() since we’re not writing to a file
    }

    public void clearTokens() {
        tokenMap.clear();
        // No need to call saveTokens() since we’re not writing to a file
    }
}
