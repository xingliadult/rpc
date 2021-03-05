package cn.ggstd.exception;

/**
 * Created by lixing on 2021-2-25 上午 8:31.
 */
public class RpcException extends RuntimeException {

    static final long serialVersionUID = 1L;

    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

}
