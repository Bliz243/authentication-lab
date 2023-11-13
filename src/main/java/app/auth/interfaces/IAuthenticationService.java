package app.auth.interfaces;

import java.io.IOException;

public interface IAuthenticationService {
    boolean authenticate(String username, String password);

    boolean hasPermission(String user, String operation);

    void setUserRole(String user, String role) throws IOException;

    String getAvailableCommands(String user);
}
