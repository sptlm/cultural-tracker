package com.culturalnavigator.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.ModelAndView;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public Object handleNotFound(NotFoundException ex, HttpServletRequest request) {
        log.warn("Not found at {}: {}", request.getRequestURI(), ex.getMessage());
        if (!wantsJson(request)) {
            return errorPage("error/404", HttpStatus.NOT_FOUND, ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("error", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(AccessDeniedAppException.class)
    public Object handleAccessDenied(AccessDeniedAppException ex, HttpServletRequest request) {
        log.warn("Access denied at {}: {}", request.getRequestURI(), ex.getMessage());
        if (!wantsJson(request)) {
            return errorPage("error/403", HttpStatus.FORBIDDEN, ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiErrorResponse("error", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResource(NoResourceFoundException ex, HttpServletRequest request) {
        if (!wantsJson(request)) {
            return errorPage("error/404", HttpStatus.NOT_FOUND, "Страница не найдена");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse("error", "Ресурс не найден", Instant.now()));
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse("error", message, Instant.now()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Object handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Bad request at {}: {}", request.getRequestURI(), ex.getMessage());
        if (!wantsJson(request)) {
            return errorPage("error/400", HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        return ResponseEntity.badRequest()
                .body(new ApiErrorResponse("error", ex.getMessage(), Instant.now()));
    }

    @ExceptionHandler(Exception.class)
    public Object handleAny(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}", request.getRequestURI(), ex);
        if (!wantsJson(request)) {
            return errorPage("error/500", HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse("error", "Внутренняя ошибка сервера", Instant.now()));
    }

    private boolean wantsJson(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        String accept = request.getHeader("Accept");
        return request.getServletPath().startsWith("/api/")
                || "XMLHttpRequest".equals(requestedWith)
                || (accept != null && accept.contains("application/json"));
    }

    private ModelAndView errorPage(String view, HttpStatus status, String message) {
        ModelAndView modelAndView = new ModelAndView(view);
        modelAndView.setStatus(status);
        modelAndView.addObject("message", message);
        return modelAndView;
    }
}
