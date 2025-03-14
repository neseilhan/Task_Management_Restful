package loremipsum.dev.taskmanagement.concretes;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import loremipsum.dev.taskmanagement.entities.Attachment;
import loremipsum.dev.taskmanagement.repositories.AttachmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentService {
    private final AttachmentRepository attachmentRepository;

    public List<Attachment> getAllAttachments() {
        return attachmentRepository.findAll();
    }

    public Attachment getAttachmentById(UUID id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found"));
    }

    public Attachment saveAttachment(Attachment attachment) {
        return attachmentRepository.save(attachment);
    }

    public void deleteAttachment(UUID id) {
        Attachment attachment = getAttachmentById(id);
        attachmentRepository.delete(attachment);
    }
}
