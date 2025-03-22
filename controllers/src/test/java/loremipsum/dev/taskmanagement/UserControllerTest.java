package loremipsum.dev.taskmanagement;

import lombok.SneakyThrows;
import loremipsum.dev.taskmanagement.abstracts.IUserService;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.enums.RoleType;
import loremipsum.dev.taskmanagement.exception.DuplicateRecordException;
import loremipsum.dev.taskmanagement.exception.GlobalExceptionHandler;
import loremipsum.dev.taskmanagement.exception.UserNotFoundException;
import loremipsum.dev.taskmanagement.request.AssignUserRequest;
import loremipsum.dev.taskmanagement.request.UpdateUserRequest;
import loremipsum.dev.taskmanagement.resultHelper.Message;
import loremipsum.dev.taskmanagement.resultHelper.ResultHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private IUserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @SneakyThrows
    void testGetUserById() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()));

        verify(userService).getUserById(userId);
    }

    @Test
    @SneakyThrows
    void testGetUserByIdNotFound() {
        UUID userId = UUID.randomUUID();
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService).getUserById(userId);
    }

    @Test
    @SneakyThrows
    void testGetAllUsers() {
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        User user2 = new User();
        user2.setId(UUID.randomUUID());

        List<User> users = List.of(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(user1.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(user2.getId().toString()));

        verify(userService).getAllUsers();
    }

    @Test
    @SneakyThrows
    void testGetAllUsersNull() {
        when(userService.getAllUsers()).thenReturn(null);

        mockMvc.perform(get("/users"))
                .andExpect(status().isNoContent());

        verify(userService).getAllUsers();
    }
    @Test
    @SneakyThrows
    void testGetAllUsersNoContent() {
        when(userService.getAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/users"))
                .andExpect(status().isNoContent());

        verify(userService).getAllUsers();
    }

    @Test
    @SneakyThrows
    void testDeleteUser() {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResultHelper.success().getMessage()));

        verify(userService).deleteUser(userId);
    }

    @Test
    @SneakyThrows
    void testDeleteUserNotFound() {
        UUID userId = UUID.randomUUID();
        doThrow(new UserNotFoundException(userId.toString()))
                .when(userService).deleteUser(userId);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with ID: " + userId));
    }

    @Test
    @SneakyThrows
    void testAssignUserToTask() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        AssignUserRequest request = new AssignUserRequest();
        request.setUserId(userId);
        request.setTaskId(taskId);

        String requestJson = "{"
                + "\"userId\":\"" + userId.toString() + "\","
                + "\"taskId\":\"" + taskId.toString() + "\""
                + "}";

        mockMvc.perform(post("/users/assign-to-task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResultHelper.success().getMessage()));

        verify(userService).assignUserToTask(taskId, userId);
    }

    @Test
    @SneakyThrows
    void testAssignUserToProject() {
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        AssignUserRequest request = new AssignUserRequest();
        request.setUserId(userId);
        request.setProjectId(projectId);

        String requestJson = "{"
                + "\"userId\":\"" + userId.toString() + "\","
                + "\"projectId\":\"" + projectId.toString() + "\""
                + "}";

        mockMvc.perform(post("/users/assign-to-project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(ResultHelper.success().getMessage()));

        verify(userService).assignUserToProject(projectId, userId);
    }

    @Test
    @SneakyThrows
    void testAssignUserToTaskDuplicate() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        AssignUserRequest request = new AssignUserRequest();
        request.setUserId(userId);
        request.setTaskId(taskId);

        doThrow(new DuplicateRecordException(Message.DUPLICATE_RECORD))
                .when(userService).assignUserToTask(taskId, userId);

        String requestJson = "{" +
                "\"userId\":\"" + userId.toString() + "\"," +
                "\"taskId\":\"" + taskId.toString() + "\"}";

        mockMvc.perform(post("/users/assign-to-task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Duplicate records are not allowed."));
    }

    @Test
    @SneakyThrows
    void testUpdateUser() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("Updated Username");
        request.setEmail("updated@example.com");
        request.setPassword("newpassword");
        request.setRoleType(RoleType.PROJECT_MANAGER);

        when(userService.updateUser(any(UUID.class), any(UpdateUserRequest.class))).thenReturn(user);

        String requestJson = "{"
                + "\"username\":\"Updated Username\","
                + "\"email\":\"updated@example.com\","
                + "\"password\":\"newpassword\","
                + "\"roleType\":\"PROJECT_MANAGER\""
                + "}";

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()));

        verify(userService).updateUser(userId, request);
    }

    @Test
    @SneakyThrows
    void testUpdateUserNotFound() {
        UUID userId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("Updated Username");
        request.setEmail("updated@example.com");
        request.setPassword("newpassword");
        request.setRoleType(RoleType.PROJECT_MANAGER);

        when(userService.updateUser(any(UUID.class), any(UpdateUserRequest.class)))
                .thenThrow(new UserNotFoundException(userId.toString()));

        String requestJson = "{" +
                "\"username\":\"Updated Username\"," +
                "\"email\":\"updated@example.com\"," +
                "\"password\":\"newpassword\"," +
                "\"roleType\":\"PROJECT_MANAGER\"}";

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with ID: " + userId));
    }
}