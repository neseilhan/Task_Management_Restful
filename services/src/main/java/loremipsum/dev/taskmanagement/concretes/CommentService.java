package loremipsum.dev.taskmanagement.concretes;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.abstracts.ICommentService;
import loremipsum.dev.taskmanagement.entities.Comment;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.exception.CommentNotFoundException;
import loremipsum.dev.taskmanagement.exception.TaskNotFoundException;
import loremipsum.dev.taskmanagement.exception.UserNotFoundException;
import loremipsum.dev.taskmanagement.repositories.CommentRepository;
import loremipsum.dev.taskmanagement.repositories.TaskRepository;
import loremipsum.dev.taskmanagement.repositories.UserRepository;
import loremipsum.dev.taskmanagement.request.CommentRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;


    @Override
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER')")
    public Comment addCommentToTask(UUID taskId, UUID userId, String content) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Comment comment = Comment.builder()
                .task(task)
                .user(user)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        return commentRepository.save(comment);
    }

    @Override
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER')")
    public List<Comment> getCommentsByTaskId(UUID taskId) {
        return commentRepository.findByTaskId(taskId);
    }

    @Override
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER')")
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER')")
    public Comment getCommentById(UUID id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id.toString()));
    }

    @Override
    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER')")
    public void deleteComment(UUID id) {
        Comment comment = getCommentById(id);
        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    private CommentRequest convertToDTO(Comment comment) {
        return CommentRequest.builder()
                .id(comment.getId())
                .taskId(comment.getTask().getId())
                .userId(comment.getUser().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .deleted(comment.isDeleted())
                .build();
    }

}
