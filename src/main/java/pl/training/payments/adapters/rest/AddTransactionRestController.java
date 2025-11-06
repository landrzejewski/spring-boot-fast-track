package pl.training.payments.adapters.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.training.payments.application.AddTransactionUseCase;
import pl.training.payments.domain.CardNumber;
import pl.training.payments.domain.Money;
import pl.training.payments.domain.TransactionType;

import static pl.training.payments.domain.TransactionType.INFLOW;
import static pl.training.payments.domain.TransactionType.PAYMENT;

@RestController
final class AddCardTransactionRestController {

    private final AddTransactionUseCase addTransactionUseCase;

    AddCardTransactionRestController(final AddTransactionUseCase addTransactionUseCase) {
        this.addTransactionUseCase = addTransactionUseCase;
    }

    @PostMapping("api/cards/{number:\\d{16,19}}/transactions")
    ResponseEntity<Void> addCardTransaction(
            @PathVariable final String number,
            @RequestBody final AddCardTransactionRequest addCardTransactionRequest) {
        var cardNumber = new CardNumber(number);
        var amount = addCardTransactionRequest.money();
        var transactionType = addCardTransactionRequest.transactionType();
        addTransactionUseCase.handle(cardNumber, amount, transactionType);
        return ResponseEntity.noContent().build();
    }

}

record AddCardTransactionRequest(Double amount,
                                 String currencyCode,
                                 String type) {

    Money money() {
        return new Money(amount, currencyCode);
    }

    TransactionType transactionType() {
        return switch (type) {
            case "IN" -> INFLOW;
            case "OUT" -> PAYMENT;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

}
