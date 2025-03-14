package loremipsum.dev.taskmanagement.concretes;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.abstracts.IUserService;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.repositories.TaskRepository;
import loremipsum.dev.taskmanagement.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UUID userId, User user) {
        return null;
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
    //
//    @PreAuthorize("hasAnyRole('TEAM_LEADER', 'PROJECT_MANAGER')")
//    public Task assignTask(UUID taskId, UUID userId) {
//        Task task = taskRepository.findById(taskId)
//                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//        task.setAssignee(user);
//        return taskRepository.save(task);
//    }
//
//    @PreAuthorize("hasAnyRole('TEAM_LEADER', 'PROJECT_MANAGER')")
//    public Task getTaskById(UUID taskId) {
//        return taskRepository.findById(taskId)
//                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
//    }

}
