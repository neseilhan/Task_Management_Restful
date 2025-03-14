package loremipsum.dev.taskmanagement.concretes;

import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.abstracts.ITaskService;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.enums.TaskStatus;
import loremipsum.dev.taskmanagement.repositories.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService implements ITaskService {
    private final TaskRepository taskRepository;

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(UUID taskId, Task task) {
        Optional<Task> existingTaskOptional = taskRepository.findById(taskId);
        if (existingTaskOptional.isPresent()) {
            Task existingTask = existingTaskOptional.get();
            existingTask.setTitle(task.getTitle());
            existingTask.setDescription(task.getDescription());
            existingTask.setStatus(task.getStatus());
            existingTask.setPriority(task.getPriority());
            existingTask.setAssignee(task.getAssignee());
            existingTask.setProject(task.getProject());
            existingTask.setAcceptanceCriteria(task.getAcceptanceCriteria());
            existingTask.setCancelReason(task.getCancelReason());
            existingTask.setBlockReason(task.getBlockReason());
            return taskRepository.save(existingTask);
        } else {
            throw new IllegalArgumentException("Task not found");
        }
    }

    @Override
    public Task getTaskById(UUID taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public void changeTaskStatus(UUID taskId, TaskStatus status, String reason) {
        Optional<Task> existingTaskOptional = taskRepository.findById(taskId);
        if (existingTaskOptional.isPresent()) {
            Task existingTask = existingTaskOptional.get();
            existingTask.setStatus(status);
            if (status == TaskStatus.CANCELLED) {
                existingTask.setCancelReason(reason);
            } else if (status == TaskStatus.BLOCKED) {
                existingTask.setBlockReason(reason);
            }
            taskRepository.save(existingTask);
        } else {
            throw new IllegalArgumentException("Task not found");
        }
    }
}
