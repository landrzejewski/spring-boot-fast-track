package pl.training.common.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.logging.Logger;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice(annotations = RestController.class)
public final class GlobalRestExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(GlobalRestExceptionHandler.class.getName());
    private static final String KEY_VALUE_SEPARATOR = " - ";
    private static final String DELIMITER = ", ";

    private final RestExceptionResponseBuilder responseBuilder;

    public GlobalRestExceptionHandler(final RestExceptionResponseBuilder responseBuilder) {
        this.responseBuilder = responseBuilder;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> onException(final Exception exception, final Locale locale) {
        LOGGER.info("Exception occurred: " + exception);
        return responseBuilder.build(exception, INTERNAL_SERVER_ERROR, locale);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> onMethodArgumentNotValid(final MethodArgumentNotValidException exception, final Locale locale) {
        var description = responseBuilder.getLocalizedMessage(exception, locale, getValidationErrors(exception));
        return responseBuilder.build(description, BAD_REQUEST);
    }

    private String getValidationErrors(final MethodArgumentNotValidException exception) {
        return exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + KEY_VALUE_SEPARATOR + fieldError.getDefaultMessage())
                .collect(joining(DELIMITER));
    }

}
