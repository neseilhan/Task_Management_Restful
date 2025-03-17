package loremipsum.dev.taskmanagement;

import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.abstracts.IAttachmentService;
import loremipsum.dev.taskmanagement.entities.Attachment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final IAttachmentService attachmentService;

    @PostMapping("/task/{taskId}")
    public ResponseEntity<Attachment> addAttachmentToTask(@PathVariable UUID taskId, @RequestBody Attachment attachment) {
        Attachment createdAttachment = attachmentService.addAttachmentToTask(taskId, attachment);
        return ResponseEntity.ok(createdAttachment);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Attachment>> getAttachmentsByTaskId(@PathVariable UUID taskId) {
        List<Attachment> attachments = attachmentService.getAttachmentsByTaskId(taskId);
        return ResponseEntity.ok(attachments);
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable UUID attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
}