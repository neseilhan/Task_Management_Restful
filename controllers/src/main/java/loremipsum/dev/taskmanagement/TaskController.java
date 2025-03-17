package loremipsum.dev.taskmanagement;

import lombok.RequiredArgsConstructor;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final ITaskService taskService;
    private final IProjectService projectService;
    private final IUserService userService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest taskRequest) {
        Optional<User> optionalAssignee = userService.getUserById(taskRequest.getAssignee());
        if (!optionalAssignee.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<Project> optionalProject = projectService.getProjectById(taskRequest.getProjectId());
        if (!optionalProject.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Task task = taskRequest.toTask(optionalAssignee.get(), optionalProject.get());
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.ok(new TaskResponse(createdTask));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable UUID taskId, @RequestBody TaskRequest taskRequest) {
        Optional<User> optionalAssignee = userService.getUserById(taskRequest.getAssignee());
        if (!optionalAssignee.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<Project> optionalProject = projectService.getProjectById(taskRequest.getProjectId());
        if (!optionalProject.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Task task = taskRequest.toTask(optionalAssignee.get(), optionalProject.get());
        task.setId(taskId);
        Task updatedTask = taskService.updateTask(taskId, task);
        return ResponseEntity.ok(new TaskResponse(updatedTask));
    }
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID taskId) {
        Task task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(new TaskResponse(task));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        List<TaskResponse> taskResponses = tasks.stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(taskResponses);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<Void> changeTaskStatus(@PathVariable UUID taskId, @RequestParam TaskStatus status, @RequestParam(required = false) String cancelReason) {
        taskService.changeTaskStatus(taskId, status, cancelReason);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/priority")
    public ResponseEntity<Void> setTaskPriority(@PathVariable UUID taskId, @RequestParam TaskPriority priority) {
        taskService.setTaskPriority(taskId, priority);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/title-description")
    public ResponseEntity<Void> updateTaskTitleAndDescription(@PathVariable UUID taskId, @RequestParam String title, @RequestParam String description) {
        taskService.updateTaskTitleAndDescription(taskId, title, description);
        return ResponseEntity.noContent().build();
    }

}