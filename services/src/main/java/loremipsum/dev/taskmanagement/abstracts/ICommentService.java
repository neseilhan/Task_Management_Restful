package loremipsum.dev.taskmanagement.abstracts;

import loremipsum.dev.taskmanagement.entities.Comment;

import java.util.List;
import java.util.UUID;

public interface ICommentService {
    Comment addCommentToTask(UUID taskId, UUID userId, String content);
    List<Comment> getCommentsByTaskId(UUID taskId);
    List<Comment> getAllComments();
    Comment getCommentById(UUID id);
    void deleteComment(UUID id);
}
