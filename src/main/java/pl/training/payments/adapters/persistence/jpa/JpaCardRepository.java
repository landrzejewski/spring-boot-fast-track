package pl.training.payments.adapters.persistence.jpa;

import jakarta.persistence.LockModeType;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

public interface JpaCardRepository extends JpaRepository<CardEntity, String>, JpaSpecificationExecutor<CardEntity>, JpaCardRepositoryExtensions /*CrudRepository<CardEntity, String>*/ /*Repository<CardEntity, String>*/ {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value ="5000"))
    // @Query("select c from Card c where c.number = :number")
    Optional<CardEntity> findByNumber(String number);

    // LOAD - All attributes specified in entity graph will be treated as Eager, and all attribute not specified will be treated as Lazy
    // FETCH - All attributes specified in entity graph will be treated as Eager, and all attribute not specified use their default/mapped value
    @EntityGraph(value = "properties")
    // @EntityGraph(value = CardEntity.WITH_PROPERTIES, type = FETCH)
    Page<CardEntity> findByNumber(String number, Pageable pageable);

    @Query("select new pl.training.payments.adapters.persistence.jpa.CardView(c.number, c.expiration) from Card c")
    List<CardView> findAllSummaries();

    @Query("select c.number as number, c.expiration as expiration from Card c")
    Stream<CardProjection> findAllProjections(); // full select to db

    @Async
    @Query("select c from Card c")
    Future<List<CardEntity>> findAllAsync();

    @Transactional
    @Modifying
    @Query("delete Card c")
    void clear();

}
