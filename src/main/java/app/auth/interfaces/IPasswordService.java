package app.auth.interfaces;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public interface IPasswordService {

    String getPassword(String username);

    boolean userExists(String username);

    void createNewUser(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException;

    void updateExistingPassword(String username, String newPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException;

    boolean verifyPassword(String username, String providedPassword);

    void deleteUser(String username);
}
