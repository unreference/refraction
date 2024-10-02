package com.github.unreference.refraction.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum Rank {
  DEFAULT("Default", null),

  ADMIN("Admin", null, DEFAULT),
  OWNER("Owner", null, ADMIN);

  private final String id;
  private final String prefix;
  private final Rank parent;

  private final Map<String, RankPermission> grantedPermissions;
  private final Set<String> revokedPermissions;

  Rank(String id, String prefix) {
    this.id = id;
    this.prefix = prefix;
    this.parent = null;
    this.grantedPermissions = new HashMap<>();
    this.revokedPermissions = new HashSet<>();
  }

  Rank(String id, String prefix, Rank parent) {
    this.id = id;
    this.prefix = prefix;
    this.parent = parent;
    this.grantedPermissions = new HashMap<>();
    this.revokedPermissions = new HashSet<>();
  }

  public static String getId(Rank rank) {
    return rank.getId();
  }

  public static Rank getRankFromId(String id) {
    for (Rank rank : Rank.values()) {
      if (rank.getId().equalsIgnoreCase(id)) {
        return rank;
      }
    }

    return null;
  }

  public String getId() {
    return id;
  }

  public void grantPermission(String permission, boolean isInheritable) {
    grantedPermissions.put(permission, new RankPermission(isInheritable));
  }

  public void revokePermission(String permission) {
    grantedPermissions.remove(permission);
    revokedPermissions.add(permission);
  }

  public boolean isPermitted(String permission) {
    if (revokedPermissions.contains(permission)) {
      return false; // Explicitly revoked permission
    }

    RankPermission perm = grantedPermissions.get(permission);
    if (perm != null) {
      return true; // Explicitly granted permission
    }

    if (parent != null && parent.isPermitted(permission)) {
      RankPermission parentPermission = parent.grantedPermissions.get(permission);

      if (parentPermission != null && parentPermission.isInheritable()) {
        grantedPermissions.put(permission, parentPermission);
        return true; // Inherits granted permission
      }
    }

    return false;
  }
}
