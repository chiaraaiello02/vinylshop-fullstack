package it.chiara.vinylshop.exceptions;

import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDate;

@ControllerAdvice
@RestController
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    // Gestione eccezione NotFoundException
    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ErrorResponse> exceptionNotFoundHandler(Exception ex) {
        ErrorResponse errore = new ErrorResponse();
        errore.setDate(LocalDate.now());
        errore.setCode(HttpStatus.NOT_FOUND.value());
        errore.setMessage(ex.getMessage());
        return new ResponseEntity<>(errore, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    // Gestione eccezione DuplicateException
    @ExceptionHandler(DuplicateException.class)
    public final ResponseEntity<ErrorResponse> exceptionDuplicateRecordHandler(Exception ex) {
        ErrorResponse errore = new ErrorResponse();
        errore.setDate(LocalDate.now());
        errore.setCode(HttpStatus.NOT_ACCEPTABLE.value());
        errore.setMessage(ex.getMessage());
        return new ResponseEntity<>(errore, new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE);
    }

    // Gestione eccezione BindingException
    @ExceptionHandler(BindingException.class)
    public ResponseEntity<ErrorResponse> exceptionBindingHandler(Exception ex) {
        ErrorResponse errore = new ErrorResponse();
        errore.setDate(LocalDate.now());
        errore.setCode(HttpStatus.BAD_REQUEST.value());
        errore.setMessage(ex.getMessage());
        return new ResponseEntity<>(errore, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // (Opzionale) Gestione OptimisticLockException (come nel tuo GlobalExceptionHandler)
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockException(OptimisticLockException ex) {
        ErrorResponse errore = new ErrorResponse();
        errore.setDate(LocalDate.now());
        errore.setCode(HttpStatus.CONFLICT.value());
        errore.setMessage("Conflitto durante l'elaborazione della richiesta. Riprova.");
        return new ResponseEntity<>(errore, new HttpHeaders(), HttpStatus.CONFLICT);
    }
}