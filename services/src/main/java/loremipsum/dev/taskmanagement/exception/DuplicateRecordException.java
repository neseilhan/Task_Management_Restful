package loremipsum.dev.taskmanagement.exception;

import loremipsum.dev.taskmanagement.config.Message;

public class DuplicateRecordException extends RuntimeException{
    public DuplicateRecordException(String id) {
        super(Message.DUPLICATE_RECORD + id);
    }
}
