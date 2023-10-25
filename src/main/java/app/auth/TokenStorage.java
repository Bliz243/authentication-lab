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
        if (TOKEN_FILE_PATH != null && !TOKEN_FILE_PATH.trim().isEmpty()) {
            loadTokens();
        } else {
            System.err.println("Token file path is not set. Please configure the tokenFile parameter.");
        }
    }

    public static TokenStorage getInstance() {
        return instance;
    }

    private void loadTokens() {
        File tokenFile = new File(TOKEN_FILE_PATH);
        if (!tokenFile.exists()) {
            try {
                boolean created = tokenFile.createNewFile();
                if (!created) {
                    System.err.println("Failed to create token file at " + TOKEN_FILE_PATH);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return; // File is empty, no tokens to load
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(tokenFile))) {
            // ... existing code ...
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getToken(String username) {
        return tokenMap.get(username);
    }

    public void storeToken(String username, String token) {
        String[] parts = token.split(":");
        if (parts.length < 2) return;
        String actualToken = parts[1];
        tokenMap.put(username, actualToken);
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
