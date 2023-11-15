package app.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import app.auth.interfaces.IAuthenticationService;
import app.auth.interfaces.IPasswordService;
import app.util.ACLPolicy;
import app.util.ConfigManager;
import app.util.RBACPolicy;

/**
 * AuthenticationService class implements IAuthenticationService interface and
 * provides methods for authentication, role-based access control (RBAC), and
 * access control list (ACL).
 * It uses ConfigManager to read and write RBAC and ACL policies from and to
 * JSON files.
 * It also uses IPasswordService to verify user passwords.
 */
public class AuthenticationService implements IAuthenticationService {
    private Map<String, String> commandMap;
    private IPasswordService passwordService;
    private static final Logger logger = Logger.getLogger(AuthenticationService.class.getName());
    public RBACPolicy rbacPolicies;
    public ACLPolicy aclPolicy;
    private String rbacFileParamter = "RBAC";
    private String aclFileParamter = "ACL";

    public AuthenticationService(IPasswordService passwordService) throws IOException {
        this.aclPolicy = ConfigManager.getInstance().readACLJson(aclFileParamter);
        this.rbacPolicies = ConfigManager.getInstance().readRBACJson(rbacFileParamter);
        this.passwordService = passwordService;
        createACLCommandMap();
    }

    public AuthenticationService(IPasswordService passwordService, String setFileParamter) throws IOException {
        this.aclPolicy = ConfigManager.getInstance().readACLJson(setFileParamter);
        this.rbacPolicies = ConfigManager.getInstance().readRBACJson(setFileParamter);
        this.passwordService = passwordService;
        createACLCommandMap();

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

        ConfigManager.getInstance().writeJson(rbacPolicies.getPolicies(), rbacFileParamter);
    }

    public boolean createNewRole(String role, List<String> permissions) throws IOException {
        if (rbacPolicies.getPolicies().containsKey(role)) {
            return false;
        }

        rbacPolicies.getPolicies().put(role, new RBACPolicy.RolePolicy(new ArrayList<>(), permissions));
        ConfigManager.getInstance().writeJson(rbacPolicies.getPolicies(), rbacFileParamter);
        logger.info(String.format("New role '%s' created.", role));
        return true;
    }

    public void deleteRole(String role) throws IOException {
        if (rbacPolicies.getPolicies().containsKey(role)) {
            rbacPolicies.getPolicies().remove(role);
            ConfigManager.getInstance().writeJson(rbacPolicies.getPolicies(), rbacFileParamter);
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
            ConfigManager.getInstance().writeJson(rbacPolicies.getPolicies(), rbacFileParamter);
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
        StringBuilder commands = new StringBuilder();
        commands.append("\nAvailable commands for ").append(user).append(":\n");

        aclPolicy.getPolicies().forEach((command, commandPolicy) -> {
            if (commandPolicy.getMembers().contains(user)) {
                commands.append(commandMap.get(command));
            }
        });

        return commands.toString();
    }

    @Override
    public void addUserToCommand(String user, String operation) throws IOException {
        ACLPolicy.CommandPolicy commandPolicy = aclPolicy.getPolicies().get(operation);
        if (commandPolicy == null) {
            throw new IllegalArgumentException("The command does not exist: " + operation);
        }

        if (!commandPolicy.getMembers().contains(user)) {
            commandPolicy.getMembers().add(user);
            // Assuming ConfigManager has a method to write ACL policies back to the file
            ConfigManager.getInstance().writeJson(aclPolicy.getPolicies(), aclFileParamter);
        }
    }

    @Override
    public void removeUserFromCommand(String user, String operation) throws IOException {
        ACLPolicy.CommandPolicy commandPolicy = aclPolicy.getPolicies().get(operation);
        if (commandPolicy == null) {
            throw new IllegalArgumentException("The command does not exist: " + operation);
        }

        if (commandPolicy.getMembers().remove(user)) {
            // Only write to the file if the user was actually removed
            ConfigManager.getInstance().writeJson(aclPolicy.getPolicies(), aclFileParamter);
        } else {
            throw new IllegalArgumentException("The user does not have access to the command: " + operation);
        }
    }

    private void createACLCommandMap() {
        commandMap = new HashMap<>();
        commandMap.put("start", "start: Start the print server\n");
        commandMap.put("stop", "stop: Stops the print server\n");
        commandMap.put("restart", "restart: Restarts the print server\n");
        commandMap.put("status", "status <printer>: Shows printer status\n");
        commandMap.put("readconfig", "readConfig <parameter>: Reads configuration\n");
        commandMap.put("setconfig", "setConfig <paramter> <value>: Sets configuration\n");
        commandMap.put("print", "print <filename> <printer>: Prints the file\n");
        commandMap.put("queue", "queue <printer>: Shows print queue\n");
        commandMap.put("topqueue", "topQueue <printer> <job>: Moves job to top of queue\n");
        commandMap.put("updatepassword", "updatePassword <username> <password>: Update user password\n");
        commandMap.put("createuser", "createUser <username> <password>: Creates a new user\n");
        commandMap.put("removeuserfromcommand", "removeUserCommand <user> <command>: Removes a command for a user\n");
        commandMap.put("addusertocommand", "addUserCommand: <username> <command>: Adds a command to a user\n");
    }
}
