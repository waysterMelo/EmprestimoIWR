package org.wayster.com.emprestimos.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.wayster.com.emprestimos.UsuarioExistenteException;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UsuarioExistenteException.class)
    public ResponseEntity<?> handleUsuarioExistente(UsuarioExistenteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "erro", "Usuário já cadastrado",
                "mensagem", ex.getMessage(),
                "timestamp", Instant.now()
        ));
    }

}
