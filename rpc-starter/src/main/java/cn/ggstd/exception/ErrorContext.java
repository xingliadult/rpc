package cn.ggstd.exception;

/**
 * Created by lixing on 2021-2-25 上午 8:42.
 */
public class ErrorContext {

    private static final ThreadLocal<ErrorContext> LOCAL = new ThreadLocal<>();

    private String message;
    private Throwable cause;

    private ErrorContext() {
    }

    public static ErrorContext instance(){
        ErrorContext context = LOCAL.get();
        if(context == null){
            context = new ErrorContext();
            LOCAL.set(context);
        }
        return context;
    }

    public ErrorContext message(String message) {
        this.message = message;
        return this;
    }

    public ErrorContext cause(Throwable cause){
        this.cause = cause;
        return this;
    }

    public void reset(){
        this.cause = null;
        this.message = null;
        LOCAL.remove();
    }

    @Override
    public String toString() {
        return "{\"ErrorContext\":{"
                + "\"message\":\""
                + message + '\"'
                + ",\"cause\":"
                + cause
                + "}}";
    }
}
