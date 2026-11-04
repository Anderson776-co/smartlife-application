package co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import co.com.smartlife.smartlifeapi.crosscutting.exception.SmartLifeException;
import co.com.smartlife.smartlifeapi.shared.infrastructure.input.controller.dto.Response;
import co.com.smartlife.smartlifeapi.shared.service.input.port.MessageResolverInputPort;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    static final String PAYLOAD_TOO_LARGE_CODE = "USER_MESSAGE_PAYLOAD_TOO_LARGE";
    static final String REQUEST_INVALID_CODE = "USER_MESSAGE_REQUEST_INVALID";
    static final String ACCESS_DENIED_CODE = "USER_MESSAGE_ACCESS_DENIED";
    static final String SESSION_REQUIRED_CODE = "USER_MESSAGE_SESSION_REQUIRED";
    static final String UNEXPECTED_ERROR_CODE = "USER_ERROR_TRYING_TO_MAKE_AN_OPERATION";
    private static final String UNEXPECTED_ERROR_DEFAULT =
            "Se ha presentado un problema tratando de realizar la operación. Por favor intente de nuevo.";

    private final MessageResolverInputPort messages;

    public GlobalExceptionHandler(final MessageResolverInputPort messages) {
        this.messages = messages;
    }

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

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Response<Object>> handleUploadTooLarge(final MaxUploadSizeExceededException exception) {
        LOGGER.warn("[REQUEST] Carga por encima del límite: {}", exception.getMessage());
        return failed(HttpStatus.CONTENT_TOO_LARGE, PAYLOAD_TOO_LARGE_CODE);
    }

    @ExceptionHandler({ HttpMessageNotReadableException.class, TypeMismatchException.class })
    public ResponseEntity<Response<Object>> handleUnreadable(final Exception exception) {
        LOGGER.warn("[REQUEST] Cuerpo ilegible: {}", exception.getMessage());
        return failed(HttpStatus.BAD_REQUEST, REQUEST_INVALID_CODE);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response<Object>> handleAccessDenied(final AccessDeniedException exception) {
        LOGGER.warn("[AUTHORIZATION] {}", exception.getMessage());
        return failed(HttpStatus.FORBIDDEN, ACCESS_DENIED_CODE);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Response<Object>> handleAuthentication(final AuthenticationException exception) {
        LOGGER.warn("[AUTHENTICATION] {}", exception.getMessage());
        return failed(HttpStatus.UNAUTHORIZED, SESSION_REQUIRED_CODE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Object>> handleUnexpected(final Exception exception) {
        if (exception instanceof ErrorResponse errorResponse && errorResponse.getStatusCode().is4xxClientError()) {
            LOGGER.warn("[REQUEST] {}", exception.getMessage());
            return failed(errorResponse.getStatusCode(), REQUEST_INVALID_CODE);
        }
        LOGGER.error("[UNEXPECTED] {}", exception.getMessage(), exception);
        return failed(HttpStatus.INTERNAL_SERVER_ERROR, UNEXPECTED_ERROR_CODE);
    }

    private ResponseEntity<Response<Object>> failed(final HttpStatusCode status, final String code) {
        final Response<Object> body = Response.createFailedResponse();
        body.addMessage(messages.resolve(code, UNEXPECTED_ERROR_DEFAULT));
        return ResponseEntity.status(status).body(body);
    }
}
