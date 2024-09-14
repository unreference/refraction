package me.unreference.refraction.managers;

import me.unreference.refraction.models.RankModel;

public class RankManager {
    private static RankManager instance;

    private RankManager() {
    }

    public static RankManager get() {
        if (instance == null) {
            instance = new RankManager();
        }

        return instance;
    }

    public String getId(RankModel rank) {
        return rank.getId();
    }

    public RankModel getRankFromId(String id) {
        for (RankModel rank : RankModel.values()) {
            if (rank.getId().equalsIgnoreCase(id)) {
                return rank;
            }
        }

        return null;
    }
}
