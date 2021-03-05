package cn.ggstd.server;

import cn.ggstd.common.model.service.RpcService;

/**
 * Created by lixing on 2021-2-25 上午 8:24.
 */
public abstract class RpcServer extends RpcService{

    protected final RequestHandler requestHandler;

    public RpcServer(String protocol, Integer port, RequestHandler requestHandler) {
        super(protocol, port);
        this.requestHandler = requestHandler;
    }

    //开启服务
    public abstract void start();
    //关闭服务
    public abstract void stop();
}
