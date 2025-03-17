package loremipsum.dev.taskmanagement.abstracts;

import loremipsum.dev.taskmanagement.entities.Project;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.enums.ProjectStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IProjectService {
    Project createProject(Project project);
    Project updateProject(UUID projectId, Project project);
    Optional<Project> getProjectById(UUID projectId);
    List<Project> getAllProjects();
    void deleteProject(UUID projectId);
    List<Project> getProjectsByDepartment(String departmentName);
    List<Task> getTasksByProjectId(UUID projectId);
    Set<User> getTeamMembersByProjectId(UUID projectId);
    ProjectStatus getStatus(UUID projectId);
}
