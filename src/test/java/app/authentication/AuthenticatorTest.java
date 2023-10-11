package app.authentication;

import app.auth.Authenticator;
import app.auth.PasswordVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticatorTest {

    private Authenticator authenticator;

    @BeforeEach
    void setUp() {
        PasswordVerifier passwordVerifier = new PasswordVerifier(new PasswordStorage());
        authenticator = new Authenticator(passwordVerifier);
    }

    @Test
    void testAuthenticate() {
        // Assuming the password file has an entry for user "alice" with password "secret"
        assertTrue(authenticator.authenticate("alice", "secret"));
        assertFalse(authenticator.authenticate("alice", "wrongPassword"));
    }
}
