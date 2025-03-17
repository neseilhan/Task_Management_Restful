package loremipsum.dev.taskmanagement.concretes;

import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.abstracts.IProjectService;
import loremipsum.dev.taskmanagement.config.Message;
import loremipsum.dev.taskmanagement.entities.Project;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.enums.ProjectStatus;
import loremipsum.dev.taskmanagement.exception.ProjectNotFoundException;
import loremipsum.dev.taskmanagement.repositories.ProjectRepository;
import loremipsum.dev.taskmanagement.repositories.TaskRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService implements IProjectService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    @Override
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    @Override
    public Project updateProject(UUID projectId, Project project) {
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(Message.NOT_FOUND));

        Project updatedProject = Project.builder()
                .id(existingProject.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .status(project.getStatus())
                .departmentName(project.getDepartmentName())
                .tasks(project.getTasks())
                .teamMembers(project.getTeamMembers())
                .build();

        return projectRepository.save(updatedProject);
    }

    @PreAuthorize("hasAnyRole('TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public Optional<Project> getProjectById(UUID projectId) {
        return projectRepository.findById(projectId);
    }

    @PreAuthorize("hasAnyRole('TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    @Override
    public void deleteProject(UUID projectId) {
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId.toString()));

        existingProject.setDeleted(true); // or set status to 'CANCELLED'
        projectRepository.save(existingProject);
    }

    @PreAuthorize("hasAnyRole('TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public List<Project> getProjectsByDepartment(String departmentName) {
        return projectRepository.findByDepartmentName(departmentName);
    }

    @PreAuthorize("hasAnyRole('TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public List<Task> getTasksByProjectId(UUID projectId) {
        return taskRepository.findByProjectId(projectId);
    }
    @PreAuthorize("hasAnyRole('TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public Set<User> getTeamMembersByProjectId(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId.toString()));
        return project.getTeamMembers();
    }

    @PreAuthorize("hasAnyRole('TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public ProjectStatus getStatus(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId.toString()));
        return project.getStatus();
    }
}