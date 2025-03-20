package loremipsum.dev.taskmanagement;

import loremipsum.dev.taskmanagement.concretes.ProjectService;
import loremipsum.dev.taskmanagement.entities.Project;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.enums.ProjectStatus;
import loremipsum.dev.taskmanagement.exception.ProjectNotFoundException;
import loremipsum.dev.taskmanagement.repositories.ProjectRepository;
import loremipsum.dev.taskmanagement.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .id(UUID.randomUUID())
                .title("Test Project")
                .description("Test Project Description")
                .status(ProjectStatus.IN_PROGRESS)
                .departmentName("Test Department")
                .tasks(new ArrayList<>())
                .teamMembers(new HashSet<>())
                .build();
    }

    @Test
    void testCreateProject() {
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        Project createdProject = projectService.createProject(project);

        assertThat(createdProject).isEqualTo(project);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testUpdateProject_Success() {
        UUID projectId = project.getId();
        Project updatedProject = Project.builder()
                .id(projectId)
                .title("Updated Title")
                .description("Updated Description")
                .status(ProjectStatus.IN_PROGRESS)
                .departmentName("Updated Department")
                .tasks(new ArrayList<>())
                .teamMembers(new HashSet<>())
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Project result = projectService.updateProject(projectId, updatedProject);

        assertThat(result.getTitle()).isEqualTo(updatedProject.getTitle());
        assertThat(result.getDescription()).isEqualTo(updatedProject.getDescription());
        assertThat(result.getStatus()).isEqualTo(updatedProject.getStatus());
        assertThat(result.getDepartmentName()).isEqualTo(updatedProject.getDepartmentName());

        verify(projectRepository).findById(projectId);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void testUpdateProject_NotFound() {
        UUID projectId = UUID.randomUUID();
        Project updatedProject = Project.builder()
                .id(projectId)
                .title("Updated Title")
                .description("Updated Description")
                .status(ProjectStatus.IN_PROGRESS)
                .departmentName("Updated Department")
                .tasks(new ArrayList<>())
                .teamMembers(new HashSet<>())
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.updateProject(projectId, updatedProject));

        verify(projectRepository).findById(projectId);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void testGetProjectById_Success() {
        UUID projectId = project.getId();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Optional<Project> foundProject = projectService.getProjectById(projectId);

        assertThat(foundProject).isPresent();
        assertThat(foundProject.get()).isEqualTo(project);

        verify(projectRepository).findById(projectId);
    }

    @Test
    void testGetProjectById_NotFound() {
        UUID projectId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        Optional<Project> foundProject = projectService.getProjectById(projectId);

        assertThat(foundProject).isNotPresent();

        verify(projectRepository).findById(projectId);
    }

    @Test
    void testGetAllProjects() {
        List<Project> projects = List.of(project);
        when(projectRepository.findAll()).thenReturn(projects);

        List<Project> result = projectService.getAllProjects();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(project);

        verify(projectRepository).findAll();
    }

    @Test
    void testDeleteProject_Success() {
        UUID projectId = project.getId();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        projectService.deleteProject(projectId);

        assertThat(project.isDeleted()).isTrue();

        verify(projectRepository).findById(projectId);
        verify(projectRepository).save(project);
    }

    @Test
    void testDeleteProject_NotFound() {
        UUID projectId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.deleteProject(projectId));

        verify(projectRepository).findById(projectId);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void testGetProjectsByDepartment() {
        String departmentName = "Test Department";
        List<Project> projects = List.of(project);
        when(projectRepository.findByDepartmentName(departmentName)).thenReturn(projects);

        List<Project> result = projectService.getProjectsByDepartment(departmentName);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(project);

        verify(projectRepository).findByDepartmentName(departmentName);
    }

    @Test
    void testGetTasksByProjectId() {
        UUID projectId = project.getId();
        Task task = new Task();
        List<Task> tasks = List.of(task);
        when(taskRepository.findByProjectId(projectId)).thenReturn(tasks);

        List<Task> result = projectService.getTasksByProjectId(projectId);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(task);

        verify(taskRepository).findByProjectId(projectId);
    }

    @Test
    void testGetTeamMembersByProjectId() {
        UUID projectId = project.getId();
        User user = new User();
        Set<User> teamMembers = Set.of(user);
        project.setTeamMembers(teamMembers);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        Set<User> result = projectService.getTeamMembersByProjectId(projectId);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result).contains(user);

        verify(projectRepository).findById(projectId);
    }

    @Test
    void testGetStatus() {
        UUID projectId = project.getId();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ProjectStatus status = projectService.getStatus(projectId);

        assertThat(status).isEqualTo(ProjectStatus.IN_PROGRESS);

        verify(projectRepository).findById(projectId);
    }
}
