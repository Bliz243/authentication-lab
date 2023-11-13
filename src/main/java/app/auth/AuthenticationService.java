package app.auth;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import app.auth.interfaces.IAuthenticationService;
import app.auth.interfaces.IPasswordService;
import app.util.ConfigManager;
import app.util.PolicyConfig;

public class AuthenticationService implements IAuthenticationService {

    private IPasswordService passwordService;
    private static final Logger logger = Logger.getLogger(AuthenticationService.class.getName());
    private PolicyConfig policies;
    private String accessFileParameter = "accessPolicies";

    public AuthenticationService(IPasswordService passwordService) throws IOException {
        this.policies = ConfigManager.getInstance().readJson(accessFileParameter);
        this.passwordService = passwordService;
    }

    public AuthenticationService(IPasswordService passwordService, String setFileParamter) throws IOException {
        this.accessFileParameter = setFileParamter;
        this.policies = ConfigManager.getInstance().readJson(setFileParamter);
        this.passwordService = passwordService;
    }

    public boolean hasPermission(String user, String operation) {
        // Check if the user has the operation in their direct permissions
        for (Map.Entry<String, PolicyConfig.RolePolicy> entry : policies.getPolicies().entrySet()) {
            PolicyConfig.RolePolicy rolePolicy = entry.getValue();

            if (rolePolicy.getMembers().contains(user.toLowerCase())
                    && rolePolicy.getPermissions().contains(operation)) {
                return true;
            }
        }

        // The user does not have permission
        return false;
    }

    public void setUserRole(String user, String role) throws IOException {
        if (!policies.getPolicies().containsKey(role)) {
            throw new IllegalArgumentException("The role does not exist: " + role);
        }

        for (PolicyConfig.RolePolicy rolePolicy : policies.getPolicies().values()) {
            rolePolicy.getMembers().removeIf(existingUser -> existingUser.equalsIgnoreCase(user));
        }

        policies.getPolicies().get(role).getMembers().add(user);

        ConfigManager.getInstance().writeJson(policies, accessFileParameter);
    }

    public boolean authenticate(String username, String password) {
        boolean authenticated = passwordService.verifyPassword(username, password);
        logger.info(String.format("Authentication attempt for user %s: %s", username,
                authenticated ? "SUCCESS" : "FAILURE"));
        return authenticated;
    }
}
