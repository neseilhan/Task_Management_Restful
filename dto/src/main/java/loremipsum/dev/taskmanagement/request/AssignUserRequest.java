package loremipsum.dev.taskmanagement.request;

import lombok.Data;

import java.util.UUID;

@Data
public class AssignUserRequest {
    private UUID taskId;
    private UUID projectId;
    private UUID userId;
}