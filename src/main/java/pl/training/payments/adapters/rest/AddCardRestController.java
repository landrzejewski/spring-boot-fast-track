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
final class AddCardRestController {

    private final AddCardUseCase addCardUseCase;

    AddCardRestController(AddCardUseCase addCardUseCase) {
        this.addCardUseCase = addCardUseCase;
    }

    // @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    // @PreAuthorize("!#addCardRequest.currencyCode().equals('EUR')")
    // @PostAuthorize("returnObject.statusCodeValue == 201")
    // @Secured("ROLE_MANAGER")
    // @RolesAllowed("MANAGER")
    @PostMapping("api/cards")
    ResponseEntity<AddCardResponse> addCard(@Validated @RequestBody AddCardRequest addCardRequest) {
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