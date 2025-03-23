package loremipsum.dev.taskmanagement;

import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.abstracts.ICommentService;
import loremipsum.dev.taskmanagement.entities.Comment;
import loremipsum.dev.taskmanagement.response.CommentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final ICommentService commentService;

    @PostMapping("/task/{taskId}/user/{userId}")
    public ResponseEntity<CommentResponse> addCommentToTask(@PathVariable UUID taskId, @PathVariable UUID userId, @RequestBody String content) {
        Comment createdComment = commentService.addCommentToTask(taskId, userId, content);
        return ResponseEntity.ok(new CommentResponse(createdComment));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByTaskId(@PathVariable UUID taskId) {
        List<Comment> comments = commentService.getCommentsByTaskId(taskId);
        List<CommentResponse> commentResponses = comments.stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentResponses);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getAllComments() {
        List<Comment> comments = commentService.getAllComments();
        List<CommentResponse> commentResponses = comments.stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(commentResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable UUID id) {
        Comment comment = commentService.getCommentById(id);
        return ResponseEntity.ok(new CommentResponse(comment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}