package app.auth;

import app.log.AppLogger;
import org.apache.logging.log4j.Logger;

public class Authenticator {

    private PasswordVerifier passwordVerifier;
    private static final Logger logger = AppLogger.getLogger(Authenticator.class);

    public Authenticator(PasswordVerifier passwordVerifier) {
        this.passwordVerifier = passwordVerifier;
    }

    public boolean authenticate(String username, String password) {
        boolean authenticated = passwordVerifier.verifyPassword(username, password);
        logger.info("Authentication attempt for user {}: {}", username, authenticated ? "SUCCESS" : "FAILURE");
        return authenticated;
    }
}
