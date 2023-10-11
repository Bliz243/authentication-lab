package app.auth;

import app.log.AppLogger;
import app.util.ConfigManager;
import java.util.logging.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PasswordStorage {

    private static final Logger logger = AppLogger.getLogger(PasswordStorage.class.getName());
    private static final String PASSWORD_FILE = ConfigManager.getInstance().getParameter("passwordFile");  // adjusted line
    private Map<String, String> passwordMap;

    public PasswordStorage() {
        passwordMap = new HashMap<>();
        loadPasswords();
    }

    private void loadPasswords() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PASSWORD_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length >= 2) {
                    String username = parts[0];
                    String password = parts[1];
                    passwordMap.put(username, password);
                }
            }
        } catch (IOException e) {
            logger.severe("Failed to load passwords from file: " + e.getMessage());
        }
    }

    public String getPassword(String username) {
        return passwordMap.get(username);
    }

    public void updatePassword(String username, String newPassword) {
        passwordMap.put(username, newPassword);
        savePasswords();
    }

    private void savePasswords() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASSWORD_FILE))) {
            for (Map.Entry<String, String> entry : passwordMap.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            logger.severe("Failed to save passwords to file: " + e.getMessage());
        }
    }
}
