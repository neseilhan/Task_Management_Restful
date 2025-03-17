package loremipsum.dev.taskmanagement.response;

import lombok.Data;
import loremipsum.dev.taskmanagement.entities.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CommentResponse {
    private UUID id;
    private UUID taskId;
    private UUID userId;
    private String content;
    private LocalDateTime createdAt;
    private boolean deleted;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.taskId = comment.getTask().getId();
        this.userId = comment.getUser().getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.deleted = comment.isDeleted();
    }
}