package loremipsum.dev.taskmanagement.abstracts;

import loremipsum.dev.taskmanagement.entities.Comment;

import java.util.List;
import java.util.UUID;

public interface ICommentService {
    Comment addComment(UUID taskId, UUID userId, String content);
    List<Comment> getCommentsByTask(UUID taskId);
}
