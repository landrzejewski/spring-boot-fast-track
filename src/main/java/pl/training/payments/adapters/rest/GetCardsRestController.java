package pl.training.payments.adapters.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.training.common.PageSpec;
import pl.training.common.ResultPage;
import pl.training.payments.application.GetCardsUseCase;
import pl.training.payments.domain.Card;

import java.time.LocalDate;

@RestController
final class GetCardsRestController {

    private final GetCardsUseCase getCardsUseCase;

    GetCardsRestController(final GetCardsUseCase getCardsUseCase) {
        this.getCardsUseCase = getCardsUseCase;
    }

    @GetMapping("api/cards")
    ResponseEntity<ResultPage<GetCardsResponse>> getCards(
            @RequestParam(required = false, defaultValue = "0") final int pageNumber,
            @RequestParam(required = false, defaultValue = "10") final int pageSize) {
        var pageSpec = new PageSpec(pageNumber, pageSize);
        var response = getCardsUseCase.handle(pageSpec).map(GetCardsResponse::from);
        return ResponseEntity.ok(response);
    }

}

record GetCardsResponse(String number, LocalDate expiration, Double balance, String currencyCode) {

    static GetCardsResponse from(Card card) {
        return new GetCardsResponse(card.getNumber().value(), card.getExpiration(), card.getBalance().amount().doubleValue(), card.getCurrency().getCurrencyCode());
    }

}

