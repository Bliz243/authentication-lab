
package app.authentication;

import app.auth.AuthenticationService;
import app.auth.EncryptionService;
import app.auth.PasswordService;
import app.auth.interfaces.IEncryptionService;
import app.auth.interfaces.IPasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {
    private AuthenticationService authenticationService;
    private IEncryptionService encrypt;
    private IPasswordService passwordService;

    @BeforeEach
    public void setUp() throws IOException {
        encrypt = new EncryptionService();
        passwordService = new PasswordService(encrypt);
        authenticationService = new AuthenticationService(passwordService, "testAccessPolicies"); // Uses the loaded
                                                                                                  // test policies
    }

    @Test
    void testSuperAdminPermissions() {
        // Test that Alice, who is a super_admin, has all permissions
        String[] superAdminPermissions = { "start", "stop", "restart", "status", "readconfig", "setconfig", "print",
                "queue", "topqueue" };
        for (String permission : superAdminPermissions) {
            assertTrue(authenticationService.hasPermission("alice", permission),
                    "Super admin should have permission to " + permission);
        }
    }

    @Test
    void testTechnicianPermissions() {
        // Test that Bob, who is a technician, has the correct permissions
        assertTrue(authenticationService.hasPermission("bob", "start"));
        assertTrue(authenticationService.hasPermission("bob", "status"));
        assertFalse(authenticationService.hasPermission("bob", "print"),
                "Technician should not have permission to print");
    }

    @Test
    void testPowerUserPermissions() {
        // Test that Cecilia, who is a powerUser, has the correct permissions
        assertTrue(authenticationService.hasPermission("cecilia", "print"));
        assertTrue(authenticationService.hasPermission("cecilia", "queue"));
        assertFalse(authenticationService.hasPermission("cecilia", "setconfig"),
                "Power user should not have permission to setconfig");
    }

    @Test
    void testUserPermissions() {
        // Test that David, who is a user, has the correct permissions
        assertTrue(authenticationService.hasPermission("david", "print"));
        assertTrue(authenticationService.hasPermission("david", "queue"));
        assertFalse(authenticationService.hasPermission("david", "restart"),
                "User should not have permission to restart");
    }

    @Test
    void testSetUserRole() throws IOException {
        String user = "cecilia";
        String newRole = "user";
        // Set user role to 'user'

        authenticationService.setUserRole(user, newRole);

        // Check that 'newUser' now has 'user' role permissions
        assertTrue(authenticationService.hasPermission(user, "print"), "User should have permission to print");
        assertTrue(authenticationService.hasPermission(user, "queue"), "User should have permission to view the queue");
        assertFalse(authenticationService.hasPermission(user, "restart"), "User should not have permission to restart");

        // Reload the policy to ensure changes are persisted
        assertTrue(authenticationService.hasPermission(user, "print"),
                "User should still have permission to print after reloading policies");
        assertTrue(authenticationService.hasPermission(user, "queue"),
                "User should still have permission to view the queue after reloading policies");
        assertFalse(authenticationService.hasPermission(user, "restart"),
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

    String existingUser = "testnewrole";

    authenticationService.createNewRole(newRole, permissions);
    authenticationService.setUserRole(existingUser, newRole);

    for (String permission : permissions) {
        assertTrue(authenticationService.hasPermission(existingUser, permission),
                    "User should have '" + permission + "' permission after being assigned to the new role.");
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

        assertTrue(authenticationService.hasPermission("alice", newPermission),
                "Super admin should have the newly added permission.");
    }
}
