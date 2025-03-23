package loremipsum.dev.taskmanagement.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import loremipsum.dev.taskmanagement.entities.Attachment;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentResponse {
    private UUID id;
    private UUID taskId;
    private String filePath;
    private LocalDateTime uploadedAt;
    private boolean deleted;

    public AttachmentResponse(Attachment attachment) {
        this.id = attachment.getId();
        this.taskId = attachment.getTask().getId();
        this.filePath = attachment.getFilePath();
        this.uploadedAt = attachment.getUploadedAt();
        this.deleted = attachment.isDeleted();
    }
}