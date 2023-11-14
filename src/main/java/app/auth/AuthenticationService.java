package app.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import app.auth.interfaces.IAuthenticationService;
import app.auth.interfaces.IPasswordService;
import app.util.ACLPolicy;
import app.util.ConfigManager;
import app.util.RBACPolicy;
import app.util.ACLPolicy.CommandPolicy;

public class AuthenticationService implements IAuthenticationService {

    private IPasswordService passwordService;
    private static final Logger logger = Logger.getLogger(AuthenticationService.class.getName());
    public RBACPolicy rbacPolicies;
    public ACLPolicy aclPolicy;
    private String accessFileParameter = "RBAC";

    public AuthenticationService(IPasswordService passwordService) throws IOException {
        this.rbacPolicies = ConfigManager.getInstance().readRBACJson(accessFileParameter);
        this.passwordService = passwordService;
    }

    public AuthenticationService(IPasswordService passwordService, String setFileParamter) throws IOException {
        this.accessFileParameter = setFileParamter;
        this.rbacPolicies = ConfigManager.getInstance().readRBACJson(setFileParamter);
        this.passwordService = passwordService;
    }

    public boolean hasRBACPermission(String user, String operation) {
        for (Map.Entry<String, RBACPolicy.RolePolicy> entry : rbacPolicies.getPolicies().entrySet()) {
            RBACPolicy.RolePolicy rolePolicy = entry.getValue();

            if (rolePolicy.getMembers().contains(user.toLowerCase())
                    && rolePolicy.getPermissions().contains(operation)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasACLPermission(String user, String operation) {
        ACLPolicy.CommandPolicy commandPolicy = aclPolicy.getPolicies().get(operation);
        return commandPolicy != null && commandPolicy.getMembers().contains(user);
    }

    public void setUserRole(String user, String role) throws IOException {
        if (!rbacPolicies.getPolicies().containsKey(role)) {
            throw new IllegalArgumentException("The role does not exist: " + role);
        }

        for (RBACPolicy.RolePolicy rolePolicy : rbacPolicies.getPolicies().values()) {
            rolePolicy.getMembers().removeIf(existingUser -> existingUser.equalsIgnoreCase(user));
        }

        rbacPolicies.getPolicies().get(role).getMembers().add(user);

        ConfigManager.getInstance().writeJson(rbacPolicies, accessFileParameter);
    }

    public boolean createNewRole(String role, List<String> permissions) throws IOException {
        if (rbacPolicies.getPolicies().containsKey(role)) {
            return false;
        }

        rbacPolicies.getPolicies().put(role, new RBACPolicy.RolePolicy(new ArrayList<>(), permissions));
        ConfigManager.getInstance().writeJson(rbacPolicies, accessFileParameter);
        logger.info(String.format("New role '%s' created.", role));
        return true;
    }

    public void deleteRole(String role) throws IOException {
        if (rbacPolicies.getPolicies().containsKey(role)) {
            rbacPolicies.getPolicies().remove(role);
            ConfigManager.getInstance().writeJson(rbacPolicies, accessFileParameter);
            logger.info(String.format("Role '%s' deleted.", role));
        } else {
            throw new IllegalArgumentException("The role does not exist: " + role);
        }
    }

    public List<String> getListOfRoles() {
        return new ArrayList<>(rbacPolicies.getPolicies().keySet());
    }

    public void addPermissionToRole(String role, String permission) throws IOException {
        if (!rbacPolicies.getPolicies().containsKey(role)) {
            throw new IllegalArgumentException("The role does not exist: " + role);
        }
        RBACPolicy.RolePolicy rolePolicy = rbacPolicies.getPolicies().get(role);
        if (!rolePolicy.getPermissions().contains(permission)) {
            rolePolicy.getPermissions().add(permission);
            ConfigManager.getInstance().writeJson(rbacPolicies, accessFileParameter);
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

    @Override
    public String getRBACAvailableCommands(String user) {
        StringBuilder commands = new StringBuilder();
        commands.append("\nAvailable commands:\n");

        for (Map.Entry<String, RBACPolicy.RolePolicy> entry : rbacPolicies.getPolicies().entrySet()) {
            RBACPolicy.RolePolicy rolePolicy = entry.getValue();
            if (rolePolicy.getMembers().contains(user)) {
                for (String permission : rolePolicy.getPermissions()) {
                    switch (permission) {
                        case "start":
                            commands.append("Start the system. ");
                            break;
                        case "stop":
                            commands.append("stop: Stops the print server.\\n" + //
                                    "");
                            break;
                        case "restart":
                            commands.append("restart: Restarts the print server.\n");
                            break;
                        case "status":
                            commands.append("status <printer>: Shows printer status. \n");
                            break;
                        case "readconfig":
                            commands.append("readConfig <parameter>: Reads configuration.\n");
                            break;
                        case "setconfig":
                            commands.append("setConfig <paramter> <value>: Sets configuration.\n");
                            break;
                        case "print":
                            commands.append("print <filename> <printer>: Prints the file.\n");
                            break;
                        case "queue":
                            commands.append("queue <printer>: Shows print queue. \n");
                            break;
                        case "topqueue":
                            commands.append("topQueue <printer> <job>: Moves job to top of queue.\n");
                            break;
                        case "updatepassword":
                            commands.append("updatePassword <username> <password>: Update user password\n");
                            break;
                        case "createuser":
                            commands.append("createUser <username> <password>: Creates a new user\n");
                            break;
                        case "updateuserperm":
                            commands.append(
                                    "updateUserPermission <username> <role>: Updates a users role on the print server\n");
                        default:
                            commands.append("Unknown permission. ");
                            break;
                    }
                }
            }
        }
        commands.append("logout: Logs current user out\n");
        return commands.toString();
    }

    @Override
    public String getACLAvailableCommands(String user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getACLAvailableCommands'");
    }

    @Override
    public void addUserToCommand(String user, String operation) {
        if (!aclPolicy.getPolicies().containsKey(operation)) {
            throw new IllegalArgumentException("The command does not exist: " + operation);
        }

    }

    @Override
    public void removeUserFromCommand(String user, String operation) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeUserFromCommand'");
    }
}
