package loremipsum.dev.taskmanagement.response;

import lombok.Data;
import loremipsum.dev.taskmanagement.entities.Project;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.enums.ProjectStatus;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class ProjectResponse {
    private UUID id;
    private String title;
    private String description;
    private ProjectStatus status;
    private String departmentName;
    private List<UUID> taskIds;
    private Set<UUID> teamMemberIds;
    private boolean deleted;

    public ProjectResponse(Project project) {
        this.id = project.getId();
        this.title = project.getTitle();
        this.description = project.getDescription();
        this.status = project.getStatus();
        this.departmentName = project.getDepartmentName();
        this.taskIds = project.getTasks().stream().map(Task::getId).collect(Collectors.toList());
        this.teamMemberIds = project.getTeamMembers().stream().map(User::getId).collect(Collectors.toSet());
        this.deleted = project.isDeleted();
    }
}