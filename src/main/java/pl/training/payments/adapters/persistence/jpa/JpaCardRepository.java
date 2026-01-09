package pl.training.payments.adapters.persistence.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JpaCardRepository extends JpaRepository<CardEntity, String> /*CrudRepository<CardEntity, String>*/ /*Repository<CardEntity, String>*/ {

    // @Query("select c from Card c where c.number = :number")
    Optional<CardEntity> findByNumber(String number);

    /*@Query("select c from Card c where c.number = :number and c.owner = ?#{ principal.username }")
    Page<CardEntity> findAllByOwner(Pageable pageable, String username);*/

}
