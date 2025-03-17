package loremipsum.dev.taskmanagement.exception;

import loremipsum.dev.taskmanagement.config.Message;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String id) {
        super(Message.USER_NOT_FOUND + id);
    }
}
