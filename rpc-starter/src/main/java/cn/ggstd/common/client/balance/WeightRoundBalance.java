package cn.ggstd.common.client.balance;

import cn.ggstd.annotation.LoadBalance;
import cn.ggstd.common.model.service.RpcService;

import java.util.List;

/**
 * Created by lixing on 2021-3-1 下午 2:17.
 * 权重轮询
 */
@LoadBalance("weight round")
public class WeightRoundBalance implements LoadBalancePolicy {

    private static volatile int index;

    @Override
    public RpcService chooseOne(List<RpcService> serviceList) {
        Integer totalWeight = serviceList.stream().mapToInt(RpcService::getWeight).sum();
        int num = (index++) % totalWeight;
        for(RpcService service:serviceList){
            if(service.getWeight() > num){
                return service;
            }
            num -= service.getWeight();
        }
        return null;
    }

    public static void main(String[] args) {
        int num = 8 % 9;
        System.out.println(index++);
        System.out.println(num);
    }
}
