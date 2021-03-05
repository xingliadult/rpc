package cn.ggstd.common.client.balance;

import cn.ggstd.annotation.LoadBalance;
import cn.ggstd.common.model.service.RpcService;

import java.util.List;

/**
 * Created by lixing on 2021-3-1 上午 11:13.
 * 轮询算法
 */
@LoadBalance("full round")
public class FullRoundBalance implements LoadBalancePolicy {

    private volatile int numIndex;

    @Override
    public RpcService chooseOne(List<RpcService> serviceList) {
        if(numIndex >= serviceList.size()){
            numIndex = 0;
        }
        return serviceList.get(numIndex++);
    }
}
