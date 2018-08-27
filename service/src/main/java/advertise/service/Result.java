package advertise.service;



import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class Result {
    private String message;
    private List<ErrorCode> errors;
    private Object[] extras;

    public Result(Object... extras) {
        this.extras = extras;
    }

    public static Result newResultInstance(Object... extras) {
        return new Result(extras);
    }

    public String getMessage() {
        return message;
    }

    public List<ErrorCode> getErrors() {
        return errors;
    }

    public Object[] getExtras() {
        return extras;
    }

    public boolean isSuccess() {
        return errors == null || errors.isEmpty();
    }

    public void onResultCall(BiConsumer<String, Object[]> successCallback, TriConsumer<String, List<ErrorCode>, Object[]> failCallback) {
        if (isSuccess())
            successCallback.accept(message, extras);
        else
            failCallback.accept(message, errors, extras);
    }

    public <R> R  onResultCallAndGet(BiFunction<String, Object[], R> successCallback, TriFunction<String, List<ErrorCode>, Object[], R> failCallback) {
        if (isSuccess())
            return successCallback.apply(message, extras);
        else
            return failCallback.apply(message, errors, extras);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void addError(ErrorCode error) {
        if (Objects.isNull(this.errors))
            this.errors = new ArrayList<>();
        errors.add(error);
    }

    public void setExtras(Object[] extras) {
        this.extras = extras;
    }
}
