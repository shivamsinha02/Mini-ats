package com.ats.web;

import com.ats.service.Exceptions.*;
import java.util.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<Map<String, Object>> body(HttpStatus s, String msg, Object errors) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("status", s.value());
        m.put("message", msg);
        if (errors != null) m.put("errors", errors);
        return ResponseEntity.status(s).body(m);
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<?> notFound(NotFoundException e) { return body(HttpStatus.NOT_FOUND, e.getMessage(), null); }

    @ExceptionHandler({ConflictException.class, DataIntegrityViolationException.class})
    ResponseEntity<?> conflict(Exception e) {
        return body(HttpStatus.CONFLICT, e instanceof ConflictException ? e.getMessage() : "Duplicate or invalid data", null);
    }

    @ExceptionHandler(InvalidTransitionException.class)
    ResponseEntity<?> transition(InvalidTransitionException e) { return body(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage(), null); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> invalid(MethodArgumentNotValidException e) {
        Map<String, String> errs = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> errs.put(f.getField(), f.getDefaultMessage()));
        return body(HttpStatus.BAD_REQUEST, "Validation failed", errs);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    ResponseEntity<?> badRequest(Exception e) { return body(HttpStatus.BAD_REQUEST, "Malformed request", null); }
}
