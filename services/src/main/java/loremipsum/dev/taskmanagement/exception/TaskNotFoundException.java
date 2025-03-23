package loremipsum.dev.taskmanagement.exception;

import loremipsum.dev.taskmanagement.resultHelper.Message;

public class TaskNotFoundException extends RuntimeException{
    public TaskNotFoundException(String id) {
        super(id);
    }
}
