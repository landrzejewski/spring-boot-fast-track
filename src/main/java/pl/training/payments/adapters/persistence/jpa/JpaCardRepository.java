package pl.training.payments.adapters.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Interfejs Spring Data JPA do operacji na encjach CardEntity.
 * Spring automatycznie generuje implementację w runtime poprzez proxy.
 * 
 * Hierarchia interfejsów Spring Data (pokazana w komentarzach):
 * 
 * 1. Repository<T,ID> - marker interface, brak metod
 * 2. CrudRepository<T,ID> - podstawowe operacje CRUD:
 *    - save(S entity), saveAll(Iterable<S> entities)
 *    - findById(ID id), existsById(ID id)
 *    - findAll(), findAllById(Iterable<ID> ids)
 *    - count()
 *    - deleteById(ID id), delete(T entity), deleteAll()
 * 
 * 3. JpaRepository<T,ID> - rozszerza CrudRepository o:
 *    - flush() - wymusza synchronizację z bazą danych
 *    - saveAndFlush(S entity) - zapis z natychmiastową synchronizacją
 *    - deleteInBatch(Iterable<T> entities) - usuwanie batch bez kaskadowania
 *    - getOne(ID id) - zwraca lazy-loaded proxy (przestarzałe, użyj getReferenceById)
 *    - Wsparcie dla PagingAndSortingRepository (stronicowanie i sortowanie)
 * 
 * Spring Data automatycznie:
 * - Skanuje interfejsy rozszerzające Repository
 * - Generuje implementacje jako Spring Bean
 * - Parsuje nazwy metod i generuje zapytania SQL
 * - Zarządza transakcjami (@Transactional na poziomie klasy)
 * 
 * Interface jest package-private - enkapsulacja warstwy persystencji.
 */
interface JpaCardRepository extends JpaRepository<CardEntity, String> { // CrudRepository<CardEntity, String> // Repository<CardEntity, String>

    /**
     * Metoda query by method name - Spring Data parsuje nazwę i generuje zapytanie.
     * 
     * Konwencja nazewnictwa:
     * - findBy - rozpoczyna zapytanie SELECT
     * - Number - nazwa pola w encji (pole "number" w CardEntity)
     * 
     * Spring automatycznie generuje JPQL:
     * SELECT c FROM Card c WHERE c.number = ?1
     * 
     * Alternatywnie można użyć @Query (pokazane w komentarzu) dla:
     * - Bardziej złożonych zapytań
     * - Optymalizacji (np. JOIN FETCH)
     * - Natywnych zapytań SQL (@Query(nativeQuery = true))
     * 
     * Optional<T> - wzorzec do obsługi braku wyniku bez null:
     * - Wymusza sprawdzenie czy wartość istnieje
     * - Zapobiega NullPointerException
     * - Oferuje metody jak orElse(), orElseThrow(), map()
     */
    // @Query("select c from Card c where c.number = :cardNumber")
    Optional<CardEntity> findByNumber(String cardNumber);

}
