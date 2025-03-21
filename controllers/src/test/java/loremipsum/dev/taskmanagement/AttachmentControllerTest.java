package loremipsum.dev.taskmanagement;

import loremipsum.dev.taskmanagement.abstracts.IAttachmentService;
import loremipsum.dev.taskmanagement.entities.Attachment;
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

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(attachmentController).build();
    }

    @Test
    void testAddAttachmentToTask() throws Exception {
        UUID taskId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "file.txt", MediaType.TEXT_PLAIN_VALUE, "Test content".getBytes());
        Attachment attachment = new Attachment();
        attachment.setId(UUID.randomUUID());

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
        UUID taskId = UUID.randomUUID();
        Attachment attachment = new Attachment();
        attachment.setId(UUID.randomUUID());
        List<Attachment> attachments = List.of(attachment);

        when(attachmentService.getAttachmentsByTaskId(any(UUID.class))).thenReturn(attachments);

        mockMvc.perform(get("/attachments/task/{taskId}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(attachment.getId().toString()));

        verify(attachmentService).getAttachmentsByTaskId(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(taskId);
    }

    @Test
    void testDeleteAttachment() throws Exception {
        UUID attachmentId = UUID.randomUUID();

        doNothing().when(attachmentService).deleteAttachment(any(UUID.class));

        mockMvc.perform(delete("/attachments/{attachmentId}", attachmentId))
                .andExpect(status().isNoContent());

        verify(attachmentService).deleteAttachment(uuidCaptor.capture());
        assertThat(uuidCaptor.getValue()).isEqualTo(attachmentId);
    }
}