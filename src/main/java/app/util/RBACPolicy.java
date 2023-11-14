package app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RBACPolicy {
    private Map<String, RolePolicy> policies;

    public RBACPolicy(Map<String, Object> map) {
        this.policies = new HashMap<>();
        // Check if the map contains the "policies" top-level key
        if (map.containsKey("policies") && map.get("policies") instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> rolesMap = (Map<String, Object>) map.get("policies");
            populateRoles(rolesMap);
        } else {
            // If there's no "policies" key, assume the map is already the roles map
            populateRoles(map);
        }
    }

    private void populateRoles(Map<String, Object> rolesMap) {
        rolesMap.forEach((roleName, roleInfo) -> {
            if (roleInfo instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> roleMap = (Map<String, Object>) roleInfo;
                List<String> members = (List<String>) roleMap.get("members");
                List<String> permissions = (List<String>) roleMap.get("permissions");
                this.policies.put(roleName, new RolePolicy(members, permissions));
            }
        });
    }

    public Map<String, RolePolicy> getPolicies() {
        return policies;
    }

    protected void setPolicies(Map<String, RolePolicy> roles) {
        this.policies = roles;
    }

    public static class RolePolicy {
        private List<String> members;
        private List<String> permissions;

        public RolePolicy(List<String> members, List<String> permissions) {
            this.members = members != null ? members : new ArrayList<>();
            this.permissions = permissions;
        }

        public List<String> getMembers() {
            return members;
        }

        public List<String> getPermissions() {
            return permissions;
        }

        protected void setMembers(List<String> members) {
            this.members = members;
        }

        protected void setPermissions(List<String> permissions) {
            this.permissions = permissions;
        }
    }
}