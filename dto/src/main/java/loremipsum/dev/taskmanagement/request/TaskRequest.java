package loremipsum.dev.taskmanagement.request;


import lombok.Data;
import loremipsum.dev.taskmanagement.enums.TaskPriority;
import loremipsum.dev.taskmanagement.enums.TaskStatus;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.entities.Project;

import java.util.List;
import java.util.UUID;

@Data
public class TaskRequest {
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private UUID assignee;
    private UUID projectId;
    private String acceptanceCriteria;
    private String cancelReason;
    private String blockReason;
    private List<CommentRequest> comments;

    public Task toTask(User assignee, Project project) {
        return Task.builder()
                .title(this.title)
                .description(this.description)
                .status(this.status)
                .priority(this.priority)
                .assignee(assignee)
                .project(project)
                .acceptanceCriteria(this.acceptanceCriteria)
                .cancelReason(this.cancelReason)
                .blockReason(this.blockReason)
                .build();
    }
}