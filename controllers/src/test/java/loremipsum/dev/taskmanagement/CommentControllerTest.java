package loremipsum.dev.taskmanagement;

import com.fasterxml.jackson.databind.ObjectMapper;
import loremipsum.dev.taskmanagement.abstracts.ICommentService;
import loremipsum.dev.taskmanagement.entities.Comment;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.entities.User;
import loremipsum.dev.taskmanagement.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @Mock
    private ICommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<UUID> uuidCaptor;

    @Captor
    private ArgumentCaptor<String> contentCaptor;

    private UUID taskId;
    private UUID userId;
    private Comment comment;
    private String content;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        taskId = UUID.randomUUID();
        userId = UUID.randomUUID();
        content = "This is a test comment";
        comment = new Comment();
        comment.setId(UUID.randomUUID());

        Task task = new Task();
        task.setId(taskId);
        comment.setTask(task);

        User user = new User();
        user.setId(userId);
        comment.setUser(user);
    }

    @Test
    void testAddCommentToTask() throws Exception {
        when(commentService.addCommentToTask(any(UUID.class), any(UUID.class), anyString())).thenReturn(comment);

        mockMvc.perform(post("/comments/task/{taskId}/user/{userId}", taskId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId().toString()));

        verify(commentService).addCommentToTask(uuidCaptor.capture(), uuidCaptor.capture(), contentCaptor.capture());
        assertThat(uuidCaptor.getAllValues().get(0)).isEqualTo(taskId);
        assertThat(uuidCaptor.getAllValues().get(1)).isEqualTo(userId);
        assertThat(contentCaptor.getValue()).isEqualTo(content);
    }

    @Test
    void testGetCommentsByTaskId() throws Exception {
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());

        Task task = new Task();
        task.setId(taskId);
        comment.setTask(task);

        User user = new User();
        user.setId(UUID.randomUUID());
        comment.setUser(user);

        when(commentService.getCommentsByTaskId(any(UUID.class))).thenReturn(List.of(comment));

        mockMvc.perform(get("/comments/task/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(comment.getId().toString()));

        verify(commentService).getCommentsByTaskId(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(taskId);
    }

    @Test
    void testGetAllComments() throws Exception {
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());

        Task task = new Task();
        task.setId(UUID.randomUUID());
        comment.setTask(task);

        User user = new User();
        user.setId(UUID.randomUUID());
        comment.setUser(user);

        when(commentService.getAllComments()).thenReturn(List.of(comment));

        mockMvc.perform(get("/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(comment.getId().toString()));

        verify(commentService).getAllComments();
    }

    @Test
    void testGetCommentById() throws Exception {
        UUID commentId = comment.getId();

        when(commentService.getCommentById(any(UUID.class))).thenReturn(comment);

        mockMvc.perform(get("/comments/{id}", commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId().toString()));

        verify(commentService).getCommentById(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(commentId);
    }

    @Test
    void testDeleteComment() throws Exception {
        UUID commentId = comment.getId();

        doNothing().when(commentService).deleteComment(any(UUID.class));

        mockMvc.perform(delete("/comments/{id}", commentId))
                .andExpect(status().isNoContent());

        verify(commentService).deleteComment(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(commentId);
    }
}