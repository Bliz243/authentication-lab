package app.authentication;

import app.auth.PasswordStorage;
import app.auth.PasswordVerifier;
import app.auth.SecurePasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordVerifierTest {

    private PasswordVerifier passwordVerifier;
    private SecurePasswordService securePasswordService;
    private PasswordStorage passwordStorage;

    @BeforeEach
    void setUp() {
        passwordStorage = new PasswordStorage();
        passwordVerifier = new PasswordVerifier(passwordStorage);
        securePasswordService = new SecurePasswordService();
    }

    @Test
    void testVerifyPassword() throws Exception {
        // Assuming the password file has an entry for user "alice" with a hashed password
        String originalPassword = "secret";
        String hashedPassword = securePasswordService.generateSecurePassword(originalPassword);
        // Update the password storage with the hashed password for "alice"
        passwordStorage.updatePassword("alice", hashedPassword);

        assertTrue(passwordVerifier.verifyPassword("alice", originalPassword));
        assertFalse(passwordVerifier.verifyPassword("alice", "wrongPassword"));
    }
}
