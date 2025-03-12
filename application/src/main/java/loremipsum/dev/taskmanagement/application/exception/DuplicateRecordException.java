package loremipsum.dev.taskmanagement.application.exception;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DuplicateRecordException extends RuntimeException {
    private final Long id;
    public DuplicateRecordException(Long id) {
        super("Duplicate record found with id: " +id);
        this.id = id;
    }

}
