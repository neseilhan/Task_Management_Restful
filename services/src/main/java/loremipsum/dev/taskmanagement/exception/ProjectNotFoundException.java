package loremipsum.dev.taskmanagement.exception;

import loremipsum.dev.taskmanagement.resultHelper.Message;

public class ProjectNotFoundException extends RuntimeException{
    public ProjectNotFoundException(String id) {
        super(id);
    }
}
