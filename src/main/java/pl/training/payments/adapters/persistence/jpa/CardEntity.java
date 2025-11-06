package pl.training.payments.adapters.persistence.jpa;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity(name = "Card")
@Table(indexes = @Index(name = "card_number", columnList = "number"))
class CardEntity {

    @Id
    private String id;
    @Column(unique = true, length = 20)
    private String number;
    private LocalDate expiration;
    private String currencyCode;
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

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
