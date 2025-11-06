package pl.training.payments.adapters.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.training.common.web.LocationUri;
import pl.training.payments.application.AddCardUseCase;
import pl.training.payments.domain.Card;

import java.time.LocalDate;
import java.util.Currency;

@RestController
public class AddCardRestController {

    private final AddCardUseCase addCardUseCase;

    public AddCardRestController(final AddCardUseCase addCardUseCase) {
        this.addCardUseCase = addCardUseCase;
    }

    @PostMapping("api/cards")
    ResponseEntity<AddCardResponse> addCard(@RequestBody final AddCardRequest addCardRequest) {
        var card = addCardUseCase.handle(addCardRequest.currency());
        var cardNumber = card.getNumber().value();
        var locationUri = LocationUri.fromRequest(cardNumber);
        return ResponseEntity.created(locationUri)
                .body(AddCardResponse.from(card));
    }

}

record AddCardRequest(String currencyCode) {

    Currency currency() {
        return Currency.getInstance(currencyCode);
    }

}

record AddCardResponse(String number, LocalDate expiration) {

    static AddCardResponse from(final Card card) {
        return new AddCardResponse(card.getNumber().value(), card.getExpiration());
    }

}