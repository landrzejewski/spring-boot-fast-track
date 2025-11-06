package pl.training.payments.adapters.rest;

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
@ControllerAdvice(basePackages = "pl.training.payments.adapters.rest")
final class PaymentsRestExceptionHandler {

    private final RestExceptionResponseBuilder exceptionResponseBuilder;

    PaymentsRestExceptionHandler(final RestExceptionResponseBuilder exceptionResponseBuilder) {
        this.exceptionResponseBuilder = exceptionResponseBuilder;
    }

    @ExceptionHandler(CardNotFoundException.class)
    ResponseEntity<ExceptionResponse> onCardNotFoundException(final CardNotFoundException exception, final Locale locale) {
        return exceptionResponseBuilder.build(exception, NOT_FOUND, locale);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    ResponseEntity<ExceptionResponse> onInsufficientBalanceException(final InsufficientBalanceException exception, final Locale locale) {
        return exceptionResponseBuilder.build(exception, BAD_REQUEST, locale);
    }

}
