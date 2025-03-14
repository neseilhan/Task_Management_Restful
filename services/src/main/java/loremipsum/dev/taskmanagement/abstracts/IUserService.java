package loremipsum.dev.taskmanagement.abstracts;

import loremipsum.dev.taskmanagement.entities.User;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    User updateUser(UUID userId, User user);
    User getUserById(UUID userId);
    List<User> getAllUsers();
}
