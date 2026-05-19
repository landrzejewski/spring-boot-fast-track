package pl.training.payments.adapters.persistence.jpa;

import jakarta.persistence.LockModeType;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

public interface JpaCardRepository extends JpaRepository<CardEntity, String>, JpaCardRepositoryExtensions /*CrudRepository<CardEntity, String>*/ /*Repository<CardEntity, String>*/ {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value ="5000"))
    // @Query("select c from Card c where c.number = :number")
    Optional<CardEntity> findByNumber(String number);

    // LOAD - All attributes specified in entity graph will be treated as Eager, and all attribute not specified will be treated as Lazy
    // FETCH - All attributes specified in entity graph will be treated as Eager, and all attribute not specified use their default/mapped value
    @EntityGraph(value = "properties")
    // @EntityGraph(value = CardEntity.WITH_PROPERTIES, type = FETCH)
    Page<CardEntity> findByCardNumber(String cardNumber, Pageable pageable);


}
