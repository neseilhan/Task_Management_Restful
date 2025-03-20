package loremipsum.dev.taskmanagement.utils;

import loremipsum.dev.taskmanagement.enums.TaskStatus;


public class TaskStateFlow {
    public static boolean isValidTransition(TaskStatus currentStatus, TaskStatus status) {
        return switch (currentStatus) {
            case BACKLOG -> status == TaskStatus.IN_ANALYSIS || status == TaskStatus.CANCELLED;
            case IN_ANALYSIS -> status == TaskStatus.IN_PROGRESS || status == TaskStatus.BLOCKED || status == TaskStatus.CANCELLED;
            case IN_PROGRESS -> status == TaskStatus.COMPLETED || status == TaskStatus.BLOCKED || status == TaskStatus.CANCELLED;
            case BLOCKED -> status == TaskStatus.IN_ANALYSIS || status == TaskStatus.IN_PROGRESS || status == TaskStatus.CANCELLED;
            case COMPLETED -> false;
            case CANCELLED -> false;
        };
    }
}
