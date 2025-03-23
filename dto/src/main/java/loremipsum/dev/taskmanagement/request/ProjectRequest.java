package loremipsum.dev.taskmanagement.request;

import lombok.Data;
import loremipsum.dev.taskmanagement.entities.Project;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.enums.ProjectStatus;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class ProjectRequest {
    private String title;
    private String description;
    private String departmentName;
    private ProjectStatus status;
    private List<UUID> teamMembers;

    public Project toProject() {
        return Project.builder()
                .title(this.title)
                .description(this.description)
                .departmentName(this.departmentName)
                .status(this.status != null ? this.status : ProjectStatus.IN_PROGRESS)
                .build();
    }
}
