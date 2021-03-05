package cn.ggstd.common.client.net.handler;

import cn.ggstd.common.client.net.NettyNetClient;
import cn.ggstd.common.client.net.RpcFuture;
import cn.ggstd.common.constant.Constant;
import cn.ggstd.common.model.http.RpcRequest;
import cn.ggstd.common.model.http.RpcResponse;
import cn.ggstd.common.protocol.MessageProtocolPolicy;
import cn.ggstd.exception.ErrorContext;
import cn.ggstd.exception.ExceptionFactory;
import cn.ggstd.exception.RpcException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by lixing on 2021-3-1 下午 4:52.
 */
@Slf4j
public class SendHandler extends ChannelInboundHandlerAdapter {

    private volatile Channel channel;
    private final String address;
    private final MessageProtocolPolicy protocolPolicy;
    private final CountDownLatch downLatch = new CountDownLatch(1);
    private final ConcurrentMap<String,RpcFuture<RpcResponse>> requestMap = new ConcurrentHashMap<>();
    private final NettyNetClient netClient;

    public SendHandler(String address, MessageProtocolPolicy protocolPolicy, NettyNetClient netClient) {
        this.address = address;
        this.protocolPolicy = protocolPolicy;
        this.netClient = netClient;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.channel = ctx.channel();
        downLatch.countDown();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("connected to server successful:{}",ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.debug("read message:{}",msg);
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        //手动回收
        ReferenceCountUtil.release(byteBuf);
        RpcResponse response = (RpcResponse) protocolPolicy.unmarshalling(bytes,RpcResponse.class);
        RpcFuture<RpcResponse> future = requestMap.get(response.getRequestId());
        future.setResult(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.error("exception occurred:{}",cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.error("channel inactive with remoteAddress:{}",address);
        netClient.removeServer(address);
    }

    public RpcResponse sendRequest(RpcRequest request){
        RpcResponse response = null;
        RpcFuture<RpcResponse> future = new RpcFuture<>();
        requestMap.putIfAbsent(request.getRequestId(),future);
        try {
            byte[] requestBytes = protocolPolicy.marshalling(request);
            ByteBuf requestBuf = Unpooled.buffer(requestBytes.length);
            requestBuf.writeBytes(requestBytes);
            if(downLatch.await(Constant.WAIT_CONNECT_TIME, TimeUnit.SECONDS)){
                channel.writeAndFlush(requestBuf);
                response = future.get(Constant.WAIT_RESPONSE_TIME,TimeUnit.SECONDS);
            }else {
                ExceptionFactory.wrapException("establish channel time out",new RpcException());
            }
        }catch (Exception e){
            ExceptionFactory.wrapException("unexpected error occurred while send request",e);
        }finally {
            requestMap.remove(request.getRequestId());
            ErrorContext.instance().reset();
        }
        return response;
    }

}
