package loremipsum.dev.taskmanagement.concretes;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.abstracts.IAttachmentService;
import loremipsum.dev.taskmanagement.config.Message;
import loremipsum.dev.taskmanagement.entities.Attachment;
import loremipsum.dev.taskmanagement.entities.Task;
import loremipsum.dev.taskmanagement.exception.AttachmentNotFoundException;
import loremipsum.dev.taskmanagement.exception.TaskNotFoundException;
import loremipsum.dev.taskmanagement.repositories.AttachmentRepository;
import loremipsum.dev.taskmanagement.repositories.TaskRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService implements IAttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;

    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public Attachment addAttachmentToTask(UUID taskId, Attachment attachment) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId.toString()));
        attachment.setTask(task);
        return attachmentRepository.save(attachment);
    }

    @PreAuthorize("hasAnyRole('TEAM_MEMBER', 'TEAM_LEADER', 'PROJECT_MANAGER')")
    @Override
    public List<Attachment> getAttachmentsByTaskId(UUID taskId) {
        return attachmentRepository.findByTaskId(taskId);
    }

    @PreAuthorize("hasRole('TEAM_MEMBER')")
    @Override
    public void deleteAttachment(UUID attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException(attachmentId.toString()));
        attachment.setDeleted(true);
        attachmentRepository.save(attachment);
    }
}