package br.com.orbitall.channels.canonicals;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerOutput(
        UUID id,
        String fullName,
        String email,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean active
) {
}
