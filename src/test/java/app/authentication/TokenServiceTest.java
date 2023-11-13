package app.authentication;

import app.auth.TokenService;
import app.auth.interfaces.ITokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TokenServiceTest {
    private ITokenService tokenService;

    @BeforeEach
    public void setUp() {
        tokenService = new TokenService();
    }

    @Test
    public void testGenerateToken() {
        String username = "testuser";
        String token = tokenService.generateToken(username);
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    public void testValidateToken() {
        String username = "testuser";
        String token = tokenService.generateToken(username);
        tokenService.storeToken(username, token);
        assertTrue(tokenService.validateToken(token));
    }

    @Test
    public void testValidateToken_InvalidToken() {
        assertFalse(tokenService.validateToken("invalidtoken"));
    }

    @Test
    public void testRefreshToken() {
        String username = "testuser";
        String oldToken = tokenService.generateToken(username);
        String newToken = tokenService.refreshToken(oldToken);
        assertNotNull(newToken);
        assertTrue(newToken.length() > 0);
        assertNotEquals(oldToken, newToken);
    }

    @Test
    public void testGetUsername() {
        String username = "testuser";
        String token = tokenService.generateToken(username);
        assertEquals(username, tokenService.getUsername(token));
    }

    @Test
    public void testGetToken() {
        String username = "testuser";
        String token = "testtoken";
        tokenService.storeToken(username, token);
        assertEquals(token, tokenService.getToken(username));
    }

    @Test
    public void testRemoveToken() {
        String username = "testuser";
        String token = "testtoken";
        tokenService.storeToken(username, token);
        tokenService.removeToken(username);
        assertNull(tokenService.getToken(username));
    }

    @Test
    public void testClearTokens() {
        String username1 = "testuser1";
        String token1 = "testtoken1";
        String username2 = "testuser2";
        String token2 = "testtoken2";
        tokenService.storeToken(username1, token1);
        tokenService.storeToken(username2, token2);
        tokenService.clearTokens();
        assertNull(tokenService.getToken(username1));
        assertNull(tokenService.getToken(username2));
    }
}