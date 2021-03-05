package cn.ggstd.common.client.balance;

import cn.ggstd.annotation.LoadBalance;
import cn.ggstd.common.model.service.RpcService;

import java.util.List;
import java.util.Random;

/**
 * Created by lixing on 2021-3-1 上午 11:20.
 */
@LoadBalance("round")
public class RoundBalance implements LoadBalancePolicy {

    private final Random random = new Random();

    @Override
    public RpcService chooseOne(List<RpcService> serviceList) {
        return serviceList.get(random.nextInt(serviceList.size()));
    }
}
