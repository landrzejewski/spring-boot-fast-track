package pl.training.payments.application;

public interface TransactionEventPublisher {

    void publish(TransactionAdded transactionAdded);

}
