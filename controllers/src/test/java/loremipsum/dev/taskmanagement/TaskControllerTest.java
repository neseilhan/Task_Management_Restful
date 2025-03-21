package loremipsum.dev.taskmanagement;

import loremipsum.dev.taskmanagement.abstracts.IProjectService;
import loremipsum.dev.taskmanagement.abstracts.ITaskService;
import loremipsum.dev.taskmanagement.abstracts.IUserService;
import loremipsum.dev.taskmanagement.entities.Project;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.enums.TaskPriority;
import loremipsum.dev.taskmanagement.enums.TaskStatus;
import loremipsum.dev.taskmanagement.request.TaskRequest;
import loremipsum.dev.taskmanagement.response.TaskResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    private ITaskService taskService;

    @Mock
    private IProjectService projectService;

    @Mock
    private IUserService userService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<UUID> uuidCaptor;

    @Captor
    private ArgumentCaptor<TaskRequest> taskRequestCaptor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void testCreateTask() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        Project project = new Project();
        project.setId(projectId);

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setAssignee(userId);
        taskRequest.setProjectId(projectId);

        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setAssignee(user);
        task.setProject(project);

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(taskService.createTask(any(Task.class))).thenReturn(task);

        String taskRequestJson = "{"
                + "\"assignee\":\"" + userId.toString() + "\","
                + "\"projectId\":\"" + projectId.toString() + "\""
                + "}";

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId().toString()));

        verify(taskService).createTask(any(Task.class));
    }

    @Test
    void testUpdateTask() throws Exception {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        Project project = new Project();
        project.setId(projectId);

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setAssignee(userId);
        taskRequest.setProjectId(projectId);

        Task task = new Task();
        task.setId(taskId);
        task.setAssignee(user);
        task.setProject(project);

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
        when(taskService.updateTask(any(UUID.class), any(Task.class))).thenReturn(task);

        String taskRequestJson = "{"
                + "\"assignee\":\"" + userId.toString() + "\","
                + "\"projectId\":\"" + projectId.toString() + "\""
                + "}";

        mockMvc.perform(put("/tasks/{taskId}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId().toString()));

        verify(taskService).updateTask(any(UUID.class), any(Task.class));
    }

    @Test
    void testGetTaskById() throws Exception {
        UUID taskId = UUID.randomUUID();
        Task task = new Task();
        task.setId(taskId);

        User user = new User();
        user.setId(UUID.randomUUID());
        task.setAssignee(user);

        when(taskService.getTaskById(taskId)).thenReturn(task);

        mockMvc.perform(get("/tasks/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId().toString()));

        verify(taskService).getTaskById(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(taskId);
    }

    @Test
    void testGetAllTasks() throws Exception {
        Task task = new Task();
        task.setId(UUID.randomUUID());

        User user = new User();
        user.setId(UUID.randomUUID());
        task.setAssignee(user);

        List<Task> tasks = List.of(task);

        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(task.getId().toString()));

        verify(taskService).getAllTasks();
    }

    @Test
    void testChangeTaskStatus() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskStatus status = TaskStatus.IN_PROGRESS;

        doNothing().when(taskService).changeTaskStatus(any(UUID.class), any(TaskStatus.class), any());

        mockMvc.perform(patch("/tasks/{taskId}/status", taskId)
                        .param("status", status.toString()))
                .andExpect(status().isNoContent());

        verify(taskService).changeTaskStatus(uuidCaptor.capture(), any(TaskStatus.class), any());
        assertThat(uuidCaptor.getValue()).isEqualTo(taskId);
    }

    @Test
    void testChangeBlockOrCancelReason() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskStatus status = TaskStatus.CANCELLED;
        String reason = "Reason for cancellation";

        doNothing().when(taskService).changeTaskStatus(any(UUID.class), any(TaskStatus.class), any());

        mockMvc.perform(patch("/tasks/{taskId}/block-or-cancel", taskId)
                        .param("status", status.toString())
                        .param("reason", reason))
                .andExpect(status().isNoContent());

        verify(taskService).changeTaskStatus(uuidCaptor.capture(), any(TaskStatus.class), any());
        assertThat(uuidCaptor.getValue()).isEqualTo(taskId);
    }

    @Test
    void testSetTaskPriority() throws Exception {
        UUID taskId = UUID.randomUUID();
        TaskPriority priority = TaskPriority.HIGH;

        doNothing().when(taskService).setTaskPriority(any(UUID.class), any(TaskPriority.class));

        mockMvc.perform(patch("/tasks/{taskId}/priority", taskId)
                        .param("priority", priority.toString()))
                .andExpect(status().isNoContent());

        verify(taskService).setTaskPriority(uuidCaptor.capture(), any(TaskPriority.class));
        assertThat(uuidCaptor.getValue()).isEqualTo(taskId);
    }

    @Test
    void testUpdateTaskTitleAndDescription() throws Exception {
        UUID taskId = UUID.randomUUID();
        String title = "New Title";
        String description = "New Description";

        doNothing().when(taskService).updateTaskTitleAndDescription(any(UUID.class), anyString(), anyString());

        mockMvc.perform(patch("/tasks/{taskId}/title-description", taskId)
                        .param("title", title)
                        .param("description", description))
                .andExpect(status().isNoContent());

        verify(taskService).updateTaskTitleAndDescription(uuidCaptor.capture(), anyString(), anyString());
        assertThat(uuidCaptor.getValue()).isEqualTo(taskId);
    }
}