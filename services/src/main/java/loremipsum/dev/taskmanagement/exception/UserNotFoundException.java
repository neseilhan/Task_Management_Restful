package loremipsum.dev.taskmanagement.exception;

import loremipsum.dev.taskmanagement.resultHelper.Message;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String id) {
        super(id);
    }
}
