package loremipsum.dev.taskmanagement;

import loremipsum.dev.taskmanagement.concretes.TaskService;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.enums.TaskPriority;
import loremipsum.dev.taskmanagement.enums.TaskStatus;
import loremipsum.dev.taskmanagement.exception.InvalidTaskStateException;
import loremipsum.dev.taskmanagement.exception.TaskNotFoundException;
import loremipsum.dev.taskmanagement.repositories.TaskRepository;
import loremipsum.dev.taskmanagement.utils.TaskStateFlow;
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
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id(UUID.randomUUID())
                .title("Test Task")
                .description("Test Task Description")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.MEDIUM)
                .build();
    }

    @Test
    void testCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task createdTask = taskService.createTask(task);

        assertThat(createdTask).isEqualTo(task);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testUpdateTask_Success() {
        UUID taskId = task.getId();
        Task updatedTask = Task.builder()
                .id(taskId)
                .title("Updated Title")
                .description("Updated Description")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.HIGH)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateTask(taskId, updatedTask);

        assertThat(result.getTitle()).isEqualTo(updatedTask.getTitle());
        assertThat(result.getDescription()).isEqualTo(updatedTask.getDescription());
        assertThat(result.getStatus()).isEqualTo(updatedTask.getStatus());
        assertThat(result.getPriority()).isEqualTo(updatedTask.getPriority());

        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void testUpdateTask_NotFound() {
        UUID taskId = UUID.randomUUID();
        Task updatedTask = Task.builder()
                .id(taskId)
                .title("Updated Title")
                .description("Updated Description")
                .status(TaskStatus.IN_PROGRESS)
                .priority(TaskPriority.HIGH)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(taskId, updatedTask));

        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testGetTaskById_Success() {
        UUID taskId = task.getId();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        Task foundTask = taskService.getTaskById(taskId);

        assertThat(foundTask).isEqualTo(task);

        verify(taskRepository).findById(taskId);
    }

    @Test
    void testGetTaskById_NotFound() {
        UUID taskId = UUID.randomUUID();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(taskId));

        verify(taskRepository).findById(taskId);
    }

    @Test
    void testGetAllTasks() {
        List<Task> tasks = List.of(task);
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(task);

        verify(taskRepository).findAll();
    }

    @Test
    void testChangeTaskStatus_Success() {
        UUID taskId = task.getId();
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;

        try (MockedStatic<TaskStateFlow> mockedTaskStateFlow = mockStatic(TaskStateFlow.class)) {
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            mockedTaskStateFlow.when(() -> TaskStateFlow.isValidTransition(task.getStatus(), newStatus)).thenReturn(true);

            taskService.changeTaskStatus(taskId, newStatus, null);

            assertThat(task.getStatus()).isEqualTo(newStatus);

            verify(taskRepository).findById(taskId);
            verify(taskRepository).save(task);
        }
    }

    @Test
    void testChangeTaskStatus_InvalidTransition() {
        UUID taskId = task.getId();
        TaskStatus newStatus = TaskStatus.COMPLETED;

        try (MockedStatic<TaskStateFlow> mockedTaskStateFlow = mockStatic(TaskStateFlow.class)) {
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            mockedTaskStateFlow.when(() -> TaskStateFlow.isValidTransition(task.getStatus(), newStatus)).thenReturn(false);

            assertThrows(InvalidTaskStateException.class, () -> taskService.changeTaskStatus(taskId, newStatus, null));

            verify(taskRepository).findById(taskId);
            verify(taskRepository, never()).save(task);
        }
    }

    @Test
    void testChangeTaskStatus_CancelledWithoutReason() {
        UUID taskId = task.getId();
        TaskStatus newStatus = TaskStatus.CANCELLED;

        try (MockedStatic<TaskStateFlow> mockedTaskStateFlow = mockStatic(TaskStateFlow.class)) {
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            mockedTaskStateFlow.when(() -> TaskStateFlow.isValidTransition(task.getStatus(), newStatus)).thenReturn(true);

            assertThrows(InvalidTaskStateException.class, () -> taskService.changeTaskStatus(taskId, newStatus, null));

            verify(taskRepository).findById(taskId);
            verify(taskRepository, never()).save(task);
        }
    }
    @Test
    void testChangeTaskStatus_CompletedTask() {
        UUID taskId = task.getId();
        task.setStatus(TaskStatus.COMPLETED);
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThrows(InvalidTaskStateException.class, () -> taskService.changeTaskStatus(taskId, newStatus, null));

        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(task);
    }

    @Test
    void testSetTaskPriority_Success() {
        UUID taskId = task.getId();
        TaskPriority newPriority = TaskPriority.HIGH;

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.setTaskPriority(taskId, newPriority);

        assertThat(task.getPriority()).isEqualTo(newPriority);

        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(task);
    }

    @Test
    void testSetTaskPriority_NotFound() {
        UUID taskId = UUID.randomUUID();
        TaskPriority newPriority = TaskPriority.HIGH;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.setTaskPriority(taskId, newPriority));

        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testUpdateTaskTitleAndDescription_Success() {
        UUID taskId = task.getId();
        String newTitle = "Updated Title";
        String newDescription = "Updated Description";

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.updateTaskTitleAndDescription(taskId, newTitle, newDescription);

        assertThat(task.getTitle()).isEqualTo(newTitle);
        assertThat(task.getDescription()).isEqualTo(newDescription);

        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(task);
    }

    @Test
    void testUpdateTaskTitleAndDescription_NotFound() {
        UUID taskId = UUID.randomUUID();
        String newTitle = "Updated Title";
        String newDescription = "Updated Description";

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTaskTitleAndDescription(taskId, newTitle, newDescription));

        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testChangeTaskStatus_CancelledWithReason() {
        UUID taskId = task.getId();
        TaskStatus newStatus = TaskStatus.CANCELLED;
        String reason = "Task no longer needed";

        try (MockedStatic<TaskStateFlow> mockedTaskStateFlow = mockStatic(TaskStateFlow.class)) {
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            mockedTaskStateFlow.when(() -> TaskStateFlow.isValidTransition(task.getStatus(), newStatus)).thenReturn(true);

            taskService.changeTaskStatus(taskId, newStatus, reason);

            assertThat(task.getStatus()).isEqualTo(newStatus);
            assertThat(task.getCancelReason()).isEqualTo(reason);

            verify(taskRepository).findById(taskId);
            verify(taskRepository).save(task);
        }
    }

    @Test
    void testChangeTaskStatus_BlockedWithReason() {
        UUID taskId = task.getId();
        TaskStatus newStatus = TaskStatus.BLOCKED;
        String reason = "Dependency not met";

        try (MockedStatic<TaskStateFlow> mockedTaskStateFlow = mockStatic(TaskStateFlow.class)) {
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            mockedTaskStateFlow.when(() -> TaskStateFlow.isValidTransition(task.getStatus(), newStatus)).thenReturn(true);

            taskService.changeTaskStatus(taskId, newStatus, reason);

            assertThat(task.getStatus()).isEqualTo(newStatus);
            assertThat(task.getBlockReason()).isEqualTo(reason);

            verify(taskRepository).findById(taskId);
            verify(taskRepository).save(task);
        }
    }

    @Test
    void testChangeTaskStatus_TaskNotFound() {
        UUID taskId = UUID.randomUUID();
        TaskStatus newStatus = TaskStatus.IN_PROGRESS;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.changeTaskStatus(taskId, newStatus, null));

        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }


}