package pl.training.payments.adapters.persistence.jpa;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@NamedQuery(name = CardEntity.BY_CARD_NUMBER, query = "select c from Card c where c.number = :cardNumber")
@NamedEntityGraph(name = CardEntity.WITH_PROPERTIES, attributeNodes = @NamedAttributeNode("properties"))
@Entity(name = "Card")
public class CardEntity {

    public static final String BY_CARD_NUMBER = "CardEntity.byCardNumber";
    public static final String WITH_PROPERTIES = "CardEntity.withProperties";

    @Id
    private String id;
    @Column(unique = true, length = 20)
    private String number;
    private LocalDate expiration;
    private String currencyCode;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String transactions;
    @JoinColumn(name = "card_id")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CardPropertyEntity> properties;

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

    public List<CardPropertyEntity> getProperties() {
        return properties;
    }

    public void setProperties(List<CardPropertyEntity> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CardEntity that = (CardEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CardEntity{" +
                "id='" + id + '\'' +
                ", number='" + number + '\'' +
                ", expiration=" + expiration +
                ", currencyCode='" + currencyCode + '\'' +
                ", transactions='" + transactions +
                '}';
    }

}
