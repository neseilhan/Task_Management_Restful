package loremipsum.dev.taskmanagement.concretes;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.abstracts.IUserService;
import loremipsum.dev.taskmanagement.config.Message;
import loremipsum.dev.taskmanagement.entities.Project;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.enums.RoleType;
import loremipsum.dev.taskmanagement.exception.ProjectNotFoundException;
import loremipsum.dev.taskmanagement.exception.TaskNotFoundException;
import loremipsum.dev.taskmanagement.exception.UserNotFoundException;
import loremipsum.dev.taskmanagement.repositories.ProjectRepository;
import loremipsum.dev.taskmanagement.repositories.TaskRepository;
import loremipsum.dev.taskmanagement.repositories.UserRepository;
import loremipsum.dev.taskmanagement.request.UpdateUserRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public Optional<User> getUserById(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId.toString());
        }
        return user;
    }


    @Override
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new UserNotFoundException(Message.NOT_FOUND);
        }
        return users;
    }


    @Override
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public void deleteUser(UUID userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));
        existingUser.setDeleted(true);
        userRepository.save(existingUser);
    }

    @Override
    @PreAuthorize("hasAnyRole('TEAM_LEADER', 'PROJECT_MANAGER')")
    public void assignUserToTask(UUID taskId, UUID userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId.toString()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        task.setAssignee(user);
        taskRepository.save(task);
    }

    @Override
    @PreAuthorize("hasAnyRole('TEAM_LEADER', 'PROJECT_MANAGER')")
    public void assignUserToProject(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId.toString()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        project.getTeamMembers().add(user);
        projectRepository.save(project);
    }
    @PreAuthorize("hasRole('PROJECT_MANAGER')")
    public User updateUser(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        User updatedUser = User.builder()
                .id(user.getId())
                .username(Optional.ofNullable(request.getUsername()).orElse(user.getUsername()))
                .email(Optional.ofNullable(request.getEmail()).orElse(user.getEmail()))
                .password(Optional.ofNullable(request.getPassword()).map(passwordEncoder::encode).orElse(user.getPassword()))
                .roles(Optional.ofNullable(request.getRoleType()).map(Collections::singleton).orElse(user.getRoles()))
                .build();

        return userRepository.save(updatedUser);
    }
}
