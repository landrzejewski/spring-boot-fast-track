package pl.training.payments.adapters.persistence.jpa;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

public interface CardProjection {

    String getNumber();

    LocalDate getExpiration();

    @Value("#{target.number + ':' + target.expiration}")
    String getInfo();

}
