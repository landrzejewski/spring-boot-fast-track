package pl.training.payments.domain;

import java.util.UUID;

import static java.util.UUID.randomUUID;

public record TransactionId(UUID value) {

    public TransactionId() {
        this(randomUUID());
    }

}
