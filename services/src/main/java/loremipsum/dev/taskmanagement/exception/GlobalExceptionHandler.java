package loremipsum.dev.taskmanagement.exception;

import lombok.NoArgsConstructor;
import loremipsum.dev.taskmanagement.config.Result;
import loremipsum.dev.taskmanagement.config.ResultHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@NoArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(RuntimeException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaughtException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "An unexpected error occurred");
        body.put("timestamp", LocalDateTime.now());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<Result> handleProjectNotFoundException(ProjectNotFoundException e) {
        Result result = ResultHelper.notFoundError();
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<Result> handleTaskNotFoundException(TaskNotFoundException e) {
        Result result = ResultHelper.notFoundError();
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Result> handleUserNotFoundException(UserNotFoundException e) {
        Result result = ResultHelper.notFoundError();
        return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(DuplicateRecordException.class)
    public ResponseEntity<Result> handleDuplicateRecordException(DuplicateRecordException e) {
        Result result = ResultHelper.recordAlreadyExistsError(e.getId(), null);
        return new ResponseEntity<>(result, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

}
