package app.authentication;

import app.auth.EncryptionService;
import app.auth.PasswordService;
import app.auth.interfaces.IEncryptionService;
import app.util.ConfigManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

class PasswordServiceTest {
    private PasswordService passwordService;
    private IEncryptionService EncryptionService;
    private String passwordFilePath;

    @BeforeEach
    public void setUp() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        EncryptionService = new EncryptionService();
        passwordFilePath = "passwordFileTest";
        passwordService = new PasswordService(EncryptionService, passwordFilePath);

        Files.deleteIfExists(Paths.get(ConfigManager.getInstance().getParameter(passwordFilePath)));
        Files.createFile(Paths.get(ConfigManager.getInstance().getParameter(passwordFilePath)));

        String username = "alice";
        String password = "password";
        passwordService.createNewUser(username, password);

    }

    @AfterEach
    public void tearDown() throws IOException {
        // Clean up the test password file after each test
        Files.deleteIfExists(Paths.get(passwordFilePath));
        Files.createFile(Paths.get(passwordFilePath));
    }

    @Test
    public void testCreateNewUser() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String username = "david";
        String password = "password4";

        assertFalse(passwordService.userExists(username), "User should not exist before creation.");
        passwordService.createNewUser(username, password);
        assertTrue(passwordService.userExists(username), "User should exist after creation.");
        assertTrue(passwordService.verifyPassword(username, password), "User should be verifiable after creation.");
    }

    @Test
    public void testUpdateExistingPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String username = "alice";
        String newPassword = "newpassword";
        passwordService.updateExistingPassword(username, newPassword);
        assertTrue(passwordService.verifyPassword(username, newPassword));
    }

    @Test
    public void testVerifyPassword() {
        assertTrue(passwordService.verifyPassword("alice", "password"));
        assertFalse(passwordService.verifyPassword("alice", "wrongpassword"));
        assertFalse(passwordService.verifyPassword("nonexistentuser", "password"));
    }

    @Test
    public void testUserExists() {
        assertTrue(passwordService.userExists("alice"));
        assertFalse(passwordService.userExists("nonexistentuser"));
    }

    @Test
    public void testDeleteUser() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String username = "deletableUser";
        String password = "password";
        passwordService.createNewUser(username, password);
        assertTrue(passwordService.userExists(username), "User should exist after creation.");

        // Delete the user and verify deletion
        passwordService.deleteUser(username);
        assertFalse(passwordService.userExists(username), "User should not exist after deletion.");
    }
}
