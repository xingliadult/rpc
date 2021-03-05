package cn.ggstd.config;

import cn.ggstd.annotation.LoadBalance;
import cn.ggstd.annotation.MessageProtocol;
import cn.ggstd.common.client.balance.LoadBalancePolicy;
import cn.ggstd.common.client.net.ClientProxy;
import cn.ggstd.common.client.net.NettyNetClient;
import cn.ggstd.common.protocol.MessageProtocolPolicy;
import cn.ggstd.discovery.ServerDiscovery;
import cn.ggstd.discovery.ZookeeperServerDiscovery;
import cn.ggstd.exception.RpcException;
import cn.ggstd.server.NettyRpcServer;
import cn.ggstd.server.RequestHandler;
import cn.ggstd.server.RpcServer;
import cn.ggstd.server.register.DefaultRpcProcessor;
import cn.ggstd.server.register.ServerRegister;
import cn.ggstd.server.register.ZookeeperServerRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Created by lixing on 2021-3-1 下午 6:18.
 */
@Configuration
@EnableConfigurationProperties(RpcConfig.class)
public class RpcAutoConfiguration {

    private Map<String,MessageProtocolPolicy> supportProtocolPolicy = new HashMap<>();
    private ServerDiscovery serverDiscovery;

    @Bean
    public RpcConfig rpcConfig() {
        return new RpcConfig();
    }

    @Bean
    public NettyNetClient nettyNetClient() {
        return new NettyNetClient();
    }

    @Bean
    public ServerRegister serverRegister(@Autowired RpcConfig rpcConfig){
        return new ZookeeperServerRegister(rpcConfig.getRegisterAddress(),rpcConfig.getWeight(),rpcConfig.getPort(),rpcConfig.getProtocol(),rpcConfig.getHost());
    }

    @Bean
    public RequestHandler requestHandler(@Autowired ServerRegister serverRegister,@Autowired RpcConfig rpcConfig){
        setSupportProtocol(supportProtocolPolicy);
        return new RequestHandler(serverRegister,supportProtocolPolicy.get(rpcConfig.getProtocol()));
    }

    @Bean
    public RpcServer rpcServer(@Autowired RequestHandler handler,@Autowired RpcConfig rpcConfig){
        return new NettyRpcServer(rpcConfig.getProtocol(),rpcConfig.getPort(),handler);
    }

    @Bean
    public ClientProxy clientProxy(@Autowired RpcConfig rpcConfig,@Autowired NettyNetClient nettyNetClient){
        ClientProxy clientProxy = new ClientProxy();
        this.serverDiscovery = new ZookeeperServerDiscovery(rpcConfig.getRegisterAddress());
        cn.ggstd.common.client.Configuration configuration = new cn.ggstd.common.client.Configuration(
                getLoadBalance(rpcConfig.getLoadBalance()),this.supportProtocolPolicy,nettyNetClient,this.serverDiscovery);
        clientProxy.setConfig(configuration);
        return clientProxy;
    }

    @Bean
    public DefaultRpcProcessor defaultRpcProcessor(@Autowired ClientProxy clientProxy, @Autowired ServerRegister serverRegister,@Autowired RpcServer rpcServer){
        return new DefaultRpcProcessor(clientProxy,serverRegister,rpcServer,this.serverDiscovery);
    }

    private LoadBalancePolicy getLoadBalance(String loadBalance) {
        Iterator<LoadBalancePolicy> iterator = ServiceLoader.load(LoadBalancePolicy.class).iterator();
        while (iterator.hasNext()){
            LoadBalancePolicy balance = iterator.next();
            if (!balance.getClass().isAnnotationPresent(LoadBalance.class)) {
                throw new RpcException("load balance can not be null");
            }
            if(loadBalance.equals(balance.getClass().getAnnotation(LoadBalance.class).value())){
                return balance;
            }
        }
        return null;
    }

    private void setSupportProtocol(Map<String,MessageProtocolPolicy> supportProtocol) {
        Iterator<MessageProtocolPolicy> iterator = ServiceLoader.load(MessageProtocolPolicy.class).iterator();
        while (iterator.hasNext()){
            MessageProtocolPolicy messageProtocolPolicy = iterator.next();
            if (!messageProtocolPolicy.getClass().isAnnotationPresent(MessageProtocol.class)) {
                throw new RpcException("message protocol can not be null");
            }
            supportProtocol.put(messageProtocolPolicy.getClass().getAnnotation(MessageProtocol.class).value(),messageProtocolPolicy);
        }
    }

}
