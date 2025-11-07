package pl.training.payments.adapters.persistence.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Dokument Spring Data MongoDB reprezentujący kartę płatniczą w bazie NoSQL.
 * 
 * @Document - adnotacja Spring Data MongoDB oznaczająca klasę jako dokument.
 * Parametr collection="Card" określa nazwę kolekcji w MongoDB.
 * 
 * Różnice między MongoDB a JPA:
 * - MongoDB przechowuje dokumenty JSON/BSON zamiast rekordów w tabelach
 * - Brak schematu (schema-less) - elastyczna struktura dokumentów
 * - Brak relacji foreign key - dane często denormalizowane
 * - Transakcje w polu String zamiast osobnej tabeli - naturalne dla NoSQL
 * 
 * Spring Data MongoDB automatycznie:
 * - Mapuje obiekty Java na dokumenty BSON
 * - Konwertuje typy (np. LocalDate na ISODate w MongoDB)
 * - Zarządza połączeniami przez MongoTemplate
 */
@Document(collection = "Card")
class CardDocument {

    /**
     * @Id - oznacza pole jako klucz główny dokumentu (_id w MongoDB).
     * MongoDB automatycznie generuje ObjectId jeśli nie podano wartości.
     * String jest automatycznie konwertowany na/z ObjectId przez Spring Data.
     */
    @Id
    private String id;
    
    /**
     * Pola bez adnotacji są automatycznie mapowane na pola dokumentu.
     * Spring Data MongoDB używa nazw pól Java jako nazw w dokumencie.
     * 
     * W przeciwieństwie do JPA, nie ma @Column - MongoDB jest schemaless.
     * Można użyć @Field("customName") aby zmienić nazwę w dokumencie.
     * 
     * Brak @Indexed - indeksy w MongoDB tworzy się programowo lub przez
     * @Indexed / @CompoundIndex na poziomie klasy.
     */
    private String number;
    private LocalDate expiration;
    private String currencyCode;
    
    /**
     * Transakcje jako JSON String - naturalny sposób w MongoDB.
     * Alternatywy:
     * - Lista subdokumentów: List<TransactionDocument>
     * - DBRef do osobnej kolekcji (rzadko używane)
     * 
     * MongoDB świetnie radzi sobie z dokumentami JSON,
     * więc przechowywanie jako String jest wydajne.
     */
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

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        var otherEntity = (CardDocument) other;
        return Objects.equals(id, otherEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
