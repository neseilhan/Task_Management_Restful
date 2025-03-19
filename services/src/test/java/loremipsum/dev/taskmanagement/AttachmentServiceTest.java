package loremipsum.dev.taskmanagement;

import loremipsum.dev.taskmanagement.concretes.AttachmentService;
import loremipsum.dev.taskmanagement.entities.Attachment;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.exception.DuplicateRecordException;
import loremipsum.dev.taskmanagement.exception.TaskNotFoundException;
import loremipsum.dev.taskmanagement.repositories.AttachmentRepository;
import loremipsum.dev.taskmanagement.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private AttachmentService attachmentService;

    private final String attachmentsDirectory = "D:/IdeaProjects/taskmanagement/attachments";

    private Task task;
    private Attachment attachment;
    private UUID taskId;
    private UUID attachmentId;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        attachmentId = UUID.randomUUID();

        task = new Task();
        task.setId(taskId);

        attachment = new Attachment();
        attachment.setId(attachmentId);
        attachment.setTask(task);
        attachment.setFilePath(attachmentsDirectory + "/test-file.txt");

        attachmentService = new AttachmentService(attachmentRepository, taskRepository);
        setAttachmentsDirectory(attachmentService, attachmentsDirectory);
    }

    private void setAttachmentsDirectory(AttachmentService service, String directory) {
        try {
            java.lang.reflect.Field field = AttachmentService.class.getDeclaredField("attachmentsDirectory");
            field.setAccessible(true);
            field.set(service, directory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAddAttachmentToTask() throws IOException {
        String fileName = "test-file.txt";
        String storedFileName = UUID.randomUUID() + "_" + fileName;
        Path filePath = Paths.get(attachmentsDirectory, storedFileName);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(file.getOriginalFilename()).thenReturn(fileName);

        Attachment savedAttachment = Attachment.builder()
                .task(task)
                .filePath(filePath.toString())
                .build();

        when(attachmentRepository.save(any(Attachment.class))).thenReturn(savedAttachment);

        Attachment result = attachmentService.addAttachmentToTask(taskId, file);

        assertNotNull(result);
        assertEquals(task, result.getTask());
        assertEquals(filePath.toString(), result.getFilePath());

        verify(taskRepository, times(1)).findById(taskId);
        verify(file, times(1)).getOriginalFilename();
        verify(attachmentRepository, times(1)).save(any(Attachment.class));
    }

    @Test
    void testAddAttachmentToTask_TaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            attachmentService.addAttachmentToTask(taskId, file);
        });

        assertEquals("Task not found with ID: " + taskId, exception.getMessage());

        verify(taskRepository, times(1)).findById(taskId);
        verify(file, never()).getOriginalFilename();
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

    @Test
    void testGetAttachmentsByTaskId() {
        when(attachmentRepository.findByTaskId(taskId)).thenReturn(List.of(attachment));

        List<Attachment> result = attachmentService.getAttachmentsByTaskId(taskId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(attachment, result.get(0));

        verify(attachmentRepository, times(1)).findByTaskId(taskId);
    }

    @Test
    void testDeleteAttachment() {
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.of(attachment));

        attachmentService.deleteAttachment(attachmentId);

        assertTrue(attachment.isDeleted());

        verify(attachmentRepository, times(1)).findById(attachmentId);
        verify(attachmentRepository, times(1)).save(attachment);
    }

    @Test
    void testDeleteAttachment_DuplicateRecordException() {
        when(attachmentRepository.findById(attachmentId)).thenReturn(Optional.empty());

        DuplicateRecordException exception = assertThrows(DuplicateRecordException.class, () -> {
            attachmentService.deleteAttachment(attachmentId);
        });

        assertEquals("Duplicate records are not allowed." + attachmentId, exception.getMessage());

        verify(attachmentRepository, times(1)).findById(attachmentId);
        verify(attachmentRepository, never()).save(any(Attachment.class));
    }

}