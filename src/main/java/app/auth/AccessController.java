package app.auth;

import java.io.IOException;
import java.util.Map;

import app.util.ConfigManager;
import app.util.PolicyConfig;

public class AccessController {

    private PolicyConfig policies;

    public AccessController() throws IOException {
        this.policies = ConfigManager.getInstance().readJson();
    }

    public boolean hasPermission(String user, String operation) {
        // Check if the user has the operation in their direct permissions
        for (Map.Entry<String, PolicyConfig.UserPolicy> entry : policies.getPolicies().entrySet()) {
            PolicyConfig.UserPolicy policy = entry.getValue();

            System.out.println("User: " + user);
            System.out.println("Role: " + entry.getKey());
            System.out.println("Permissions: " + policy.getPermissions());
            System.out.println("Members: " + policy.getMembers());

            if (policy.getMembers().contains(user.toLowerCase()) && policy.getPermissions().contains(operation)) {
                return true;
            }
        }

        // The user does not have permission
        return false;
    }
}
