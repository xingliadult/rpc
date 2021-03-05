package cn.ggstd.common.client.net;

import cn.ggstd.common.client.net.handler.SendHandler;
import cn.ggstd.common.constant.Constant;
import cn.ggstd.common.model.http.RpcRequest;
import cn.ggstd.common.model.http.RpcResponse;
import cn.ggstd.common.model.service.RpcService;
import cn.ggstd.common.protocol.MessageProtocolPolicy;
import cn.ggstd.discovery.ServerDiscoveryCache;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * Created by lixing on 2021-3-1 下午 4:47.
 */
@Slf4j
public class NettyNetClient implements NetClient{

    private final ExecutorService pool = new ThreadPoolExecutor(10,20,300, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000), new ThreadFactoryBuilder().setNameFormat("rpcClient-%d").build());
    private final EventLoopGroup loopGroup = new NioEventLoopGroup(10);
    private final ConcurrentMap<String,SendHandler> connectedServers = new ConcurrentHashMap<>();
    private final CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

    @Override
    public RpcResponse sendRequest(RpcRequest request, RpcService service, MessageProtocolPolicy protocolPolicy) {
        String address = service.getAddress();
        SendHandler handler;
        if(connectedServers.containsKey(address)){
            log.info("使用现有的连接");
            handler = connectedServers.get(address);
            return handler.sendRequest(request);
        }
        String[] addressInfo = service.getAddress().split(Constant.COLON);
        String url = addressInfo[0];
        Integer port = Integer.parseInt(addressInfo[1]);
        handler = new SendHandler(service.getAddress(),protocolPolicy,this);
        pool.submit(() -> {
            //配置客户端
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(handler);
                        }
                    });
            //启用客户端连接
            ChannelFuture channelFuture = bootstrap.connect(url,port);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    connectedServers.putIfAbsent(service.getAddress(),handler);
                    cyclicBarrier.await();
                }
            });
        });
        log.info("使用新的连接");
        return getResponse(request,handler);
    }

    private RpcResponse getResponse(RpcRequest request, SendHandler handler) {
        try {
            cyclicBarrier.await();
            return handler.sendRequest(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }finally {
            cyclicBarrier.reset();
        }
        return null;
    }

    public void removeServer(String address){
        /**
         * 从已建立连接的缓存中移除
         */
        connectedServers.remove(address);
        /**
         * 再把已缓存的服务移除，避免其继续被负载均衡到
         */
        ServerDiscoveryCache.removeServiceByAddress(address);
    }
}
