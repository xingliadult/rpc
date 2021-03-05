package cn.ggstd.server.register;

/**
 * Created by lixing on 2021-2-25 上午 8:25.
 */
public interface ServerRegister {

    void register(ServiceObject serviceObject) throws Exception;
    ServiceObject getService(String serviceName) throws Exception;

}
