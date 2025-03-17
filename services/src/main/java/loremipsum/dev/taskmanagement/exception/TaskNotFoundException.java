package loremipsum.dev.taskmanagement.exception;

import loremipsum.dev.taskmanagement.config.Message;

public class TaskNotFoundException extends RuntimeException{
    public TaskNotFoundException(String id) {
        super(Message.TASK_NOT_FOUND + id);
    }
}
