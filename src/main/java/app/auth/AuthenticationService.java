package app.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import app.auth.interfaces.IAuthenticationService;
import app.auth.interfaces.IPasswordService;
import app.util.ConfigManager;
import app.util.PolicyConfig;

public class AuthenticationService implements IAuthenticationService {

    private IPasswordService passwordService;
    private static final Logger logger = Logger.getLogger(AuthenticationService.class.getName());
    public PolicyConfig policies;
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
        for (Map.Entry<String, PolicyConfig.RolePolicy> entry : policies.getPolicies().entrySet()) {
            PolicyConfig.RolePolicy rolePolicy = entry.getValue();

            if (rolePolicy.getMembers().contains(user.toLowerCase())
                    && rolePolicy.getPermissions().contains(operation)) {
                return true;
            }
        }
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

     public boolean createNewRole(String role, List<String> permissions) throws IOException {
        if (policies.getPolicies().containsKey(role)) {
            return false;
        }

        policies.getPolicies().put(role, new PolicyConfig.RolePolicy(new ArrayList<>(), permissions));
        ConfigManager.getInstance().writeJson(policies, accessFileParameter);
        logger.info(String.format("New role '%s' created.", role));
        return true;
    }

    public void deleteRole(String role) throws IOException {
        if (policies.getPolicies().containsKey(role)) {
            policies.getPolicies().remove(role);
            ConfigManager.getInstance().writeJson(policies, accessFileParameter);
            logger.info(String.format("Role '%s' deleted.", role));
        } else {
            throw new IllegalArgumentException("The role does not exist: " + role);
        }
    }

    public List<String> getListOfRoles() {
        return new ArrayList<>(policies.getPolicies().keySet());
    }

    public void addPermissionToRole(String role, String permission) throws IOException {
        if (!policies.getPolicies().containsKey(role)) {
            throw new IllegalArgumentException("The role does not exist: " + role);
        }
        PolicyConfig.RolePolicy rolePolicy = policies.getPolicies().get(role);
        if (!rolePolicy.getPermissions().contains(permission)) {
            rolePolicy.getPermissions().add(permission);
            ConfigManager.getInstance().writeJson(policies, accessFileParameter);
            logger.info(String.format("Permission '%s' added to role '%s'.", permission, role));
        } else {
            logger.warning(String.format("Permission '%s' already exists in role '%s'.", permission, role));
        }
    }

    public boolean authenticate(String username, String password) {
        boolean authenticated = passwordService.verifyPassword(username, password);
        logger.info(String.format("Authentication attempt for user %s: %s", username,
                authenticated ? "SUCCESS" : "FAILURE"));
        return authenticated;
    }
}
