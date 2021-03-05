package cn.ggstd.server.register;

import cn.ggstd.common.model.service.RpcService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lixing on 2021-2-25 上午 8:55.
 */
public class BaseServerRegister extends RpcService implements ServerRegister{

    public BaseServerRegister() {
    }

    private final Map<String,ServiceObject> cachedService = new HashMap<>();

    @Override
    public void register(ServiceObject serviceObject) throws Exception {
        if(serviceObject == null){
            throw new IllegalArgumentException("Service can not be null");
        }
        cachedService.put(serviceObject.getServiceName(),serviceObject);
    }

    @Override
    public ServiceObject getService(String serviceName) throws Exception {
        return cachedService.get(serviceName);
    }
}
