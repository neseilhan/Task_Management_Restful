package loremipsum.dev.taskmanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import loremipsum.dev.taskmanagement.abstracts.ITaskService;
import loremipsum.dev.taskmanagement.entities.Project;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.enums.TaskPriority;
import loremipsum.dev.taskmanagement.enums.TaskStatus;
import loremipsum.dev.taskmanagement.exception.*;
import loremipsum.dev.taskmanagement.request.TaskRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    @Mock
    private ITaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private Task sampleTask;
    private TaskRequest sampleTaskRequest;
    private UUID sampleTaskId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        sampleTaskId = UUID.randomUUID();
        sampleTask = Task.builder()
                .id(sampleTaskId)
                .title("Sample Task")
                .description("Sample Description")
                .status(TaskStatus.IN_ANALYSIS)
                .priority(TaskPriority.MEDIUM)
                .assignee(User.builder().id(UUID.randomUUID()).build())
                .project(Project.builder().id(UUID.randomUUID()).build())
                .acceptanceCriteria("Sample Acceptance Criteria")
                .cancelReason("Sample Cancel Reason")
                .blockReason("Sample Block Reason")
                .build();

        sampleTaskRequest = TaskRequest.builder()
                .title("Sample Task")
                .description("Sample Description")
                .status(TaskStatus.IN_ANALYSIS)
                .priority(TaskPriority.MEDIUM)
                .assignee(sampleTask.getAssignee().getId())
                .projectId(sampleTask.getProject().getId())
                .acceptanceCriteria("Sample Acceptance Criteria")
                .cancelReason("Sample Cancel Reason")
                .blockReason("Sample Block Reason")
                .build();
    }

    @Test
    void testCreateTask() throws Exception {
        when(taskService.createTask(any(Task.class))).thenReturn(sampleTask);

        String taskRequestJson = objectMapper.writeValueAsString(sampleTaskRequest);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskRequestJson))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();

                    assertTrue(responseBody.contains(sampleTask.getId().toString()));
                    assertTrue(responseBody.contains(sampleTask.getTitle()));
                });
    }

    @Test
    void testUpdateTask() throws Exception {
        when(taskService.updateTask(any(UUID.class), any(Task.class))).thenReturn(sampleTask);

        TaskRequest updateTaskRequest = TaskRequest.builder()
                .title("Updated Task")
                .description("Updated Description")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.HIGH)
                .assignee(sampleTask.getAssignee().getId())
                .projectId(sampleTask.getProject().getId())
                .acceptanceCriteria("Updated Acceptance Criteria")
                .cancelReason("Updated Cancel Reason")
                .blockReason("Updated Block Reason")
                .build();

        String taskRequestJson = objectMapper.writeValueAsString(updateTaskRequest);

        mockMvc.perform(put("/tasks/{taskId}", sampleTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskRequestJson))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();

                    assertTrue(responseBody.contains(sampleTask.getId().toString()));
                });
    }

    @Test
    void testGetTaskById() throws Exception {
        when(taskService.getTaskById(sampleTask.getId())).thenReturn(sampleTask);

        mockMvc.perform(get("/tasks/{taskId}", sampleTask.getId()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();

                    assertTrue(responseBody.contains(sampleTask.getId().toString()));
                });
    }

    @Test
    void testGetAllTasks() throws Exception {
        when(taskService.getAllTasks()).thenReturn(List.of(sampleTask));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();

                    assertTrue(responseBody.contains(sampleTask.getId().toString()));
                });
    }

    @Test
    void testChangeTaskStatus() throws Exception {
        doNothing().when(taskService).changeTaskStatus(sampleTaskId, TaskStatus.IN_PROGRESS, null);

        mockMvc.perform(patch("/tasks/{taskId}/status", sampleTaskId)
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertTrue(responseBody.contains("Process successfully executed."),
                            "Expected response to contain 'Process successfully executed.' but found: " + responseBody);
                });
    }

    @Test
    void testChangeBlockOrCancelReason() throws Exception {
        doNothing().when(taskService).changeTaskStatus(sampleTaskId, TaskStatus.CANCELLED, "Cancelled reason");

        mockMvc.perform(patch("/tasks/{taskId}/block-or-cancel", sampleTaskId)
                        .param("status", "CANCELLED")
                        .param("reason", "Cancelled reason"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertTrue(responseBody.contains("Process successfully executed."),
                            "Expected response to contain 'Process successfully executed.' but found: " + responseBody);
                });
    }

    @Test
    void testSetTaskPriority() throws Exception {
        doNothing().when(taskService).setTaskPriority(sampleTaskId, TaskPriority.HIGH);

        mockMvc.perform(patch("/tasks/{taskId}/priority", sampleTaskId)
                        .param("priority", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();

                    assertTrue(responseBody.contains("Process successfully executed."),
                            "Expected response to contain 'Process successfully executed.' but found: " + responseBody);
                });
    }

    @Test
    void testUpdateTaskTitleAndDescription() throws Exception {
        doNothing().when(taskService).updateTaskTitleAndDescription(sampleTaskId, "New Title", "New Description");

        mockMvc.perform(patch("/tasks/{taskId}/title-description", sampleTaskId)
                        .param("title", "New Title")
                        .param("description", "New Description"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();

                    assertTrue(responseBody.contains("Process successfully executed."),
                            "Expected response to contain 'Process successfully executed.' but found: " + responseBody);
                });
    }

    @Test
    void testDeleteTask() throws Exception {
        doNothing().when(taskService).deleteTask(sampleTaskId);

        mockMvc.perform(delete("/tasks/{taskId}", sampleTaskId))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();

                    assertTrue(responseBody.contains("Process successfully executed."),
                            "Expected response to contain 'Process successfully executed.' but found: " + responseBody);
                });

        verify(taskService, times(1)).deleteTask(sampleTaskId);
    }

    @Test
    void testCreateTaskDuplicate() throws Exception {
        when(taskService.createTask(any(Task.class))).thenThrow(new DuplicateRecordException("Duplicate record"));

        String taskRequestJson = objectMapper.writeValueAsString(sampleTaskRequest);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskRequestJson))
                .andExpect(status().isConflict())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();

                    assertTrue(responseBody.contains("Duplicate records are not allowed."),
                            "Expected response to contain 'Duplicate records are not allowed.' but found: " + responseBody);
                });
    }

    @Test
    void testChangeTaskStatusInvalid() throws Exception {
        doThrow(new InvalidTaskStateException("Invalid status")).when(taskService).changeTaskStatus(sampleTaskId, TaskStatus.IN_PROGRESS, null);

        mockMvc.perform(patch("/tasks/{taskId}/status", sampleTaskId)
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String responseBody = result.getResponse().getContentAsString();
                    assertTrue(responseBody.contains("Task state flow validation error: "),
                            "Expected response to contain 'Invalid status' but found: " + responseBody);
                });
    }
    @Test
    void testDeleteTask_Success() throws Exception {
        doNothing().when(taskService).deleteTask(sampleTaskId);

        mockMvc.perform(delete("/tasks/{taskId}", sampleTaskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Process successfully executed."))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value(true));

        verify(taskService, times(1)).deleteTask(sampleTaskId);
    }

    @Test
    void testDeleteTask_NotFound() throws Exception {
        doThrow(new TaskNotFoundException(sampleTaskId.toString())).when(taskService).deleteTask(sampleTaskId);

        mockMvc.perform(delete("/tasks/{taskId}", sampleTaskId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with ID: " + sampleTaskId))
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.status").value(false));

        verify(taskService, times(1)).deleteTask(sampleTaskId);
    }
}