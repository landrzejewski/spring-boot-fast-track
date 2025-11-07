package pl.training.payments.adapters.persistence.jpa;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Encja JPA reprezentująca kartę płatniczą w bazie danych relacyjnej.
 * 
 * @Entity - podstawowa adnotacja JPA oznaczająca klasę jako encję mapowaną na tabelę w bazie danych.
 * Parametr name="Card" określa nazwę encji używaną w zapytaniach JPQL/HQL.
 * 
 * @Table - konfiguruje szczegóły tabeli w bazie danych:
 * - indexes - definiuje indeksy bazodanowe dla optymalizacji zapytań
 * 
 * @Index - tworzy indeks na kolumnie "number" o nazwie "card_number".
 * Indeksy przyspieszają wyszukiwanie po numerze karty (SELECT ... WHERE number = ?).
 * Spring/Hibernate automatycznie utworzy ten indeks podczas generowania schematu.
 * 
 * Klasa jest package-private (brak modyfikatora public) - zgodnie z zasadami
 * enkapsulacji w architekturze heksagonalnej, encje JPA są szczegółem implementacyjnym
 * warstwy persistence i nie powinny wyciekać poza pakiet.
 */
@Entity(name = "Card")
@Table(indexes = @Index(name = "card_number", columnList = "number"))
class CardEntity {

    /**
     * @Id - oznacza pole jako klucz główny encji.
     * Brak @GeneratedValue oznacza, że ID jest zarządzane przez aplikację,
     * nie przez bazę danych. W tym przypadku używany jest UUID z domeny.
     */
    @Id
    private String id;
    
    /**
     * @Column - konfiguruje mapowanie kolumny:
     * - unique = true - wartość musi być unikalna w całej tabeli (UNIQUE constraint)
     * - length = 20 - maksymalna długość VARCHAR(20) w bazie danych
     * 
     * Spring/Hibernate automatycznie utworzy constraint UNIQUE podczas generowania DDL.
     */
    @Column(unique = true, length = 20)
    private String number;
    
    /**
     * Pole bez adnotacji - mapowane automatycznie na kolumnę o tej samej nazwie.
     * LocalDate z Java Time API jest automatycznie mapowany na typ DATE w bazie.
     */
    private LocalDate expiration;
    
    private String currencyCode;
    
    /**
     * @Lob - Large Object - dla dużych danych tekstowych lub binarnych.
     * W PostgreSQL mapuje się na typ TEXT, w innych bazach może być CLOB.
     * 
     * @Basic(fetch = FetchType.EAGER) - określa strategię pobierania:
     * - EAGER - dane są pobierane natychmiast razem z encją
     * - LAZY (domyślnie dla @Lob) - dane pobierane dopiero przy pierwszym dostępie
     * 
     * Tutaj EAGER jest używane, bo transakcje są zawsze potrzebne przy pracy z kartą.
     * Przechowywanie transakcji jako JSON w polu tekstowym to przykład
     * denormalizacji dla wydajności - alternatywą byłaby osobna tabela transakcji.
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String transactions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDate getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDate expiration) {
        this.expiration = expiration;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getTransactions() {
        return transactions;
    }

    public void setTransactions(String transactions) {
        this.transactions = transactions;
    }

    /**
     * Implementacja equals() oparta na kluczu głównym (id).
     * 
     * WAŻNE dla JPA/Hibernate:
     * - Encje są porównywane po ID, nie po wszystkich polach
     * - Gwarantuje spójność w kolekcjach Set i mapach HashMap
     * - Hibernate używa equals() do śledzenia zmian w encjach
     * - Porównanie po ID jest bezpieczne nawet dla proxy Hibernate
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        var otherEntity = (CardEntity) other;
        return Objects.equals(id, otherEntity.id);
    }

    /**
     * hashCode() spójny z equals() - używa tylko pola id.
     * 
     * Zasada: jeśli dwa obiekty są equals(), muszą mieć ten sam hashCode.
     * Używanie tylko ID zapewnia stabilność hash nawet gdy inne pola się zmienią.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
