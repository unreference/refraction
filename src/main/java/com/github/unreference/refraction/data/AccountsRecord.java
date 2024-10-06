package com.github.unreference.refraction.data;

import java.time.LocalDateTime;

public record AccountsRecord(
    String uuid,
    String name,
    int gems,
    int shards,
    LocalDateTime firstPlayed,
    LocalDateTime lastPlayed) {}
