package loremipsum.dev.taskmanagement;

import loremipsum.dev.taskmanagement.abstracts.IProjectService;
import loremipsum.dev.taskmanagement.abstracts.IUserService;
import loremipsum.dev.taskmanagement.concretes.TaskService;
import loremipsum.dev.taskmanagement.entities.Project;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.enums.TaskPriority;
import loremipsum.dev.taskmanagement.enums.TaskStatus;
import loremipsum.dev.taskmanagement.exception.InvalidTaskStateException;
import loremipsum.dev.taskmanagement.exception.TaskNotFoundException;
import loremipsum.dev.taskmanagement.repositories.ProjectRepository;
import loremipsum.dev.taskmanagement.repositories.TaskRepository;
import loremipsum.dev.taskmanagement.repositories.UserRepository;
import loremipsum.dev.taskmanagement.config.TaskStateFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    @Mock
    private IProjectService projectService;

    @Mock
    private IUserService userService;

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
        User assignee = new User();
        assignee.setId(UUID.randomUUID());

        Project project = new Project();
        project.setId(UUID.randomUUID());

        task.setAssignee(assignee);
        task.setProject(project);

        when(userService.getUserById(assignee.getId())).thenReturn(Optional.of(assignee));
        when(projectService.getProjectById(project.getId())).thenReturn(Optional.of(project));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task createdTask = taskService.createTask(task);

        assertThat(createdTask).isNotNull();
        assertThat(createdTask.getAssignee()).isEqualTo(assignee);
        assertThat(createdTask.getProject()).isEqualTo(project);

        verify(userService).getUserById(assignee.getId());
        verify(projectService).getProjectById(project.getId());
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
    void testGetAllTasks_Success() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<Task> tasks = taskService.getAllTasks();

        assertEquals(1, tasks.size());
        assertEquals(task.getId(), tasks.get(0).getId());
    }

    @Test
    void testGetAllTasks_NoTasksFound() {
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            taskService.getAllTasks();
        });

        assertEquals("Data not found.", exception.getMessage());
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
    @Test
    void testDeleteTask_Success() {
        UUID taskId = task.getId();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(taskId);

        verify(taskRepository).findById(taskId);
        verify(taskRepository).delete(task);
    }

    @Test
    void testDeleteTask_NotFound() {
        UUID taskId = UUID.randomUUID();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(taskId));

        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).delete(any(Task.class));
    }


}