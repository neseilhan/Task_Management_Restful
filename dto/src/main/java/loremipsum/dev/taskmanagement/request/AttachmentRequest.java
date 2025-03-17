package loremipsum.dev.taskmanagement.request;

import lombok.Data;

import java.util.UUID;

@Data
public class AttachmentRequest {
    private UUID taskId;
    private String filePath;
}
