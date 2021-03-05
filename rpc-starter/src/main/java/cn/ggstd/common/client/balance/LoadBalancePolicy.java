package cn.ggstd.common.client.balance;

import cn.ggstd.common.model.service.RpcService;

import java.util.List;

/**
 * Created by lixing on 2021-3-1 上午 11:11.
 */
public interface LoadBalancePolicy {

    RpcService chooseOne(List<RpcService> serviceList);

}
