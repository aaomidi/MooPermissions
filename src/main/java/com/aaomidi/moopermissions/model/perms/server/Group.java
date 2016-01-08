package com.aaomidi.moopermissions.model.perms.server;

import com.aaomidi.moopermissions.engine.registeries.GroupFileRegistry;
import com.aaomidi.moopermissions.utils.StringManager;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.logging.Level;

/**
 * Created by amir on 2015-12-13.
 */
@RequiredArgsConstructor
public class Group {
    @Getter
    private final String name;
    private final LinkedHashMap<String, Boolean> permissions;
    @Getter
    @NonNull
    private int priority;
    @Getter
    @NonNull
    private String displayName;
    @Getter
    @NonNull
    private boolean isDefault;
    @NonNull
    private List<String> parents;

    public List<String> getParents() {
        if (parents == null)
            parents = new ArrayList<>();
        return parents;
    }

    public LinkedHashMap<String, Boolean> getPermissions() {
        return getPermissions(new LinkedList<>());
    }

    protected LinkedHashMap<String, Boolean> getPermissions(List<Group> visitedGroups) {
        LinkedHashMap<String, Boolean> perms = new LinkedHashMap<>();
        for (String parent : getParents()) {
            Group group = GroupFileRegistry.getGroup(parent);
            if (group == null) {
                throw new RuntimeException(parent + " WAS LISTED AS A PARENT GROUP FOR " + getName() + " BUT IT DOES NOT EXIST!");
            }

            if (visitedGroups.contains(group)) {
                StringManager.log(Level.WARNING, "Circular dependency avoided. %s was seen before.", group.getName());
                continue;
            }

            perms.putAll(group.getPermissions(visitedGroups));
        }
        perms.putAll(permissions);
        return perms;
    }

    public void addPermission(String permission, boolean give) {
        permissions.put(permission, give);
    }

    public void addPermissions(Group other) {
        this.displayName = other.getDisplayName();
        this.priority = other.getPriority();
        for (Map.Entry<String, Boolean> entry : other.getPermissions().entrySet()) {
            addPermission(entry.getKey(), entry.getValue());
        }
        this.parents.addAll(other.getParents());
    }
}
