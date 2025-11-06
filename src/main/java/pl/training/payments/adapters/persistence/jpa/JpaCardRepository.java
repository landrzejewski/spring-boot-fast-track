package pl.training.payments.adapters.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface JpaCardRepository extends JpaRepository<CardEntity, String> { // CrudRepository<CardEntity, String> // Repository<CardEntity, String>

    // @Query("select c from Card c where c.number = :cardNumber")
    Optional<CardEntity> findByNumber(String cardNumber);

}
