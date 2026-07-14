package pl.training.payments.adapters.rest;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.training.common.web.ExceptionResponse;
import pl.training.common.web.RestExceptionResponseBuilder;
import pl.training.payments.application.CardNotFoundException;
import pl.training.payments.domain.InsufficientBalanceException;

import java.util.Locale;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Order(HIGHEST_PRECEDENCE)
@ControllerAdvice(basePackageClasses = CardsRestExceptionHandler.class)
public class CardsRestExceptionHandler {

    private final RestExceptionResponseBuilder responseBuilder;

    public CardsRestExceptionHandler(RestExceptionResponseBuilder responseBuilder) {
        this.responseBuilder = responseBuilder;
    }

    @ExceptionHandler(CardNotFoundException.class)
    ResponseEntity<ExceptionResponse> onCardNotFound(final CardNotFoundException exception, Locale locale) {
        return responseBuilder.build(exception, NOT_FOUND, locale);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    ResponseEntity<ExceptionResponse> onCardNotFound(final InsufficientBalanceException exception, Locale locale) {
        return responseBuilder.build(exception, BAD_REQUEST, locale);
    }

}
