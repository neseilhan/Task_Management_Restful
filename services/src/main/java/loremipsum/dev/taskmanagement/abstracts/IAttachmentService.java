package loremipsum.dev.taskmanagement.abstracts;

import loremipsum.dev.taskmanagement.entities.Attachment;

import java.util.List;
import java.util.UUID;

public interface IAttachmentService {
    Attachment uploadAttachment(UUID taskId, String filePath);
    List<Attachment> getAttachmentsByTask(UUID taskId);
    void deleteAttachment(UUID attachmentId);
}
