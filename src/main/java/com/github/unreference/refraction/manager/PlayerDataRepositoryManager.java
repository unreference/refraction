package com.github.unreference.refraction.manager;

import com.github.unreference.refraction.Refraction;
import com.github.unreference.refraction.data.PlayerData;
import com.github.unreference.refraction.data.repository.PlayerDataRepository;
import com.github.unreference.refraction.model.Rank;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

public class PlayerDataRepositoryManager {
    private static PlayerDataRepositoryManager instance;

    private PlayerDataRepositoryManager() {
    }

    public static PlayerDataRepositoryManager get() {
        if (instance == null) {
            instance = new PlayerDataRepositoryManager();
        }

        return instance;
    }

    public void create() {
        try {
            PlayerDataRepository.get().createTable();
        } catch (SQLException exception) {
            Refraction.log(2, "Failed creating table: %s", exception.getMessage());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
        }
    }

    public boolean isNew(UUID id) {
        try {
            return !PlayerDataRepository.get().exists(id);
        } catch (SQLException exception) {
            Refraction.log(2, "Failed to check if player (uuid=%s) exists: %s", id, exception.getMessage());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
            return false;
        }
    }

    public void register(PlayerData data) {
        try {
            if (isNew(UUID.fromString(data.uuid()))) {
                PlayerDataRepository.get().insert(data);
            } else {
                Refraction.log(1, "Player (name=%s) already exists", data.name());
            }
        } catch (SQLException exception) {
            Refraction.log(2, "Failed to register new player (uuid=%s): %s", data.uuid(), exception.getMessage());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
        }
    }

    public void update(UUID uuid, String name, LocalDateTime lastPlayed) {
        try {
            PlayerDataRepository.get().updateLastPlayed(uuid, name, lastPlayed);
        } catch (SQLException exception) {
            Refraction.log(2, "Failed to update last played (name=%s): %s", name, exception.getMessage());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
        }
    }

    public UUID getId(String name) {
        try {
            return PlayerDataRepository.get().getId(name);
        } catch (SQLException exception) {
            Refraction.log(2, "Failed getting ID (name=%s): %s", name, exception.getMessage());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
            return null;
        }
    }

    public String getName(UUID id) {
        try {
            return PlayerDataRepository.get().getName(id);
        } catch (SQLException exception) {
            Refraction.log(2, "Failed getting name (uuid=%s): %s", id, exception.getMessage());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
            return null;
        }
    }

    public String getRank(UUID id) {
        try {
            return PlayerDataRepository.get().getRank(id);
        } catch (SQLException exception) {
            Refraction.log(2, "Failed getting rank (uuid=%s): %s", id, exception.getMessage());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
            return null;
        }
    }

    public String getRank(String name) {
        try {
            return PlayerDataRepository.get().getRank(name);
        } catch (SQLException exception) {
            Refraction.log(2, "Failed getting rank (name=%s): %s", name, exception.getMessage());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
            return null;
        }
    }

    public void setRank(UUID id, Rank newRank) {
        try {
            PlayerDataRepository.get().setRank(id, newRank);
        } catch (SQLException exception) {
            Refraction.log(2, "Failed setting rank (uuid=%s): %s", id, exception.getMessage());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
        }
    }

    public void setRank(String name, Rank newRank) {
        try {
            PlayerDataRepository.get().setRank(name, newRank);
        } catch (SQLException exception) {
            Refraction.log(2, "Failed setting rank (name=%s): %s", name, exception.getMessage());
            Refraction.log(2, Arrays.toString(exception.getStackTrace()));
        }
    }
}
