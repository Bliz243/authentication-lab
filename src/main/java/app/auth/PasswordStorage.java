package app.auth;

import app.log.AppLogger;
import app.util.ConfigManager;
import java.util.logging.Logger;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public class PasswordStorage {

    private static final Logger logger = AppLogger.getLogger(PasswordStorage.class.getName());
    private static final String PASSWORD_FILE = ConfigManager.getInstance().getParameter("passwordFile");
    private Map<String, String> passwordMap;
    private SecurePasswordService passwordService;


    public PasswordStorage() {
        passwordService = new SecurePasswordService();
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

    public boolean userExists(String username) {
        return passwordMap.containsKey(username);
    }

    public void createNewUser(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (!userExists(username)) {
            String hashedPass = passwordService.generateSecurePassword(password);
            passwordMap.put(username, hashedPass);
            savePasswords();
            logger.info("New user created: " + username);
        } else {
            logger.warning("User already exists: " + username);
        }
    }

    public void updateExistingPassword(String username, String newPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (userExists(username)) {
            String hashedPass = passwordService.generateSecurePassword(newPassword);
            passwordMap.put(username, hashedPass);
            savePasswords();
            logger.info("Password updated for user: " + username);
        } else {
            logger.warning("User does not exist: " + username);
        }
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
