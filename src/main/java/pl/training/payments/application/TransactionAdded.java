package pl.training.payments.application;

public record TransactionAdded(String cardNumber, String transactionId, String transactionType) {
}
