package app.auth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import app.auth.interfaces.IEncryptionService;
import app.auth.interfaces.IPasswordService;
import app.server.PrintServer;
import app.util.ConfigManager;

/**
 * This class represents a service for managing user passwords.
 * It provides methods for verifying passwords, loading and saving passwords from a file, creating new users, updating existing passwords, and deleting users.
 * Passwords are stored in a map with usernames as keys and hashed passwords as values.
 * The PasswordService class uses an IEncryptionService instance to encrypt and verify passwords.
 */
public class PasswordService implements IPasswordService {
    private static final Logger logger = Logger.getLogger(PrintServer.class.getName());
    private String PASSWORD_FILE;
    private Map<String, String> passwordMap;
    private IEncryptionService encryptionService;

    public PasswordService(IEncryptionService encryptionService) {
        this.PASSWORD_FILE = ConfigManager.getInstance().getParameter("passwordFile");
        this.encryptionService = encryptionService;
        passwordMap = new HashMap<>();
        loadPasswords();
    }

    public PasswordService(IEncryptionService encryptionService, String setFileParamter) {
        this.PASSWORD_FILE = ConfigManager.getInstance().getParameter(setFileParamter);
        this.encryptionService = encryptionService;
        passwordMap = new HashMap<>();
        loadPasswords();
    }

    public boolean verifyPassword(String username, String providedPassword) {
        String storedPasswordHash = getPassword(username);
        if (storedPasswordHash == null) {
            return false;
        }

        boolean[] requiresUpdate = new boolean[1];
        try {
            return encryptionService.verifyPassword(providedPassword, storedPasswordHash, requiresUpdate);
        } catch (Exception e) {
            return false;
        }
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

    public void createNewUser(String username, String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (!userExists(username)) {
            String hashedPass = encryptionService.generateSecurePassword(password);
            passwordMap.put(username, hashedPass);
            savePasswords();
            logger.info("New user created: " + username);
        } else {
            logger.warning("User already exists: " + username);
        }
    }

    public void updateExistingPassword(String username, String newPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (userExists(username)) {
            String hashedPass = encryptionService.generateSecurePassword(newPassword);
            passwordMap.put(username, hashedPass);
            savePasswords();
            logger.info("Password updated for user: " + username);
        } else {
            logger.warning("User does not exist: " + username);
        }
    }

    public void deleteUser(String username) {
        if (userExists(username)) {
            passwordMap.remove(username);
            savePasswords();
            logger.info("User deleted: " + username);
        } else {
            logger.warning("Cannot delete user that does not exist: " + username);
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
