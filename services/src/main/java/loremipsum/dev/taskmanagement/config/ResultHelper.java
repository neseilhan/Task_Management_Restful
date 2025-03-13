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

    public static Result recordNotFoundWithId(Long id) {
        return new Result("404", Message.RECORD_NOT_FOUND_WITH_ID + id, false);
    }

    public static <T> ResultData<T> recordAlreadyExistsError(Long id, T data) {
        return new ResultData<>("409", Message.RECORD_ALREADY_EXISTS + id, false, data);
    }
}
