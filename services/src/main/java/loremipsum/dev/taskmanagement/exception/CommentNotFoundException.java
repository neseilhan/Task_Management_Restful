package loremipsum.dev.taskmanagement.exception;

import loremipsum.dev.taskmanagement.resultHelper.Message;

public class CommentNotFoundException extends RuntimeException{
    public CommentNotFoundException(String id) {
        super(Message.COMMENT_NOT_FOUND + id);
    }
}
