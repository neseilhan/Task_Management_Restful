package loremipsum.dev.taskmanagement.entities.enums;

public enum TaskStatus {
    BACKLOG("BACKLOG"),
    IN_ANALYSIS("IN_ANALYSIS"),
    IN_PROGRESS("IN_PROGRESS"),
    BLOCKED("BLOCKED"),
    CANCELLED("CANCELLED"),
    COMPLETED("COMPLETED");

    private final String taskStatus;

    TaskStatus(String type) { taskStatus = type;}

    String getType() { return taskStatus;}

}
