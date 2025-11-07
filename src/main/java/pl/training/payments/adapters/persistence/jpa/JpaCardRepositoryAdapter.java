package pl.training.payments.adapters.persistence.jpa;

import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.training.common.PageSpec;
import pl.training.common.ResultPage;
import pl.training.common.component.Adapter;
import pl.training.payments.application.CardRepository;
import pl.training.payments.domain.Card;
import pl.training.payments.domain.CardNumber;

import java.util.Optional;

/**
 * Adapter implementujący port CardRepository dla warstwy persystencji JPA.
 * Kluczowy element architektury heksagonalnej (Ports & Adapters).
 * 
 * @Primary - Spring wybierze tę implementację jako domyślną gdy istnieje
 * wiele beanów typu CardRepository (np. JPA i MongoDB).
 * Mechanizm rozwiązywania konfliktów przy autowiring:
 * 1. @Primary - bean domyślny
 * 2. @Qualifier - wybór po nazwie
 * 3. @Profile - wybór po profilu
 * 
 * @Transactional(propagation = Propagation.MANDATORY) - zarządzanie transakcjami:
 * - MANDATORY - metoda MUSI być wywoływana w istniejącej transakcji
 * - Rzuci wyjątek jeśli brak aktywnej transakcji
 * - Gwarantuje, że operacje na bazie są częścią większej transakcji biznesowej
 * 
 * Inne opcje propagacji:
 * - REQUIRED (domyślna) - używa istniejącej lub tworzy nową
 * - REQUIRES_NEW - zawsze tworzy nową, zawiesza istniejącą
 * - SUPPORTS - używa istniejącej jeśli jest, działa bez jeśli nie ma
 * - NOT_SUPPORTED - zawiesza istniejącą, działa bez transakcji
 * - NEVER - rzuca wyjątek jeśli jest transakcja
 * 
 * @Adapter - customowy stereotyp rozszerzający @Component.
 * Semantycznie oznacza adapter w architekturze heksagonalnej.
 */
@Primary
@Transactional(propagation = Propagation.MANDATORY)
@Adapter
class JpaCardRepositoryAdapter implements CardRepository {

    private final JpaCardRepository cardRepository;
    private final JpaCardRepositoryMapper mapper;

    public JpaCardRepositoryAdapter(final JpaCardRepository cardRepository, final JpaCardRepositoryMapper mapper) {
        this.cardRepository = cardRepository;
        this.mapper = mapper;
    }

    /**
     * Zapisuje kartę w bazie danych.
     * 
     * Przepływ:
     * 1. Mapowanie obiektu domenowego Card na encję JPA CardEntity
     * 2. Zapis encji przez Spring Data repository
     * 3. Mapowanie zapisanej encji z powrotem na obiekt domenowy
     * 
     * Spring Data save() działa jako "upsert":
     * - INSERT gdy encja nie istnieje (nowe ID)
     * - UPDATE gdy encja istnieje (ID już w bazie)
     */
    @Override
    public Card save(final Card card) {
        var cardEntity = mapper.toEntity(card);
        var savedCardEntity = cardRepository.save(cardEntity);
        return mapper.toDomain(savedCardEntity);
    }

    /**
     * Pobiera stronę kart z bazy danych.
     * 
     * Spring Data automatycznie:
     * - Tłumaczy PageRequest na klauzule LIMIT/OFFSET w SQL
     * - Wykonuje dodatkowe zapytanie COUNT dla całkowitej liczby rekordów
     * - Zwraca Page<T> z metadanymi o stronicowaniu
     * 
     * Mapper konwertuje między domenowymi PageSpec/ResultPage
     * a Spring Data PageRequest/Page.
     */
    @Override
    public ResultPage<Card> findAll(PageSpec pageSpec) {
        var pageRequest = mapper.toEntity(pageSpec);
        var cardEntityPage = cardRepository.findAll(pageRequest);
        return mapper.toDomain(cardEntityPage);
    }

    /**
     * Wyszukuje kartę po numerze.
     * 
     * Wykorzystuje:
     * - Value Object pattern - CardNumber enkapsuluje logikę numeru karty
     * - Optional pattern - bezpieczna obsługa braku wyniku
     * - Method reference (mapper::toDomain) - zwięzła składnia dla lambdy
     * 
     * map() na Optional wykonuje transformację tylko gdy wartość istnieje,
     * zwraca Optional.empty() gdy brak wartości.
     */
    @Override
    public Optional<Card> findByNumber(CardNumber cardNumber) {
        var number = mapper.toEntity(cardNumber);
        return cardRepository.findByNumber(number)
                .map(mapper::toDomain);
    }

}
