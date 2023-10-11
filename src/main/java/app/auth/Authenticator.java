package app.auth;

import java.util.logging.Logger;

public class Authenticator {

    private PasswordVerifier passwordVerifier;
    private static final Logger logger = Logger.getLogger(Authenticator.class.getName());

    public Authenticator(PasswordVerifier passwordVerifier) {
        this.passwordVerifier = passwordVerifier;
    }

    public boolean authenticate(String username, String password) {
        boolean authenticated = passwordVerifier.verifyPassword(username, password);
        logger.info(String.format("Authentication attempt for user %s: %s", username, authenticated ? "SUCCESS" : "FAILURE"));
        return authenticated;
    }
}
