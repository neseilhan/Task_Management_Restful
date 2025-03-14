package loremipsum.dev.taskmanagement.concretes;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.abstracts.IUserService;
import loremipsum.dev.taskmanagement.entities.Project;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.enums.RoleType;
import loremipsum.dev.taskmanagement.exception.UserNotFoundException;
import loremipsum.dev.taskmanagement.repositories.ProjectRepository;
import loremipsum.dev.taskmanagement.repositories.TaskRepository;
import loremipsum.dev.taskmanagement.repositories.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
//    private final PasswordEncoder passwordEncoder;

//    @Override
//    public User createUser(User user) {
//        // Additional logic can be added here like validation or setting defaults
//        return userRepository.save(user);
//    }
//
//    @Override
//    public User updateUser(UUID userId, User updatedUser) {
//        // Fetch existing user
//        User existingUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Update user fields
//        existingUser.setName(updatedUser.getName());
//        existingUser.setEmail(updatedUser.getEmail());
//        existingUser.setRole(updatedUser.getRole());
//
//        // Save and return the updated user
//        return userRepository.save(existingUser);
//    }

    @Override
    public Optional<User> getUserById(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
                throw new UserNotFoundException("User not found with ID: " + userId);
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return null;
        }
        return users;
    }

    @Override
    public void deleteUser(UUID userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        existingUser.setDeleted(true);
        userRepository.save(existingUser);
    }

    @PreAuthorize("hasAnyRole('TEAM_LEADER', 'PROJECT_MANAGER')")
    public void assignUserToTask(UUID taskId, UUID userId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (taskOptional.isPresent() && userOptional.isPresent()) {
            Task task = taskOptional.get();
            User user = userOptional.get();
            task.setAssignee(user);
            taskRepository.save(task);
        } else {
            throw new IllegalArgumentException("Task or User not found");
        }
    }

    @PreAuthorize("hasAnyRole('TEAM_LEADER', 'PROJECT_MANAGER')")
    public void assignUserToProject(UUID projectId, UUID userId) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (projectOptional.isPresent() && userOptional.isPresent()) {
            Project project = projectOptional.get();
            User user = userOptional.get();
            project.getTeamMembers().add(user);
            projectRepository.save(project);
        } else {
            throw new IllegalArgumentException("Project or User not found");
        }
    }

}
