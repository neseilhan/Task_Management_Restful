package loremipsum.dev.taskmanagement;
import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.abstracts.IUserService;
import loremipsum.dev.taskmanagement.resultHelper.Result;
import loremipsum.dev.taskmanagement.resultHelper.ResultHelper;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.request.AssignUserRequest;
import loremipsum.dev.taskmanagement.request.UpdateUserRequest;
import loremipsum.dev.taskmanagement.response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(value -> ResponseEntity.ok(new UserResponse(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users == null || users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ResultHelper.success());
    }

    @PostMapping("/assign-to-task")
    public ResponseEntity<Result> assignUserToTask(@RequestBody AssignUserRequest request) {
        userService.assignUserToTask(request.getTaskId(), request.getUserId());
        return ResponseEntity.ok(ResultHelper.success());
    }

    @PostMapping("/assign-to-project")
    public ResponseEntity<Result> assignUserToProject(@RequestBody AssignUserRequest request) {
        userService.assignUserToProject(request.getProjectId(), request.getUserId());
        return ResponseEntity.ok(ResultHelper.success());
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequest request) {
        User updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(new UserResponse(updatedUser));
    }
}