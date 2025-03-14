package loremipsum.dev.taskmanagement.enums;

public enum ProjectStatus {

    IN_PROGRESS("IN_PROGRESS"),
    CANCELLED("CANCELLED"),
    COMPLETED("COMPLETED");

    private final String projectStatus;

    ProjectStatus (String type) { projectStatus = type;}

    String getType() { return projectStatus;}
}
