package pl.training.payments.adapters.persistence.jpa;

import java.util.List;

public interface JpaCardRepositoryExtensions {

    List<CardEntity> findByCardNumber(String cardNumber);

}
