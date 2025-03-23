package loremipsum.dev.taskmanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import loremipsum.dev.taskmanagement.abstracts.IProjectService;
import loremipsum.dev.taskmanagement.abstracts.ITaskService;
import loremipsum.dev.taskmanagement.abstracts.IUserService;
import loremipsum.dev.taskmanagement.entities.Project;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.enums.ProjectStatus;
import loremipsum.dev.taskmanagement.exception.GlobalExceptionHandler;
import loremipsum.dev.taskmanagement.request.ProjectRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {

    @Mock
    private ITaskService taskService;

    @Mock
    private IProjectService projectService;

    @Mock
    private IUserService userService;

    @InjectMocks
    private ProjectController projectController;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<UUID> uuidCaptor;

    @Captor
    private ArgumentCaptor<ProjectRequest> projectRequestCaptor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateProject() throws Exception {
        Project project = new Project();
        project.setId(UUID.randomUUID());
        project.setTitle("New Project");
        project.setDescription("A sample project description");
        project.setDepartmentName("IT");

        given(projectService.createProject(any(Project.class))).willReturn(project);

        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setTitle("New Project");
        projectRequest.setDescription("A sample project description");
        projectRequest.setStatus(ProjectStatus.IN_PROGRESS);
        projectRequest.setDepartmentName("IT");

        String projectRequestJson = objectMapper.writeValueAsString(projectRequest);

        mockMvc.perform(post("/projects/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentName").value("IT"))
                .andExpect(jsonPath("$.title").value("New Project"))
                .andExpect(jsonPath("$.description").value("A sample project description"));
    }

    @Test
    void testUpdateProject() throws Exception {
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);

        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setTitle("Updated Project");
        projectRequest.setDescription("Updated Description");
        projectRequest.setDepartmentName("IT");

        when(projectService.updateProject(any(UUID.class), any(Project.class))).thenReturn(project);

        String projectRequestJson = objectMapper.writeValueAsString(projectRequest);

        mockMvc.perform(put("/projects/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(project.getId().toString()));

        verify(projectService).updateProject(any(UUID.class), any(Project.class));
    }

    @Test
    void testGetProjectById() throws Exception {
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);

        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));

        mockMvc.perform(get("/projects/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(project.getId().toString()));

        verify(projectService).getProjectById(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(projectId);
    }

    @Test
    void testGetProjectByIdNotFound() throws Exception {
        UUID projectId = UUID.randomUUID();

        when(projectService.getProjectById(projectId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/projects/{projectId}", projectId))
                .andExpect(status().isNotFound());

        verify(projectService).getProjectById(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(projectId);
    }

    @Test
    void testGetAllProjects() throws Exception {
        Project project = new Project();
        project.setId(UUID.randomUUID());
        List<Project> projects = List.of(project);

        when(projectService.getAllProjects()).thenReturn(projects);

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(project.getId().toString()));

        verify(projectService).getAllProjects();
    }

    @Test
    void testDeleteProject() throws Exception {
        UUID projectId = UUID.randomUUID();
        doNothing().when(projectService).deleteProject(projectId);

        mockMvc.perform(delete("/projects/{projectId}", projectId))
                .andExpect(status().isOk());

        verify(projectService).deleteProject(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(projectId);
    }

    @Test
    void testGetProjectsByDepartment() throws Exception {
        String departmentName = "Engineering";
        Project project = new Project();
        project.setId(UUID.randomUUID());
        List<Project> projects = List.of(project);

        when(projectService.getProjectsByDepartment(departmentName)).thenReturn(projects);

        mockMvc.perform(get("/projects/department/{departmentName}", departmentName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(project.getId().toString()));

        verify(projectService).getProjectsByDepartment(departmentName);
    }

    @Test
    void testGetTasksByProjectId() throws Exception {
        UUID projectId = UUID.randomUUID();
        Project project = new Project();
        project.setId(projectId);

        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setAssignee(new User());
        task.setProject(project);

        List<Task> tasks = List.of(task);

        when(projectService.getTasksByProjectId(projectId)).thenReturn(tasks);

        mockMvc.perform(get("/projects/{projectId}/tasks", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(task.getId().toString()));

        verify(projectService).getTasksByProjectId(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(projectId);
    }

    @Test
    void testGetTeamMembersByProjectId() throws Exception {
        UUID projectId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        Set<User> teamMembers = Set.of(user);

        when(projectService.getTeamMembersByProjectId(projectId)).thenReturn(teamMembers);

        mockMvc.perform(get("/projects/{projectId}/team", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(user.getId().toString()));

        verify(projectService).getTeamMembersByProjectId(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(projectId);
    }

    @Test
    void testGetProjectStatus() throws Exception {
        UUID projectId = UUID.randomUUID();
        ProjectStatus status = ProjectStatus.IN_PROGRESS;

        when(projectService.getStatus(projectId)).thenReturn(status);

        mockMvc.perform(get("/projects/{projectId}/status", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(status.toString()));

        verify(projectService).getStatus(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(projectId);
    }
}