package app.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import app.auth.EncryptionService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptionServiceTest {
    private EncryptionService encryptionService;

    @BeforeEach
    public void setUp() {
        encryptionService = new EncryptionService();
    }

    @Test
    public void testGenerateSecurePassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "password123";
        String securePassword = encryptionService.generateSecurePassword(password);

        assertNotNull(securePassword);
        assertTrue(securePassword.contains("$"));
    }

    @Test
    public void testVerifyPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String password = "password123";
        String securePassword = encryptionService.generateSecurePassword(password);

        boolean[] requiresUpdate = new boolean[1];
        assertTrue(encryptionService.verifyPassword(password, securePassword, requiresUpdate));
        assertFalse(requiresUpdate[0]);

        String wrongPassword = "wrongpassword";
        assertFalse(encryptionService.verifyPassword(wrongPassword, securePassword, requiresUpdate));
        assertFalse(requiresUpdate[0]);

        String[] parts = securePassword.split("\\$", 8);
        parts[0] = "WrongAlgorithm";
        String modifiedSecurePassword = String.join("$", parts);
        assertTrue(encryptionService.verifyPassword(password, modifiedSecurePassword, requiresUpdate));
        assertTrue(requiresUpdate[0]);
    }

    @Test
    public void testVerifyPassword_InvalidHashFormat() {
        String password = "password123";
        String invalidHash = "invalidHash";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            boolean[] requiresUpdate = new boolean[1];
            encryptionService.verifyPassword(password, invalidHash, requiresUpdate);
        });

        String expectedMessage = "Stored password hash has an invalid format.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}