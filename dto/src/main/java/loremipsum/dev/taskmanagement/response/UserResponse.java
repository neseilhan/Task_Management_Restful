package loremipsum.dev.taskmanagement.response;

import lombok.Data;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.enums.RoleType;

import java.util.Set;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private Set<RoleType> roles;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.roles = user.getRoles();
    }
}