package cn.ggstd.common.client.net;

import cn.ggstd.common.client.Configuration;
import cn.ggstd.common.constant.Constant;
import cn.ggstd.common.model.http.RpcRequest;
import cn.ggstd.common.model.http.RpcResponse;
import cn.ggstd.common.model.service.RpcService;
import cn.ggstd.exception.ExceptionFactory;
import cn.ggstd.exception.RpcException;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lixing on 2021-2-26 下午 2:26.
 * 客户端代理类
 */
public class ClientProxy {

    private final Map<Class<?>,Object> objectCache = new HashMap<>();
    private Configuration config;

    public <T> T getProxy(Class<T> clazz) {
        return (T) objectCache.computeIfAbsent(clazz, clz ->
                Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clz},new ClientInvocationHandler(clz)));
    }

    private class ClientInvocationHandler implements InvocationHandler{

        private final Class<?> clazz;

        private ClientInvocationHandler(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            if(Constant.TO_STRING.equals(methodName)){
                return proxy.toString();
            }
            if(Constant.HASH_CODE.equals(methodName)){
                return 0;
            }
            RpcService rpcService = config.getRpcService(clazz.getName());

            Assert.notNull(rpcService, String.format("can not found rpcService for %s", clazz.getName()));

            RpcRequest request = new RpcRequest(UUID.randomUUID().toString(),rpcService.getServiceName(),methodName,args,method.getParameterTypes());
            RpcResponse response = null;
            try {
                response = config.getNetClient().sendRequest(request,rpcService,config.getProtocolPolicy(rpcService.getProtocol()));
            }finally {
                if(response == null){
                    ExceptionFactory.wrapException("the response is null",new RpcException());
                }
            }
            if(response.getException() != null){
                return response.getException();
            }
            return response.getResult();
        }
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }
}
