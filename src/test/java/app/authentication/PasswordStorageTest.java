package app.authentication;

import app.auth.PasswordStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordStorageTest {

    private PasswordStorage passwordStorage;

    @BeforeEach
    void setUp() {
        passwordStorage = new PasswordStorage();
    }

    @Test
    void testGetPassword() {
        // Assuming the password file has an entry for user "alice" with password "secret"
        assertEquals("secret", passwordStorage.getPassword("alice"));
        assertNull(passwordStorage.getPassword("nonexistentUser"));
    }

    @Test
    void testUpdatePassword() {
        passwordStorage.updatePassword("bob", "newSecret");
        assertEquals("newSecret", passwordStorage.getPassword("bob"));
    }
}
