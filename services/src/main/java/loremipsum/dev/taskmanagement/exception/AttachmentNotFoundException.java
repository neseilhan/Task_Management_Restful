package loremipsum.dev.taskmanagement.exception;

import loremipsum.dev.taskmanagement.config.Message;

public class AttachmentNotFoundException extends RuntimeException{
    public AttachmentNotFoundException(String id) {
        super(Message.ATTACHMENT_NOT_FOUND + id);
    }
}
