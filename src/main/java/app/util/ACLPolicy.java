package app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ACLPolicy {
    private Map<String, List<String>> policies;

    public ACLPolicy(Map<String, Object> aclMap) {
        System.out.println(aclMap.size());
        this.policies = new HashMap<>();
        aclMap.forEach((command, users) -> {
            if (users instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> userList = (List<String>) users;
                this.policies.put(command, userList);
            }
        });
    }

    public Map<String, List<String>> getPolicies() {
        return policies;
    }

    protected void setPolicies(Map<String, List<String>> roles) {
        this.policies = roles;
    }
}