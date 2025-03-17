package loremipsum.dev.taskmanagement.exception;

import loremipsum.dev.taskmanagement.config.Message;

public class ProjectNotFoundException extends RuntimeException{
    public ProjectNotFoundException(String id) {
        super(Message.PROJECT_NOT_FOUND + id);
    }
}
