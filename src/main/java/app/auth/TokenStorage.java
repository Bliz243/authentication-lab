package app.auth;

import app.util.ConfigManager;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TokenStorage {
    private static final TokenStorage instance = new TokenStorage();

    private static final String TOKEN_FILE_PATH = ConfigManager.getInstance().getParameter("tokenFile");
    private Map<String, String> tokenMap = new HashMap<>();

    public TokenStorage() {
        loadTokens();
    }

    public static TokenStorage getInstance() {
        return instance;
    }

    private void loadTokens() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TOKEN_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length >= 2) {
                    String username = parts[0];
                    String token = parts[1];
                    tokenMap.put(username, token);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getToken(String username) {
        return tokenMap.get(username);
    }

    public void storeToken(String username, String token) {
        tokenMap.put(username, token);
        saveTokens();
    }

    public void removeToken(String username) {
        tokenMap.remove(username);
        saveTokens();
    }

    public void clearTokens() {
        tokenMap.clear();
        saveTokens();
    }

    private void saveTokens() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TOKEN_FILE_PATH))) {
            for (Map.Entry<String, String> entry : tokenMap.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
