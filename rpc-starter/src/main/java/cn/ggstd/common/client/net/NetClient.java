package cn.ggstd.common.client.net;

import cn.ggstd.common.model.http.RpcRequest;
import cn.ggstd.common.model.http.RpcResponse;
import cn.ggstd.common.model.service.RpcService;
import cn.ggstd.common.protocol.MessageProtocolPolicy;

/**
 * Created by lixing on 2021-3-1 下午 3:55.
 */
public interface NetClient {

    RpcResponse sendRequest(RpcRequest request, RpcService service, MessageProtocolPolicy protocolPolicy);

}
