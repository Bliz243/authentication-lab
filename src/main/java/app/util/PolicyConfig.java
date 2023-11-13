package app.util;

import java.util.List;
import java.util.Map;

public class PolicyConfig {
    private Map<String, UserPolicy> policies;

    public PolicyConfig(Map<String, UserPolicy> policies) {
        this.policies = policies;
    }

    public Map<String, UserPolicy> getPolicies() {
        return policies;
    }

    public static class UserPolicy {
        private List<String> permissions;
        private List<String> members;

        public List<String> getPermissions() {
            return permissions;
        }

        public List<String> getMembers() {
            return members;
        }

    }
}