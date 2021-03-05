package cn.ggstd.common.model.http;

import lombok.Data;

/**
 * Created by lixing on 2021-2-25 下午 5:55.
 */
@Data
public class RpcRequest extends RpcHttp {

    private String method;
    private Object[] parameter;
    private Class<?>[] parameterType;

    public RpcRequest(String requestId, String serviceName, String method, Object[] parameter, Class<?>[] parameterType) {
        super(requestId, serviceName);
        this.method = method;
        this.parameter = parameter;
        this.parameterType = parameterType;
    }
}
