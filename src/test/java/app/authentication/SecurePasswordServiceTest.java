package app.authentication;

import app.auth.SecurePasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SecurePasswordServiceTest {

    private SecurePasswordService securePasswordService;

    @BeforeEach
    void setUp() {
        securePasswordService = new SecurePasswordService();
    }

    @Test
    void testGenerateAndVerifyPassword() throws Exception {
        String originalPassword = "testPassword";
        String hashedPassword = securePasswordService.generateSecurePassword(originalPassword);

        boolean[] requiresUpdate = new boolean[1];
        assertTrue(securePasswordService.verifyPassword(originalPassword, hashedPassword, requiresUpdate));
        assertFalse(requiresUpdate[0]);
    }

    @Test
    void testInvalidPassword() throws Exception {
        String originalPassword = "testPassword";
        String hashedPassword = securePasswordService.generateSecurePassword(originalPassword);

        boolean[] requiresUpdate = new boolean[1];
        assertFalse(securePasswordService.verifyPassword("wrongPassword", hashedPassword, requiresUpdate));
    }
}
