package loremipsum.dev.taskmanagement.resultHelper;

import lombok.Getter;

@Getter
public class Result {
    private boolean status;
    private String message;
    private String code;

    public Result(String code, String message, boolean status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
