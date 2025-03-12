package loremipsum.dev.taskmanagement.application.config;

public class ResultData<T> extends Result {

    private T data;
    public ResultData(String code, String message, boolean status, T data) {
        super(code, message, status);
        this.data = data;
    }
}
