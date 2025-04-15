package com.codemaster.switchadmin.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.catalina.util.FilterUtil.getRequestPath;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<ApiError.ValidationError> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> ApiError.ValidationError.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .path(getRequestPath(request))
                .details(validationErrors)
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiError apiError = ApiError.createDefault(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Malformed JSON request",
                getRequestPath(request));

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        List<ApiError.ValidationError> validationErrors = ex.getConstraintViolations().stream()
                .map(violation -> ApiError.ValidationError.builder()
                        .field(getPropertyName(violation))
                        .message(violation.getMessage())
                        .build())
                .collect(Collectors.toList());

        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Constraint violation")
                .path(request.getRequestURI())
                .details(validationErrors)
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(
            BadCredentialsException ex,
            HttpServletRequest request) {

        ApiError apiError = ApiError.createDefault(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Invalid username or password",
                request.getRequestURI());

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {

        ApiError apiError = ApiError.createDefault(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Authentication failed: " + ex.getMessage(),
                request.getRequestURI());

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiError> handleInsufficientAuthenticationException(
            InsufficientAuthenticationException ex,
            HttpServletRequest request) {

        ApiError apiError = ApiError.createDefault(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Full authentication is required to access this resource",
                request.getRequestURI());

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        ApiError apiError = ApiError.createDefault(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "You don't have permission to access this resource",
                request.getRequestURI());

        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String supportedMethods = ex.getSupportedHttpMethods() != null ?
                String.join(", ", ex.getSupportedHttpMethods().stream()
                        .map(HttpMethod::name)
                        .collect(Collectors.toSet())) :
                "unknown";

        ApiError apiError = ApiError.createDefault(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Method Not Allowed",
                "Method " + ex.getMethod() + " is not supported. Supported methods: " + supportedMethods,
                getRequestPath(request));

        return new ResponseEntity<>(apiError, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String supportedTypes = ex.getSupportedMediaTypes().stream()
                .map(MediaType::toString)
                .collect(Collectors.joining(", "));

        ApiError apiError = ApiError.createDefault(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                "Unsupported Media Type",
                "Media type '" + ex.getContentType() + "' is not supported. Supported types: " + supportedTypes,
                getRequestPath(request));

        return new ResponseEntity<>(apiError, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiError apiError = ApiError.createDefault(
                HttpStatus.NOT_ACCEPTABLE.value(),
                "Not Acceptable",
                "Could not find acceptable representation",
                getRequestPath(request));

        return new ResponseEntity<>(apiError, HttpStatus.NOT_ACCEPTABLE);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiError apiError = ApiError.createDefault(
                HttpStatus.BAD_REQUEST.value(),
                "Missing Request Parameter",
                "Required parameter '" + ex.getParameterName() + "' is not present",
                getRequestPath(request));

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiError apiError = ApiError.createDefault(
                HttpStatus.BAD_REQUEST.value(),
                "Missing Request Part",
                "Required part '" + ex.getRequestPartName() + "' is not present",
                getRequestPath(request));

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ApiError apiError = ApiError.createDefault(
                HttpStatus.NOT_FOUND.value(),
                "Resource Not Found",
                "Static resource " + ex.getResourcePath() + " not found",
                getRequestPath(request));

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String requiredType = ex.getRequiredType() != null ?
                ex.getRequiredType().getSimpleName() : "unknown type";

        String parameterName = ex instanceof MethodArgumentTypeMismatchException ?
                ((MethodArgumentTypeMismatchException) ex).getName() : "unknown parameter";

        String errorMessage = String.format(
                "Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(),
                parameterName,
                requiredType);

        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Type Mismatch")
                .message(errorMessage)
                .path(getRequestPath(request))
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return super.handleMissingPathVariable(ex, headers, status, request);
    }


    // ========== JWT SPECIFIC EXCEPTIONS ==========

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiError> handleSignatureException(
            SignatureException ex,
            HttpServletRequest request) {

        ApiError apiError = ApiError.createDefault(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid JWT Signature",
                "JWT signature does not match locally computed signature",
                request.getRequestURI());

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiError> handleExpiredJwtException(
            ExpiredJwtException ex,
            HttpServletRequest request) {

        ApiError apiError = ApiError.createDefault(
                HttpStatus.UNAUTHORIZED.value(),
                "JWT Expired",
                "JWT token has expired",
                request.getRequestURI());

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiError> handleMalformedJwtException(
            MalformedJwtException ex,
            HttpServletRequest request) {

        ApiError apiError = ApiError.createDefault(
                HttpStatus.UNAUTHORIZED.value(),
                "Malformed JWT",
                "JWT token is malformed or corrupted",
                request.getRequestURI());

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ApiError> handleUnsupportedJwtException(
            UnsupportedJwtException ex,
            HttpServletRequest request) {

        ApiError apiError = ApiError.createDefault(
                HttpStatus.UNAUTHORIZED.value(),
                "Unsupported JWT",
                "The specified JWT is not supported",
                request.getRequestURI());

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(io.jsonwebtoken.security.SecurityException.class)
    public ResponseEntity<ApiError> handleJwtSecurityException(
            io.jsonwebtoken.security.SecurityException ex,
            HttpServletRequest request) {

        ApiError apiError = ApiError.createDefault(
                HttpStatus.UNAUTHORIZED.value(),
                "JWT Security Exception",
                "Security exception occurred while processing JWT: " + ex.getMessage(),
                request.getRequestURI());

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        // This often occurs when JWT is null or empty
        if (ex.getMessage() != null &&
                (ex.getMessage().contains("JWT") || ex.getMessage().contains("Jwt"))) {
            ApiError apiError = ApiError.createDefault(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid JWT",
                    "JWT token is invalid: " + ex.getMessage(),
                    request.getRequestURI());

            return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
        }

        // Fallback for other IllegalArgumentException cases
        ApiError apiError = ApiError.createDefault(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Argument",
                ex.getMessage(),
                request.getRequestURI());

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(
            Exception ex,
            HttpServletRequest request) {

        ApiError apiError = ApiError.createDefault(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred: " + ex.getMessage(),
                request.getRequestURI());

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String getRequestPath(WebRequest request) {
        if (request.getDescription(false).startsWith("uri=")) {
            return request.getDescription(false).substring(4);
        }
        return request.getDescription(false);
    }

    private String getPropertyName(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        return propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
    }

}
