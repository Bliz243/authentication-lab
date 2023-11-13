package app.auth.interfaces;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface IEncryptionService {

    String generateSecurePassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException;

    boolean verifyPassword(String password, String storedPasswordHash, boolean[] requiresUpdate)
            throws NoSuchAlgorithmException, InvalidKeySpecException;

}
