package cn.ggstd.exception;

/**
 * Created by lixing on 2021-2-25 上午 8:42.
 */
public class ExceptionFactory {

    public static RuntimeException wrapException(String message,Exception e){
        return new RpcException(ErrorContext.instance().cause(e).message(message).toString(),e);
    }

}
