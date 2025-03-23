package loremipsum.dev.taskmanagement;

import loremipsum.dev.taskmanagement.abstracts.IAttachmentService;
import loremipsum.dev.taskmanagement.entities.Attachment;
import loremipsum.dev.taskmanagement.exception.AttachmentNotFoundException;
import loremipsum.dev.taskmanagement.exception.GlobalExceptionHandler;
import loremipsum.dev.taskmanagement.exception.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AttachmentControllerTest {

    @Mock
    private IAttachmentService attachmentService;

    @InjectMocks
    private AttachmentController attachmentController;

    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<UUID> uuidCaptor;

    @Captor
    private ArgumentCaptor<MockMultipartFile> fileCaptor;

    private UUID taskId;
    private UUID attachmentId;
    private MockMultipartFile file;
    private Attachment attachment;
    private List<Attachment> attachments;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(attachmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        taskId = UUID.randomUUID();
        attachmentId = UUID.randomUUID();
        file = new MockMultipartFile("file", "file.txt", MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());

        attachment = new Attachment();
        attachment.setId(attachmentId);

        attachments = List.of(attachment);
    }

    @Test
    void testAddAttachmentToTask() throws Exception {
        when(attachmentService.addAttachmentToTask(any(UUID.class), any(MockMultipartFile.class))).thenReturn(attachment);

        mockMvc.perform(multipart("/attachments/task/{taskId}", taskId)
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(attachment.getId().toString()));

        verify(attachmentService).addAttachmentToTask(uuidCaptor.capture(), fileCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(taskId);
        assertThat(fileCaptor.getValue().getOriginalFilename()).isEqualTo(file.getOriginalFilename());
    }

    @Test
    void testGetAttachmentsByTaskId() throws Exception {
        when(attachmentService.getAttachmentsByTaskId(any(UUID.class))).thenReturn(attachments);

        mockMvc.perform(get("/attachments/task/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(attachment.getId().toString()));

        verify(attachmentService).getAttachmentsByTaskId(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(taskId);
    }

    @Test
    void testDeleteAttachment() throws Exception {
        doNothing().when(attachmentService).deleteAttachment(any(UUID.class));

        mockMvc.perform(delete("/attachments/{attachmentId}", attachmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Process successfully executed."))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value(true));

        verify(attachmentService).deleteAttachment(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(attachmentId);
    }

    @Test
    void testAddAttachmentToTask_TaskNotFound() throws Exception {
        doThrow(new TaskNotFoundException(taskId.toString())).when(attachmentService).addAttachmentToTask(any(UUID.class), any(MockMultipartFile.class));

        mockMvc.perform(multipart("/attachments/task/{taskId}", taskId)
                        .file(file))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Task not found with ID: " + taskId))
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.status").value(false));

        verify(attachmentService).addAttachmentToTask(uuidCaptor.capture(), fileCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(taskId);
        assertThat(fileCaptor.getValue().getOriginalFilename()).isEqualTo(file.getOriginalFilename());
    }

    @Test
    void testDeleteAttachment_NotFound() throws Exception {
        doThrow(new AttachmentNotFoundException(attachmentId.toString())).when(attachmentService).deleteAttachment(any(UUID.class));

        mockMvc.perform(delete("/attachments/{attachmentId}", attachmentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Data not found."))
                .andExpect(jsonPath("$.code").value("404"))
                .andExpect(jsonPath("$.status").value(false));

        verify(attachmentService).deleteAttachment(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(attachmentId);
    }
}
