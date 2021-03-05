package cn.ggstd.common.model.service;

import lombok.Data;

/**
 * Created by lixing on 2021-2-25 上午 9:55.
 */
@Data
public class RpcService extends Service{

    protected String protocol;
    private String address;//ip:端口
    protected Integer weight;//权重
    protected Integer port;

    public RpcService() {
    }

    public RpcService(String protocol, Integer port) {
        this.protocol = protocol;
        this.port = port;
    }

    public RpcService(String serviceName, String protocol, String address, Integer weight) {
        super.serviceName = serviceName;
        this.protocol = protocol;
        this.address = address;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "{\"RpcService\":{"
                + "\"serviceName\":\""
                + serviceName + '\"'
                + ",\"protocol\":\""
                + protocol + '\"'
                + ",\"address\":\""
                + address + '\"'
                + ",\"weight\":"
                + weight
                + ",\"port\":"
                + port
                + "},\"super-RpcService\":" + super.toString() + "}";
    }
}
