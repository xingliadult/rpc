package cn.ggstd.common.model.http;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lixing on 2021-2-25 下午 5:51.
 */
@Data
public class RpcHttp implements Serializable{

    static final long serialVersionUID = 1L;

    private String requestId;
    private String serviceName;
    private Map<String,String> headers = new HashMap<>();

    public RpcHttp() {
    }

    public RpcHttp(String requestId, String serviceName) {
        this.requestId = requestId;
        this.serviceName = serviceName;
    }
}
