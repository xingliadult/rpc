package cn.ggstd.server;

import cn.ggstd.common.constant.ResponseStatusEnum;
import cn.ggstd.common.model.http.RpcRequest;
import cn.ggstd.common.model.http.RpcResponse;
import cn.ggstd.common.protocol.MessageProtocolPolicy;
import cn.ggstd.server.register.ServerRegister;
import cn.ggstd.server.register.ServiceObject;

import java.lang.reflect.Method;

/**
 * Created by lixing on 2021-2-25 上午 11:17.
 */
public class RequestHandler {

    private final ServerRegister serverRegister;
    private final MessageProtocolPolicy messageProtocolPolicy;

    public RequestHandler(ServerRegister serverRegister, MessageProtocolPolicy messageProtocolPolicy) {
        this.serverRegister = serverRegister;
        this.messageProtocolPolicy = messageProtocolPolicy;
    }

    public byte[] handleRequest(byte[] bytes) throws Exception{
        RpcResponse response = new RpcResponse();
        RpcRequest request = (RpcRequest) messageProtocolPolicy.unmarshalling(bytes,RpcRequest.class);
        ServiceObject serviceObject = serverRegister.getService(request.getServiceName());
        if(serviceObject == null){
            response.setResponseStatus(ResponseStatusEnum.NOT_FOUND);
        }else{
            try {
                response.setRequestId(request.getRequestId());
                Method method = serviceObject.getClazz().getMethod(request.getMethod(),request.getParameterType());
                Object result = method.invoke(serviceObject.getObj(),request.getParameter());
                response.setResult(result);
                response.setResponseStatus(ResponseStatusEnum.SUCCESS);
            }catch (Exception e){
                response.setResponseStatus(ResponseStatusEnum.SYSTEM_ERROR);
                response.setException(e);
            }
        }
        return messageProtocolPolicy.marshalling(response);
    }
}
