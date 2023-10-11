package app.authentication;

import app.auth.PasswordStorage;
import app.auth.PasswordVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordVerifierTest {

    private PasswordVerifier passwordVerifier;

    @BeforeEach
    void setUp() {
        PasswordStorage passwordStorage = new PasswordStorage();
        passwordVerifier = new PasswordVerifier(passwordStorage);
    }

    @Test
    void testVerifyPassword() {
        // Assuming the password file has an entry for user "alice" with password "secret"
        assertTrue(passwordVerifier.verifyPassword("alice", "secret"));
        assertFalse(passwordVerifier.verifyPassword("alice", "wrongPassword"));
    }
}
