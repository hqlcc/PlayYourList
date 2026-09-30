package br.com.playyourlist.erro;

import feign.FeignException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {
    public record Erro(int status, String mensagem, List<String> detalhes) {}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Erro> validacao(MethodArgumentNotValidException exception) {
        List<String> detalhes = exception.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage()).sorted().toList();
        return ResponseEntity.badRequest().body(new Erro(400, "Dados inválidos.", detalhes));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Erro> recurso(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(new Erro(exception.getStatusCode().value(), exception.getReason(), List.of()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Erro> formato(Exception exception) {
        return ResponseEntity.badRequest()
                .body(new Erro(400, "JSON inválido ou parâmetro com formato incorreto.", List.of()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Erro> integridade(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new Erro(409, "A operação viola uma regra de integridade do banco.", List.of()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> comunicacao(FeignException exception) {
        // Preserva os erros de validação/404/409 retornados pelos endpoints chamados.
        if (exception.status() >= 400 && exception.status() < 500 && !exception.contentUTF8().isBlank()) {
            return ResponseEntity.status(exception.status()).contentType(MediaType.APPLICATION_JSON)
                    .body(exception.contentUTF8());
        }
        int status = exception.status() < 0 ? 503 : 502;
        return ResponseEntity.status(status)
                .body(new Erro(status, "Não foi possível comunicar com o serviço solicitado.", List.of()));
    }
}
