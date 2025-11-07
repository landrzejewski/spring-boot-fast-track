package pl.training.payments.adapters.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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

    // @Secured("ROLE_MANAGER")
    // @RolesAllowed("MANAGER")
    // @PreAuthorize("!#addCardRequest.currencyCode().equals('EUR')")
    // @PostAuthorize("returnObject.statusCodeValue == 201")
    @PostMapping("api/cards")
    ResponseEntity<AddCardResponse> addCard(@Validated @RequestBody final AddCardRequest addCardRequest) {
        var card = addCardUseCase.handle(addCardRequest.currency());
        var cardNumber = card.getNumber().value();
        var locationUri = LocationUri.fromRequest(cardNumber);
        return ResponseEntity.created(locationUri)
                .body(AddCardResponse.from(card));
    }

}

record AddCardRequest(@Pattern(regexp = "[A-Z]{3}") String currencyCode) {

    Currency currency() {
        return Currency.getInstance(currencyCode);
    }

}

record AddCardResponse(String number, LocalDate expiration) {

    static AddCardResponse from(final Card card) {
        return new AddCardResponse(card.getNumber().value(), card.getExpiration());
    }

}