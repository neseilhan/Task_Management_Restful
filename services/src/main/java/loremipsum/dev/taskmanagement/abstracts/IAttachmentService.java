package loremipsum.dev.taskmanagement.abstracts;

import loremipsum.dev.taskmanagement.entities.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface IAttachmentService {
    Attachment addAttachmentToTask(UUID taskId, MultipartFile file) throws IOException;
    List<Attachment> getAttachmentsByTaskId(UUID taskId);
    void deleteAttachment(UUID attachmentId);
}
