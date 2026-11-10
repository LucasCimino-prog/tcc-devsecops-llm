package tcc.api.secureapi.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/** Respostas de erro padronizadas, sem stack traces, valores rejeitados ou detalhes internos. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> details = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
          .forEach(fe -> details.putIfAbsent(fe.getField(), fe.getDefaultMessage())); // não ecoa o valor rejeitado
        return build(HttpStatus.BAD_REQUEST, "Dados inválidos", details);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiError> handleMalformed(Exception ex) {
        return build(HttpStatus.BAD_REQUEST, "Requisição malformada", null);
    }

    /** Mensagem única para senha errada, usuário inexistente ou conta desabilitada (anti user enumeration). */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(AuthenticationException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Credenciais inválidas", null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "Acesso negado", null);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ApiError> handleConflict(EmailAlreadyUsedException ex) {
        return build(HttpStatus.CONFLICT, "Não foi possível concluir o cadastro com os dados informados", null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Recurso não encontrado", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        if (ex instanceof ErrorResponse er) { // 405, 415, 404 de rota etc.
            HttpStatus status = HttpStatus.valueOf(er.getStatusCode().value());
            return build(status, status.getReasonPhrase(), null);
        }
        log.error("Erro inesperado", ex); // detalhes apenas no log, nunca na resposta
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", null);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, Map<String, String> details) {
        return ResponseEntity.status(status).body(ApiError.of(status, message, details));
    }
}
