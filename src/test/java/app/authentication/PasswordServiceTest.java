package app.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.auth.EncryptionService;
import app.auth.PasswordService;
import app.auth.interfaces.IEncryptionService;
import app.util.ConfigManager;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordServiceTest {
    private PasswordService passwordService;

    @BeforeEach
    public void setUp() throws IOException {
        // Create a temporary file for testing
        File tempFile = File.createTempFile("passwordsTest", ".txt");
        tempFile.deleteOnExit();

        // Write test data to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("alice:password1");
            writer.newLine();
            writer.write("bob:password2");
            writer.newLine();
            writer.write("charlie:password3");
            writer.newLine();
        }

        // Create a new PasswordService instance for each test
        IEncryptionService encryptionService = new EncryptionService();
        ConfigManager.getInstance().setParameter("passwordFile", tempFile.getAbsolutePath());
        passwordService = new PasswordService(encryptionService);
    }

    @Test
    public void testVerifyPassword() {
        assertTrue(passwordService.verifyPassword("alice", "password1"));
        assertFalse(passwordService.verifyPassword("alice", "wrongpassword"));
        assertFalse(passwordService.verifyPassword("nonexistentuser", "password"));
    }

    @Test
    public void testUserExists() {
        assertTrue(passwordService.userExists("alice"));
        assertFalse(passwordService.userExists("nonexistentuser"));
    }

    @Test
    public void testCreateNewUser() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String username = "david";
        String password = "password4";
        passwordService.createNewUser(username, password);
        assertTrue(passwordService.verifyPassword(username, password));
        assertTrue(passwordService.userExists(username));
    }

    @Test
    public void testUpdateExistingPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String username = "alice";
        String newPassword = "newpassword";
        passwordService.updateExistingPassword(username, newPassword);
        assertTrue(passwordService.verifyPassword(username, newPassword));
    }
}