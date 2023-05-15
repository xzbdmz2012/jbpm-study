package com.example.qj.demo.entity;

import org.kie.api.task.UserGroupCallback;

import java.util.*;

public class MyUserGroupCallback implements UserGroupCallback {

    private Map<String, List<String>> userGroupMap = new HashMap();

    public MyUserGroupCallback() {
        userGroupMap.put("maciej", Arrays.asList("admins"));
        userGroupMap.put("tihomir", Arrays.asList("supplier"));
        userGroupMap.put("kris", Arrays.asList("admins", "managers"));
        userGroupMap.put("Administrator", Arrays.asList("Administrators"));
        userGroupMap.put("salaboy", Arrays.asList("managers", "HR", "IT", "Accounting"));
        userGroupMap.put("katy", Arrays.asList("HR", "IT", "Accounting", "admins"));
        userGroupMap.put("john", Arrays.asList("HR", "Accounting"));
        userGroupMap.put("mary", Arrays.asList("HR"));
    }

    @Override
    public boolean existsUser(String userId) {
        return userGroupMap.containsKey(userId);
    }

    @Override
    public boolean existsGroup(String groupId) {
        Iterator iterator = userGroupMap.keySet().iterator();

        String key;
        do {
            if (!iterator.hasNext()) {
                return false;
            }

            key = (String)iterator.next();
        } while(!((List)userGroupMap.get(key)).contains(groupId));

        return true;
    }

    @Override
    public List<String> getGroupsForUser(String userId) {
        return (List)userGroupMap.get(userId);
    }

    public Map<String, List<String>> getUserGroupMap() {
        return userGroupMap;
    }

    public void setUserGroups(String user, List<String> groups) {
        this.userGroupMap.put(user, groups);
    }


}
