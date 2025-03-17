package loremipsum.dev.taskmanagement.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CommentRequest {
    private UUID id;
    private UUID taskId;
    private UUID userId;
    private String content;
    private LocalDateTime createdAt;
    private boolean deleted;
}