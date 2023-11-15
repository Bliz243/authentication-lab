
package app.authentication;

import app.auth.AuthenticationService;
import app.auth.EncryptionService;
import app.auth.PasswordService;
import app.auth.interfaces.IEncryptionService;
import app.auth.interfaces.IPasswordService;
import app.util.ACLPolicy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {
        private AuthenticationService authenticationService;
        private AuthenticationService aclService;

        private IEncryptionService encrypt;
        private IPasswordService passwordService;

        @BeforeEach
        public void setUp() throws IOException {
                encrypt = new EncryptionService();
                passwordService = new PasswordService(encrypt);
                authenticationService = new AuthenticationService(passwordService, "RBACTest");
                aclService = new AuthenticationService(passwordService, "ACLTest");
        }

        @Test
        void testSuperAdminPermissions() {
                // Test that Alice, who is a super_admin, has all permissions
                String[] superAdminPermissions = { "start", "stop", "restart", "status", "readconfig", "setconfig",
                                "print",
                                "queue", "topqueue" };
                for (String permission : superAdminPermissions) {
                        assertTrue(authenticationService.hasRBACPermission("alice", permission),
                                        "Super admin should have permission to " + permission);
                }
        }

        @Test
        void testTechnicianPermissions() {
                // Test that Bob, who is a technician, has the correct permissions
                assertTrue(authenticationService.hasRBACPermission("bob", "start"));
                assertTrue(authenticationService.hasRBACPermission("bob", "status"));
                assertFalse(authenticationService.hasRBACPermission("bob", "print"),
                                "Technician should not have permission to print");
        }

        @Test
        void testPowerUserPermissions() {
                // Test that Cecilia, who is a powerUser, has the correct permissions
                assertTrue(authenticationService.hasRBACPermission("cecilia", "print"));
                assertTrue(authenticationService.hasRBACPermission("cecilia", "queue"));
                assertFalse(authenticationService.hasRBACPermission("cecilia", "setconfig"),
                                "Power user should not have permission to setconfig");
        }

        @Test
        void testUserPermissions() {
                // Test that David, who is a user, has the correct permissions
                assertTrue(authenticationService.hasRBACPermission("david", "print"));
                assertTrue(authenticationService.hasRBACPermission("david", "queue"));
                assertFalse(authenticationService.hasRBACPermission("david", "restart"),
                                "User should not have permission to restart");
        }

        @Test
        void testSetUserRole() throws IOException {
                String user = "cecilia";
                String newRole = "user";
                // Set user role to 'user'

                authenticationService.setUserRole(user, newRole);

                // Check that 'newUser' now has 'user' role permissions
                assertTrue(authenticationService.hasRBACPermission(user, "print"),
                                "User should have permission to print");
                assertTrue(authenticationService.hasRBACPermission(user, "queue"),
                                "User should have permission to view the queue");
                assertFalse(authenticationService.hasRBACPermission(user, "restart"),
                                "User should not have permission to restart");

                // Reload the policy to ensure changes are persisted
                assertTrue(authenticationService.hasRBACPermission(user, "print"),
                                "User should still have permission to print after reloading policies");
                assertTrue(authenticationService.hasRBACPermission(user, "queue"),
                                "User should still have permission to view the queue after reloading policies");
                assertFalse(authenticationService.hasRBACPermission(user, "restart"),
                                "User should still not have permission to restart after reloading policies");

                authenticationService.setUserRole(user, "powerUser");
        }

        @Test
        void testSetUserRole_RoleDoesNotExist() {
                String user = "newUser";
                String nonExistentRole = "nonExistentRole";
                Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                        authenticationService.setUserRole(user, nonExistentRole);
                });

                String expectedMessage = "The role does not exist: " + nonExistentRole;
                String actualMessage = exception.getMessage();

                assertTrue(actualMessage.contains(expectedMessage));
        }

        @Test
        public void testAuthenticate() {
                assertTrue(authenticationService.authenticate("stefan", "denstore"));
                assertFalse(authenticationService.authenticate("alice", "wrongpassword"));
                assertFalse(authenticationService.authenticate("nonexistentuser", "password123"));
        }

        @Test
        public void testCreateNewRole() throws IOException {
                String newRole = "newRole";
                List<String> permissions = Arrays.asList("newpermission1", "newpermission2");

                String existingUser = "testuser";

                authenticationService.createNewRole(newRole, permissions);
                authenticationService.setUserRole(existingUser, newRole);

                for (String permission : permissions) {
                        assertTrue(authenticationService.hasRBACPermission(existingUser, permission),
                                        "User should have '" + permission
                                                        + "' permission after being assigned to the new role.");
                }
        }

        @Test
        public void testDeleteRole() throws IOException {
                String roleToDelete = "deletableRole";
                // Assume roleToDelete exists
                authenticationService.createNewRole(roleToDelete, List.of("perm1", "perm2"));

                authenticationService.deleteRole(roleToDelete);

                assertFalse(authenticationService.getListOfRoles().contains(roleToDelete),
                                "Role should not exist after deletion.");
        }

        @Test
        public void testAddPermissionToRole() throws IOException {
                String role = "super_admin";
                String newPermission = "new_permission";

                authenticationService.addPermissionToRole(role, newPermission);

                assertTrue(authenticationService.hasRBACPermission("alice", newPermission),
                                "Super admin should have the newly added permission.");
        }

        @Test
        void testHasACLPermission() throws IOException {
                assertTrue(aclService.hasACLPermission("alice", "start"),
                                "Alice should have permission to start");

                assertFalse(aclService.hasACLPermission("bob", "stop"),
                                "Bob should not have permission to stop");

                assertTrue(aclService.hasACLPermission("cecilia", "restart"),
                                "Cecilia should have permission to restart");

                assertFalse(aclService.hasACLPermission("david", "start"),
                                "David should not have permission to start");
                assertFalse(aclService.hasACLPermission("david", "stop"),
                                "David should not have permission to stop");
                assertFalse(aclService.hasACLPermission("david", "restart"),
                                "David should not have permission to restart");
        }

        @Test
        void testAddAndRemoveUserToCommand() throws IOException {
                String user = "testuser";
                String operation = "start";
                assertFalse(aclService.hasACLPermission(user, operation),
                                "Alice should not have permission to start before adding permission");

                aclService.addUserToCommand(user, operation);

                assertTrue(aclService.hasACLPermission(user, operation),
                                "Alice should have permission to start after adding permission");

                aclService.removeUserFromCommand(user, operation);
                assertFalse(aclService.hasACLPermission(user, operation),
                                "Alice should not have permission to start before adding permission");
        }

        @Test
        void testGetRBACAvailableCommands() {
                String user = "jan";
                String expectedCommands = "\nAvailable commands:\n" +
                                "print <filename> <printer>: Prints the file.\n" +
                                "queue <printer>: Shows print queue. \n" +
                                "topQueue <printer> <job>: Moves job to top of queue.\n" +
                                "restart: Restarts the print server.\n" +
                                "logout: Logs current user out\n";
                assertEquals(expectedCommands, authenticationService.getRBACAvailableCommands(user),
                                "RBAC available commands for Jan should match expected commands");
        }

        @Test
        void testGetACLAvailableCommands() {
                String user = "testuser";
                String expectedCommands = "\nAvailable commands for testuser:\n" +
                                "print\n";
                assertEquals(expectedCommands, aclService.getACLAvailableCommands(user),
                                "ACL available commands for testuser should match expected commands");
        }
}
