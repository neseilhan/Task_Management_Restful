package loremipsum.dev.taskmanagement.abstracts;

import loremipsum.dev.taskmanagement.entities.Project;

import java.util.List;
import java.util.UUID;

public interface IProjectService {
    Project createProject(Project project);
    Project updateProject(UUID projectId, Project project);
    Project getProjectById(UUID projectId);
    List<Project> getAllProjects();
    void cancelProject(UUID projectId);
}
