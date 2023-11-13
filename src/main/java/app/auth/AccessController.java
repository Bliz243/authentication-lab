package app.auth;

import java.io.IOException;
import java.util.List;

import app.util.ConfigManager;
import app.util.PolicyConfig;

public class AccessController {

    private ConfigManager configManager;
    private PolicyConfig policies;

    public AccessController() throws IOException {
        this.configManager = ConfigManager.getInstance();
        this.policies = ConfigManager.getInstance().readJson();
    }

    public boolean hasPermission(String user, String operation) {
        // Check if the user has the operation in their direct permissions
        if (policies.getPolicies().) {
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
