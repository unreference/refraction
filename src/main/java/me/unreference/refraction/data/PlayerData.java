package me.unreference.refraction.data;

import java.time.LocalDateTime;

public record PlayerData(String uuid, String name, LocalDateTime firstPlayed, LocalDateTime lastPlayed) {
}
