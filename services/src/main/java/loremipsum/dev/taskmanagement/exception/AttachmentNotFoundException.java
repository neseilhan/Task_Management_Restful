package loremipsum.dev.taskmanagement.exception;

import loremipsum.dev.taskmanagement.resultHelper.Message;

public class AttachmentNotFoundException extends RuntimeException{
    public AttachmentNotFoundException(String id) {
        super(id);
    }
}
