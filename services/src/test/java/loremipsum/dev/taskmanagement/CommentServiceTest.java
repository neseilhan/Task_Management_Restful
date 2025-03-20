package loremipsum.dev.taskmanagement;

import loremipsum.dev.taskmanagement.concretes.CommentService;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private Task task;
    private User user;
    private Comment comment;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id(UUID.randomUUID())
                .title("Test Task")
                .description("Test Task Description")
                .build();

        user = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .build();

        comment = Comment.builder()
                .id(UUID.randomUUID())
                .task(task)
                .user(user)
                .content("Test Comment")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testAddCommentToTask_Success() {
        UUID taskId = task.getId();
        UUID userId = user.getId();
        String content = "Test Comment";

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.addCommentToTask(taskId, userId, content);

        assertThat(result).isEqualTo(comment);

        verify(taskRepository).findById(taskId);
        verify(userRepository).findById(userId);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testAddCommentToTask_TaskNotFound() {
        UUID taskId = UUID.randomUUID();
        UUID userId = user.getId();
        String content = "Test Comment";

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> commentService.addCommentToTask(taskId, userId, content));

        verify(taskRepository).findById(taskId);
        verify(userRepository, never()).findById(userId);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testAddCommentToTask_UserNotFound() {
        UUID taskId = task.getId();
        UUID userId = UUID.randomUUID();
        String content = "Test Comment";

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> commentService.addCommentToTask(taskId, userId, content));

        verify(taskRepository).findById(taskId);
        verify(userRepository).findById(userId);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void testGetCommentsByTaskId_Success() {
        UUID taskId = task.getId();
        List<Comment> comments = List.of(comment);

        when(commentRepository.findByTaskId(taskId)).thenReturn(comments);

        List<Comment> result = commentService.getCommentsByTaskId(taskId);

        assertThat(result).isEqualTo(comments);

        verify(commentRepository).findByTaskId(taskId);
    }

    @Test
    void testGetAllComments_Success() {
        List<Comment> comments = List.of(comment);

        when(commentRepository.findAll()).thenReturn(comments);

        List<Comment> result = commentService.getAllComments();

        assertThat(result).isEqualTo(comments);

        verify(commentRepository).findAll();
    }

    @Test
    void testGetCommentById_Success() {
        UUID commentId = comment.getId();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment result = commentService.getCommentById(commentId);

        assertThat(result).isEqualTo(comment);

        verify(commentRepository).findById(commentId);
    }

    @Test
    void testGetCommentById_NotFound() {
        UUID commentId = UUID.randomUUID();

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.getCommentById(commentId));

        verify(commentRepository).findById(commentId);
    }

    @Test
    void testDeleteComment_Success() {
        UUID commentId = comment.getId();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(commentId);

        assertThat(comment.isDeleted()).isTrue();

        verify(commentRepository).findById(commentId);
        verify(commentRepository).save(comment);
    }

    @Test
    void testDeleteComment_NotFound() {
        UUID commentId = UUID.randomUUID();

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(commentId));

        verify(commentRepository).findById(commentId);
        verify(commentRepository, never()).save(any(Comment.class));
    }
    @Test
    void testConvertToDTO_Success() throws Exception {
        Method method = CommentService.class.getDeclaredMethod("convertToDTO", Comment.class);
        method.setAccessible(true);

        CommentRequest commentRequest = (CommentRequest) method.invoke(commentService, comment);

        assertThat(commentRequest.getId()).isEqualTo(comment.getId());
        assertThat(commentRequest.getTaskId()).isEqualTo(comment.getTask().getId());
        assertThat(commentRequest.getUserId()).isEqualTo(comment.getUser().getId());
        assertThat(commentRequest.getContent()).isEqualTo(comment.getContent());
        assertThat(commentRequest.getCreatedAt()).isEqualTo(comment.getCreatedAt());
        assertThat(commentRequest.isDeleted()).isEqualTo(comment.isDeleted());
    }
}