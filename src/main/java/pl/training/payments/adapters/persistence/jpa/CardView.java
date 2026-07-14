package pl.training.payments.adapters.persistence.jpa;

import java.time.LocalDate;

public record CardView(String number, LocalDate expiration) {
}
