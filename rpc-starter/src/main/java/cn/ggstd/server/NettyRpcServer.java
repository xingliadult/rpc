package cn.ggstd.server;

import cn.ggstd.exception.ErrorContext;
import cn.ggstd.exception.ExceptionFactory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by lixing on 2021-2-25 上午 8:25.
 */
@Slf4j
public class NettyRpcServer extends RpcServer{

    private Channel channel;
    private final ExecutorService pool = new ThreadPoolExecutor(10,20,300, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000), new ThreadFactoryBuilder().setNameFormat("rpcServer-%d").build());

    public NettyRpcServer(String protocol, Integer port, RequestHandler requestHandler) {
        super(protocol, port, requestHandler);
    }

    @Override
    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup(10);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ChannelRequestHandler());
                        }
                    });
            //启动服务
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            log.info("server started successfully");
            channel = channelFuture.channel();
            //等待服务通道关闭
            channel.closeFuture().sync();
            //释放线程组资源
            boss.shutdownGracefully();
            workGroup.shutdownGracefully();
        }catch (Exception e){
            throw ExceptionFactory.wrapException("start netty server failed,cause: " + e,e);
        }finally {
            ErrorContext.instance().reset();
        }
    }

    @Override
    public void stop() {
        channel.close();
    }

    public class ChannelRequestHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.info("Channel active :{}",ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            pool.submit(() -> {
                try {
                    log.debug("server receive message {}",msg);
                    ByteBuf byteBuf = (ByteBuf) msg;
                    byte[] bytes = new byte[byteBuf.readableBytes()];
                    //消息写入
                    byteBuf.readBytes(bytes);
                    //手动回收
                    ReferenceCountUtil.release(byteBuf);
                    byte[] respData = requestHandler.handleRequest(bytes);
                    ByteBuf respBuf = Unpooled.buffer(respData.length);
                    respBuf.writeBytes(respData);
                    log.debug("send response {}",respBuf);
                    ctx.writeAndFlush(respBuf);
                }catch (Exception e){
                    throw ExceptionFactory.wrapException("server read message error,cause: " + e,e);
                }finally {
                    ErrorContext.instance().reset();
                }
            });
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            log.error("Exception occurred:{}",cause.getMessage());
            ctx.close();
        }
    }

}
