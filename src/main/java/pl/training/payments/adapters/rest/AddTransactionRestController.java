package pl.training.payments.adapters.rest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.training.common.validation.Range;
import pl.training.common.web.ExceptionResponse;
import pl.training.payments.application.AddTransactionUseCase;
import pl.training.payments.application.CardNotFoundException;
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
            @Validated @RequestBody final AddCardTransactionRequest addCardTransactionRequest) {
        var cardNumber = new CardNumber(number);
        var amount = addCardTransactionRequest.money();
        var transactionType = addCardTransactionRequest.transactionType();
        addTransactionUseCase.handle(cardNumber, amount, transactionType);
        return ResponseEntity.noContent().build();
    }

   /* @ExceptionHandler(CardNotFoundException.class)
    ResponseEntity<ExceptionResponse> onCardNotFoundException(final CardNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse("Card not found"));
    }*/

}

record AddCardTransactionRequest(@Range(minValue = 1, maxValue = 100) Double amount,
                                 @Pattern(regexp = "[A-Z]{3}") String currencyCode,
                                 @NotNull String type) {

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

