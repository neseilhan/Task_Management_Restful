package loremipsum.dev.taskmanagement.concretes;

import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.abstracts.IAttachmentService;
import loremipsum.dev.taskmanagement.entities.Attachment;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.exception.DuplicateRecordException;
import loremipsum.dev.taskmanagement.exception.TaskNotFoundException;
import loremipsum.dev.taskmanagement.repositories.AttachmentRepository;
import loremipsum.dev.taskmanagement.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService implements IAttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;

    @Value("${attachments.directory}")
    private String attachmentsDirectory;

    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public Attachment addAttachmentToTask(UUID taskId, MultipartFile file) throws IOException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId.toString()));

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(attachmentsDirectory, fileName);
        Files.createDirectories(filePath.getParent());
        file.transferTo(filePath.toFile());

        Attachment attachment = Attachment.builder()
                .task(task)
                .filePath(filePath.toString())
                .build();

        return attachmentRepository.save(attachment);
    }

    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public List<Attachment> getAttachmentsByTaskId(UUID taskId) {
        return attachmentRepository.findByTaskId(taskId);
    }

    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public void deleteAttachment(UUID attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new DuplicateRecordException(attachmentId.toString()));
        attachment.setDeleted(true);
        attachmentRepository.save(attachment);
    }
}