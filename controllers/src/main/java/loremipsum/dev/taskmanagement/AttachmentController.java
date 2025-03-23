package loremipsum.dev.taskmanagement;

import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.abstracts.IAttachmentService;
import loremipsum.dev.taskmanagement.entities.Attachment;
import loremipsum.dev.taskmanagement.resultHelper.Result;
import loremipsum.dev.taskmanagement.resultHelper.ResultHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController {
    private final IAttachmentService attachmentService;

    @PostMapping("/task/{taskId}")
    public ResponseEntity<Attachment> addAttachmentToTask(@PathVariable UUID taskId, @RequestParam("file") MultipartFile file) throws IOException {
        Attachment createdAttachment = attachmentService.addAttachmentToTask(taskId, file);
        return ResponseEntity.ok(createdAttachment);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Attachment>> getAttachmentsByTaskId(@PathVariable UUID taskId) {
        List<Attachment> attachments = attachmentService.getAttachmentsByTaskId(taskId);
        return ResponseEntity.ok(attachments);
    }

    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Result> deleteAttachment(@PathVariable UUID attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.ok(ResultHelper.success());
    }
}