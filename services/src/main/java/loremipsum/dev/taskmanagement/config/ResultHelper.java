package loremipsum.dev.taskmanagement.config;

public class ResultHelper {
    public static <T> ResultData<T> created(T data){
        return new ResultData<>("201", Message.CREATED, true, data);
    }

    public static <T> ResultData<T> success(T data){
        return new ResultData<>("200", Message.OK, true, data);
    }

    public static Result notFoundError(){
        return new Result("404", Message.NOT_FOUND, false);
    }

    public static Result forbiddenError() {
        return new Result("403", Message.FORBIDDEN, false);
    }

    public static Result recordNotFoundWithId(String id) {
        return new Result("404", Message.RECORD_NOT_FOUND_WITH_ID + id, false);
    }

    public static <T> ResultData<T> recordAlreadyExistsError(String id, T data) {
        return new ResultData<>("409", Message.RECORD_ALREADY_EXISTS + id, false, data);
    }
    public static Result duplicateError() {
        return new Result("409", Message.DUPLICATE_RECORD, false);
    }

    public static Result success(){
        return new Result("200", Message.OK, true);
    }

    public static Result validationError(String message) {
        return new Result("400", Message.VALIDATION_ERROR, false);
    }

}
