package loremipsum.dev.taskmanagement.abstracts;

import loremipsum.dev.taskmanagement.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserService {
    Optional<User> getUserById(UUID userId);
    List<User> getAllUsers();
    void deleteUser(UUID userId);
    void assignUserToTask(UUID userId, UUID taskId);
    void assignUserToProject(UUID userId, UUID projectId);
}
