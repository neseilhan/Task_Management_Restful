package loremipsum.dev.taskmanagement.abstracts;

import loremipsum.dev.taskmanagement.entities.Attachment;

import java.util.List;
import java.util.UUID;

public interface IAttachmentService {
    Attachment addAttachmentToTask(UUID taskId, Attachment attachment);
    List<Attachment> getAttachmentsByTaskId(UUID taskId);
    void deleteAttachment(UUID attachmentId);
}
