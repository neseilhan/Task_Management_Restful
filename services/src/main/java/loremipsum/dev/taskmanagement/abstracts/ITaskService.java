package loremipsum.dev.taskmanagement.abstracts;

import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.entities.enums.TaskStatus;

import java.util.List;
import java.util.UUID;

public interface ITaskService {
    Task createTask(Task task);
    Task updateTask(UUID taskId, Task task);
    Task getTaskById(UUID taskId);
    List<Task> getAllTasks();
    void changeTaskStatus(UUID taskId, TaskStatus status, String reason);
}
