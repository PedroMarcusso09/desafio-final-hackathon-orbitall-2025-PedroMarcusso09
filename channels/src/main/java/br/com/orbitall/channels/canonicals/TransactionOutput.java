package br.com.orbitall.channels.canonicals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionOutput(
        UUID id,
        UUID customerId,
        BigDecimal amount,
        String cardType,
        LocalDateTime createdAt,
        boolean active
) {
}
