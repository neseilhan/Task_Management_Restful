package loremipsum.dev.taskmanagement;

import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.abstracts.ITaskService;
import loremipsum.dev.taskmanagement.resultHelper.Result;
import loremipsum.dev.taskmanagement.resultHelper.ResultHelper;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.enums.TaskPriority;
import loremipsum.dev.taskmanagement.enums.TaskStatus;
import loremipsum.dev.taskmanagement.request.TaskRequest;
import loremipsum.dev.taskmanagement.response.TaskResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final ITaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest taskRequest) {
        Task createdTask = taskService.createTask(taskRequest.toTask());
        return ResponseEntity.ok((new TaskResponse(createdTask)));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable UUID taskId, @RequestBody TaskRequest taskRequest) {
        Task updatedTask = taskService.updateTask(taskId, taskRequest.toTask());
        return ResponseEntity.ok((new TaskResponse(updatedTask)));
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
    public ResponseEntity<Result> changeTaskStatus(@PathVariable UUID taskId, @RequestParam TaskStatus status) {
        taskService.changeTaskStatus(taskId, status, null);
        return ResponseEntity.ok(ResultHelper.success());
    }

    @PatchMapping("/{taskId}/block-or-cancel")
    public ResponseEntity<Result> changeBlockOrCancelReason(@PathVariable UUID taskId, @RequestParam TaskStatus status, @RequestParam String reason) {
        taskService.changeTaskStatus(taskId, status, reason);
        return ResponseEntity.ok(ResultHelper.success());
    }

    @PatchMapping("/{taskId}/priority")
    public ResponseEntity<Result> setTaskPriority(@PathVariable UUID taskId, @RequestParam TaskPriority priority) {
        taskService.setTaskPriority(taskId, priority);
        return ResponseEntity.ok(ResultHelper.success());
    }

    @PatchMapping("/{taskId}/title-description")
    public ResponseEntity<Result> updateTaskTitleAndDescription(@PathVariable UUID taskId, @RequestParam String title, @RequestParam String description) {
        taskService.updateTaskTitleAndDescription(taskId, title, description);
        return ResponseEntity.ok(ResultHelper.success());
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Result> deleteTask(@PathVariable UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok(ResultHelper.success());
    }

}