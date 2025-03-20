package loremipsum.dev.taskmanagement.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import loremipsum.dev.taskmanagement.enums.RoleType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private String username;
    private String email;
    private String password;
    private RoleType roleType;
}