package loremipsum.dev.taskmanagement.exception;

public class ProjectNotFoundException extends RuntimeException{
    public ProjectNotFoundException(String message) {
        super(message);
    }
}
