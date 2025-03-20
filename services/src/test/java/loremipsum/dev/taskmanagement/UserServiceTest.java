package loremipsum.dev.taskmanagement;

import loremipsum.dev.taskmanagement.concretes.UserService;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .username("testUser")
                .email("test@example.com")
                .password("password")
                .roles(Collections.singleton(RoleType.TEAM_MEMBER))
                .build();
    }
    @Test
    void testGetAllUsers_Success() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(user);

        verify(userRepository).findAll();
    }

    @Test
    void testGetAllUsers_UserNotFound() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(UserNotFoundException.class, () -> userService.getAllUsers());

        verify(userRepository).findAll();
    }

    @Test
    void testUpdateUser() {
        UUID userId = user.getId();
        UpdateUserRequest request = new UpdateUserRequest("newUsername", "newEmail@example.com", "newPassword", RoleType.PROJECT_MANAGER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0)); // ilk argümanı döndür (User nesnesi)

        User updatedUser = userService.updateUser(userId, request);

        assertThat(updatedUser.getUsername()).isEqualTo(request.getUsername());
        assertThat(updatedUser.getEmail()).isEqualTo(request.getEmail());
        assertThat(updatedUser.getPassword()).isEqualTo("encodedPassword");
        assertThat(updatedUser.getRoles()).contains(request.getRoleType());

        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        UUID userId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest("newUsername", "newEmail@example.com", "newPassword", RoleType.PROJECT_MANAGER);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, request));

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserById_UserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void testGetUserById_Success() {
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> foundUser = userService.getUserById(userId);

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(user);

        verify(userRepository).findById(userId);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testDeleteUser_Success() {
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        assertThat(user.isDeleted()).isTrue();

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

    @Test
    void testAssignUserToTask_TaskNotFound() {
        UUID taskId = UUID.randomUUID();
        UUID userId = user.getId();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> userService.assignUserToTask(taskId, userId));

        verify(taskRepository).findById(taskId);
        verify(userRepository, never()).findById(userId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testAssignUserToTask_UserNotFound() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Task task = new Task();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.assignUserToTask(taskId, userId));

        verify(taskRepository).findById(taskId);
        verify(userRepository).findById(userId);
        verify(taskRepository, never()).save(task);
    }

    @Test
    void testAssignUserToTask_Success() {
        UUID taskId = UUID.randomUUID();
        UUID userId = user.getId();
        Task task = new Task();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.assignUserToTask(taskId, userId);

        assertThat(task.getAssignee()).isEqualTo(user);

        verify(taskRepository).findById(taskId);
        verify(userRepository).findById(userId);
        verify(taskRepository).save(task);
    }

    @Test
    void testAssignUserToProject_ProjectNotFound() {
        UUID projectId = UUID.randomUUID();
        UUID userId = user.getId();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> userService.assignUserToProject(projectId, userId));

        verify(projectRepository).findById(projectId);
        verify(userRepository, never()).findById(userId);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void testAssignUserToProject_UserNotFound() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Project project = new Project();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.assignUserToProject(projectId, userId));

        verify(projectRepository).findById(projectId);
        verify(userRepository).findById(userId);
        verify(projectRepository, never()).save(project);
    }

    @Test
    void testAssignUserToProject_Success() {
        UUID projectId = UUID.randomUUID();
        UUID userId = user.getId();
        Project project = new Project();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.assignUserToProject(projectId, userId);

        assertThat(project.getTeamMembers()).contains(user);

        verify(projectRepository).findById(projectId);
        verify(userRepository).findById(userId);
        verify(projectRepository).save(project);
    }
}