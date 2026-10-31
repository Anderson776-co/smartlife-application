package co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SmartLifeException.class)
    public ResponseEntity<Response<Object>> handle(final SmartLifeException exception) {
        final HttpStatus status = switch (exception.getType()) {
            case AUTHENTICATION -> HttpStatus.UNAUTHORIZED;
            case AUTHORIZATION -> HttpStatus.FORBIDDEN;
            case BUSINESS -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        LOGGER.warn("[{}] {}", exception.getType(), exception.getTechnicalMessage());
        final Response<Object> body = Response.createFailedResponse();
        body.addMessage(exception.getUserMessage());
        return ResponseEntity.status(status).body(body);
    }
}
