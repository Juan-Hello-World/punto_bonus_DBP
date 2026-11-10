package utec.week07.solution;

import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import utec.week07.solution.common.ValidationException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class RestControllerAdviceHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        // ej. "password: must be at least 8 characters; firstName: must start with an uppercase letter"
        var detail = ex.getBindingResult().getFieldErrors().stream()
                       .map(e -> e.getField() + ": " + e.getDefaultMessage())
                       .distinct()
                       .collect(Collectors.joining("; "));
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(403).build();
    }

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidationException(ValidationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) throws ResponseStatusException {
        // force default behavior if anyone throws it
        throw ex;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) throws Exception {
        System.err.println("Unhandled exception " + ex.getMessage());
        ex.printStackTrace();
        return ResponseEntity.internalServerError().build();
    }
}
