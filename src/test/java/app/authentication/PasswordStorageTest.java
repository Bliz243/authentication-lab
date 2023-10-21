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
        assertNull(passwordStorage.getPassword("nonexistentUser"));
    }

    @Test
    void testUpdatePassword() {
        passwordStorage.updateExistingPassword("bob", "newSecret");
        assertEquals("newSecret", passwordStorage.getPassword("bob"));
    }
}
