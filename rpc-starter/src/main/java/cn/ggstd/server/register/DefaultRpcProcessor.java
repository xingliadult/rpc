package cn.ggstd.server.register;

import cn.ggstd.annotation.InjectService;
import cn.ggstd.annotation.RpcServiceAno;
import cn.ggstd.common.client.net.ClientProxy;
import cn.ggstd.discovery.ServerDiscovery;
import cn.ggstd.discovery.ServerDiscoveryCache;
import cn.ggstd.discovery.ZookeeperServerDiscovery;
import cn.ggstd.server.RpcServer;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by lixing on 2021-2-26 下午 2:27.
 * 项目启动时启动服务
 */
@Slf4j
public class DefaultRpcProcessor implements ApplicationListener<ContextRefreshedEvent> {

    private final ClientProxy clientProxy;
    private final ServerRegister serverRegister;
    private final RpcServer rpcServer;
    private ServerDiscovery serverDiscovery;

    public DefaultRpcProcessor(ClientProxy clientProxy, ServerRegister serverRegister, RpcServer rpcServer, ServerDiscovery serverDiscovery) {
        this.clientProxy = clientProxy;
        this.serverRegister = serverRegister;
        this.rpcServer = rpcServer;
        this.serverDiscovery = serverDiscovery;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext context = contextRefreshedEvent.getApplicationContext();
        if(context.getParent() == null){
            startServer(context);
            injectService(context);
        }
    }

    private void startServer(ApplicationContext context) {
        Map<String,Object> beans = context.getBeansWithAnnotation(RpcServiceAno.class);
        if(beans.size() > 0) {
            boolean startServer = true;
            for(Object obj:beans.values()){
                Class<?> clazz = obj.getClass();
                Class<?>[] interfaces = clazz.getInterfaces();
                String serviceName;
                ServiceObject serviceObject;
                try {
                    if(interfaces.length != 1){
                        serviceName = clazz.getAnnotation(RpcServiceAno.class).value();
                        if(StringUtil.isNullOrEmpty(serviceName)){
                            startServer = false;
                            throw new UnsupportedOperationException("the exposed interface is not specific with " + clazz.getName());
                        }
                        serviceObject = new ServiceObject(serviceName,Class.forName(serviceName),obj);
                    }else{
                        serviceObject = new ServiceObject(interfaces[0].getName(),interfaces[0],obj);
                    }
                    serverRegister.register(serviceObject);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(startServer){
                rpcServer.start();
            }
        }
    }

    private void injectService(ApplicationContext context) {
        for(String name : context.getBeanDefinitionNames()){
            Class<?> clazz = context.getType(name);
            if(clazz == null){
                continue;
            }
            for(Field field : clazz.getDeclaredFields()){
                if(field.isAnnotationPresent(InjectService.class)){
                    field.setAccessible(true);
                    try {
                        field.set(context.getBean(name),clientProxy.getProxy(field.getType()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    ServerDiscoveryCache.addServiceClass(field.getType().getName());
                }
            }
            //注册子节点监听
            if(serverDiscovery instanceof ZookeeperServerDiscovery){
                ZookeeperServerDiscovery zookeeperServerDiscovery = (ZookeeperServerDiscovery) serverDiscovery;
                zookeeperServerDiscovery.registerZKChild();
                log.info("subscribe zkChildNode successful");
            }
        }
    }
}
