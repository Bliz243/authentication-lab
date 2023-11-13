package app.auth;

import java.util.List;

import app.util.ConfigManager;

public class AccessController {

    private ConfigManager configManager;

    public AccessController() {
        this.configManager = ConfigManager.getInstance();
    }

    public boolean hasPermission(String user, String operation) {
        // Check if the user has the operation in their direct permissions
        List<String> permissions = userPermissions.getOrDefault(user, List.of());
        if (permissions.contains(operation)) {
            return true;
        }

        // Check if the user has a role that has the operation in its permissions
        for (String role : rolePermissions.keySet()) {
            if (rolePermissions.get(role).contains(operation)) {
                return true;
            }
        }

        // The user does not have permission
        return false;
    }
}
