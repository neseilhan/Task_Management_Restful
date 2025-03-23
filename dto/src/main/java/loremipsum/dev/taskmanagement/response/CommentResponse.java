package loremipsum.dev.taskmanagement.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import loremipsum.dev.taskmanagement.entities.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private UUID id;
    private UUID taskId;
    private UUID userId;
    private String content;
    private LocalDateTime createdAt;
    private boolean deleted;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.taskId = comment.getTask() != null ? comment.getTask().getId() : null;
        this.userId = comment.getUser() != null ? comment.getUser().getId() : null;
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.deleted = comment.isDeleted();
    }
}