package br.com.orbitall.channels.canonicals;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionInput(

        @NotNull(message = "Customer ID cannot be null")
        UUID customerId,

        @NotNull(message = "Amount cannot be null")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Card type cannot be null or empty")
        String cardType
) {
}
