package me.unreference.refraction.models;

public class RankPermission {
    private final boolean isInheritable;

    public RankPermission(boolean isInheritable) {
        this.isInheritable = isInheritable;
    }

    public boolean isInheritable() {
        return isInheritable;
    }
}
