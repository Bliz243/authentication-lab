package app.authentication;

import app.auth.PasswordStorage;
import app.auth.PasswordVerifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

class PasswordStorageTest {

    private PasswordStorage passwordStorage;
    private PasswordVerifier passwordVerifier;

    @BeforeEach
    void setUp() {
        passwordStorage = new PasswordStorage();
        passwordVerifier = new PasswordVerifier(passwordStorage);
    }

    @Test
    void testGetPassword() {
        assertNull(passwordStorage.getPassword("nonexistentUser"));
    }

    @Test
    void storePassword() {
        
    }

    @Test
    void testUpdatePassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        passwordStorage.updateExistingPassword("bob", "newSecret");
        assertTrue(passwordVerifier.verifyPassword("bob", "newSecret"));
    }
}
