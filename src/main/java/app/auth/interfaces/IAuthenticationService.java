package app.auth.interfaces;

import java.io.IOException;

public interface IAuthenticationService {
    boolean authenticate(String username, String password);

    boolean hasRBACPermission(String user, String operation);

    boolean hasACLPermission(String user, String operation);

    void setUserRole(String user, String role) throws IOException;

    void deleteRole(String role) throws IOException;

    void addUserToCommand(String user, String operation);

    void removeUserFromCommand(String user, String operation);

    String getRBACAvailableCommands(String user);

    String getACLAvailableCommands(String user);

}
