package app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ACLPolicy {
    private Map<String, CommandPolicy> policies;

    public ACLPolicy(Map<String, Object> aclMap) {
        this.policies = new HashMap<>();
        aclMap.forEach((command, users) -> {
            if (users instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> userList = (List<String>) users;
                this.policies.put(command, new CommandPolicy(userList));
            }
        });
    }

    public Map<String, CommandPolicy> getPolicies() {
        return policies;
    }

    protected void setPolicies(Map<String, CommandPolicy> roles) {
        this.policies = roles;
    }

    public static class CommandPolicy {
        private List<String> members;

        public CommandPolicy(List<String> members) {
            this.members = members != null ? members : new ArrayList<>();
        }

        public List<String> getMembers() {
            return members;
        }

        protected void addMember(String user) {
            if (!members.contains(user)) {
                members.add(user);
            }
        }

        protected void removeMember(String user) {
            members.remove(user);
        }
    }
}