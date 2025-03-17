package loremipsum.dev.taskmanagement.concretes;

import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.abstracts.ITaskService;
import loremipsum.dev.taskmanagement.config.Message;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.enums.TaskPriority;
import loremipsum.dev.taskmanagement.enums.TaskStatus;
import loremipsum.dev.taskmanagement.exception.InvalidTaskStateException;
import loremipsum.dev.taskmanagement.exception.TaskNotFoundException;
import loremipsum.dev.taskmanagement.repositories.TaskRepository;
import loremipsum.dev.taskmanagement.utils.TaskStateFlow;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {
    private final TaskRepository taskRepository;

    @PreAuthorize("hasAnyRole('TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @PreAuthorize("hasAnyRole('TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public Task updateTask(UUID taskId, Task task) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId.toString()));

        Task updatedTask = Task.builder()
                .id(existingTask.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .assignee(task.getAssignee())
                .project(task.getProject())
                .acceptanceCriteria(task.getAcceptanceCriteria())
                .cancelReason(task.getCancelReason())
                .blockReason(task.getBlockReason())
                .build();

        return taskRepository.save(updatedTask);
    }

    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public Task getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId.toString()));
    }

    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public void changeTaskStatus(UUID taskId, TaskStatus status, String reason) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));

        if (task.getStatus() == TaskStatus.COMPLETED) {
            throw new InvalidTaskStateException("Cannot change state of a completed task.");
        }

        if (!TaskStateFlow.isValidTransition(task.getStatus(), status)) {
            throw new InvalidTaskStateException("Invalid state transition from " + task.getStatus() + " to " + status);
        }

        if (status == TaskStatus.CANCELLED && (reason == null || reason.isEmpty())) {
            throw new InvalidTaskStateException("Reason must be provided for cancelling a task.");
        }

        task.setStatus(status);
        task.setCancelReason(status == TaskStatus.CANCELLED ? reason : null);
        task.setBlockReason(status == TaskStatus.BLOCKED ? reason : null);
        taskRepository.save(task);
    }

    @PreAuthorize("hasAnyRole('TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public void setTaskPriority(UUID taskId, TaskPriority priority) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId.toString()));

        existingTask.setPriority(priority);
        taskRepository.save(existingTask);
    }
    @PreAuthorize("hasAnyRole('TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public void updateTaskTitleAndDescription(UUID taskId, String title, String description) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId.toString()));

        existingTask.setTitle(title);
        existingTask.setDescription(description);
        taskRepository.save(existingTask);
    }
}