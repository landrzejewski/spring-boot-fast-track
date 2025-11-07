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

/**
 * Kontroler REST do pobierania listy kart płatniczych ze stronicowaniem.
 * 
 * @RestController - kontroler REST zwracający dane bezpośrednio w ciele odpowiedzi HTTP
 * 
 * Brak @RequestMapping na poziomie klasy oznacza, że każda metoda musi mieć pełną ścieżkę.
 * To celowy wybór projektowy - każdy endpoint jest jawnie zdefiniowany.
 */
@RestController
final class GetCardsRestController {

    private final GetCardsUseCase getCardsUseCase;

    /**
     * Konstruktor z dependency injection.
     * Spring automatycznie wstrzykuje implementację GetCardsUseCase.
     * Brak @Autowired - nie jest potrzebny dla pojedynczego konstruktora od Spring 4.3.
     */
    GetCardsRestController(final GetCardsUseCase getCardsUseCase) {
        this.getCardsUseCase = getCardsUseCase;
    }

    /**
     * Endpoint do pobierania listy kart ze stronicowaniem.
     * GET /api/cards?pageNumber=0&pageSize=10
     * 
     * @GetMapping("api/cards") - mapuje żądania HTTP GET na ścieżkę /api/cards
     * 
     * @RequestParam - wiąże parametry zapytania (query parameters) z parametrami metody:
     * - required = false - parametr opcjonalny (nie zwróci 400 gdy brak)
     * - defaultValue - wartość domyślna gdy parametr nie został podany
     * 
     * Przykładowe wywołania:
     * - GET /api/cards - użyje domyślnych wartości (strona 0, rozmiar 10)
     * - GET /api/cards?pageNumber=2 - strona 2 z domyślnym rozmiarem
     * - GET /api/cards?pageSize=50 - pierwsza strona z 50 elementami
     * - GET /api/cards?pageNumber=1&pageSize=20 - jawne wartości
     * 
     * Spring automatycznie konwertuje String na int. Błędna wartość = 400 Bad Request.
     * 
     * ResultPage<T> to własna implementacja stronicowania.
     * Alternatywą jest Spring Data Page<T>, ale tu używamy czystej domeny.
     */
    @GetMapping("api/cards")
    ResponseEntity<ResultPage<GetCardsResponse>> getCards(
            @RequestParam(required = false, defaultValue = "0") final int pageNumber,
            @RequestParam(required = false, defaultValue = "10") final int pageSize) {
        // Tworzenie specyfikacji stronicowania
        var pageSpec = new PageSpec(pageNumber, pageSize);
        
        // Wywołanie use case i mapowanie wyników na DTO
        // map() na ResultPage mapuje każdy element zachowując metadane stronicowania
        var response = getCardsUseCase.handle(pageSpec).map(GetCardsResponse::from);
        
        // ResponseEntity.ok() = status 200 OK z treścią
        return ResponseEntity.ok(response);
    }

}

record GetCardsResponse(String number, LocalDate expiration, Double balance, String currencyCode) {

    static GetCardsResponse from(Card card) {
        return new GetCardsResponse(card.getNumber().value(), card.getExpiration(), card.getBalance().amount().doubleValue(), card.getCurrency().getCurrencyCode());
    }

}

