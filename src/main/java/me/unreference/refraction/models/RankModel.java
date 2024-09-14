package me.unreference.refraction.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static me.unreference.refraction.Refraction.log;

public enum RankModel {
    PLAYER("Player", null),

    ADMIN("Admin", null, PLAYER),
    OWNER("Owner", null, ADMIN);

    private final String id;
    private final String prefix;
    private final RankModel parent;

    private final Map<String, RankPermissionModel> grantedPermissions;
    private final Set<String> revokedPermissions;

    RankModel(String id, String prefix) {
        this.id = id;
        this.prefix = prefix;
        this.parent = null;
        this.grantedPermissions = new HashMap<>();
        this.revokedPermissions = new HashSet<>();
    }

    RankModel(String id, String prefix, RankModel parent) {
        this.id = id;
        this.prefix = prefix;
        this.parent = parent;
        this.grantedPermissions = new HashMap<>();
        this.revokedPermissions = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void grantPermission(String permission, boolean isInheritable) {
        grantedPermissions.put(permission, new RankPermissionModel(isInheritable));
        log(1, "RankModel", "Granted permission: " + permission);
    }

    public void revokePermission(String permission) {
        grantedPermissions.remove(permission);
        revokedPermissions.add(permission);
    }

    public boolean isPermitted(String permission) {
        if (revokedPermissions.contains(permission)) {
            return false; // Explicitly revoked permission
        }

        RankPermissionModel perm = grantedPermissions.get(permission);
        if (perm != null) {
            return true; // Explicitly granted permission
        }

        if (parent != null && parent.isPermitted(permission)) {
            RankPermissionModel parentPermission = parent.grantedPermissions.get(permission);

            if (parentPermission != null && parentPermission.isInheritable()) {
                grantedPermissions.put(permission, parentPermission);
                return true; // Inherits granted permission
            }
        }

        return false;
    }
}
