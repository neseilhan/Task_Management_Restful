package loremipsum.dev.taskmanagement.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.enums.RoleType;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private boolean deleted;

    public UserResponse(User user) {
        this.id = user.getId().toString();
        this.name = user.getUsername();
        this.email = user.getEmail();
        this.deleted = user.isDeleted();
    }
}