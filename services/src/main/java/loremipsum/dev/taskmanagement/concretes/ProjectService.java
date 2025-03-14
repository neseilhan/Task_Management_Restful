package loremipsum.dev.taskmanagement.concretes;

import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.abstracts.IProjectService;
import loremipsum.dev.taskmanagement.entities.Project;
import loremipsum.dev.taskmanagement.enums.ProjectStatus;
import loremipsum.dev.taskmanagement.repositories.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService implements IProjectService {
    private final ProjectRepository projectRepository;

    @Override
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public Project updateProject(UUID projectId, Project project) {
        Optional<Project> existingProjectOptional = projectRepository.findById(projectId);
        if (existingProjectOptional.isPresent()) {
            Project existingProject = existingProjectOptional.get();
            existingProject.setTitle(project.getTitle());
            existingProject.setDescription(project.getDescription());
            existingProject.setStatus(project.getStatus());
            existingProject.setDepartmentName(project.getDepartmentName());
            existingProject.setTasks(project.getTasks());
            existingProject.setTeamMembers(project.getTeamMembers());
            return projectRepository.save(existingProject);
        } else {
            throw new IllegalArgumentException("Project not found");
        }
    }

    @Override
    public Project getProjectById(UUID projectId) {
        return projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public void cancelProject(UUID projectId) {
        Optional<Project> existingProjectOptional = projectRepository.findById(projectId);
        if (existingProjectOptional.isPresent()) {
            Project existingProject = existingProjectOptional.get();
            existingProject.setStatus(ProjectStatus.CANCELLED);
            projectRepository.save(existingProject);
        } else {
            throw new IllegalArgumentException("Project not found");
        }
    }
}
