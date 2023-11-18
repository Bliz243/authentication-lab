package app.auth.interfaces;

import java.io.IOException;
import java.util.List;

public interface IAuthenticationService {
    boolean authenticate(String username, String password);

    boolean hasRBACPermission(String user, String operation);

    boolean hasACLPermission(String user, String operation);

    void setUserRole(String user, String role) throws IOException;

    void deleteRole(String role) throws IOException;

    boolean createNewRole(String role, List<String> permissions) throws IOException;

    void addPermissionToRole(String role, String permission) throws IOException;

    void removeCommandFromRole(String role, String operation) throws IOException;

    void addUserToCommand(String user, String operation) throws IOException;

    void removeUserFromCommand(String user, String operation) throws IOException;

    String getRBACAvailableCommands(String user);

    String getACLAvailableCommands(String user);

}
