package loremipsum.dev.taskmanagement.exception;

import loremipsum.dev.taskmanagement.resultHelper.Message;

public class DuplicateRecordException extends RuntimeException{
    public DuplicateRecordException(String id) {
        super(id);
    }
}
