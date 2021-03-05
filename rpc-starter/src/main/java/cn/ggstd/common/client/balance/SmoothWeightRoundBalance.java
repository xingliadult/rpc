package cn.ggstd.common.client.balance;

import cn.ggstd.annotation.LoadBalance;
import cn.ggstd.common.model.service.RpcService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lixing on 2021-3-1 上午 11:23.
 * 平滑加权轮询
 */
@LoadBalance("smooth weight round")
public class SmoothWeightRoundBalance implements LoadBalancePolicy {

    private static final Map<String,Integer> SERVICE_MAP = new HashMap<>();

    @Override
    public RpcService chooseOne(List<RpcService> serviceList) {
        serviceList.forEach(data -> {
            SERVICE_MAP.putIfAbsent(data.toString(),data.getWeight());
        });
        Integer totalWeight = serviceList.stream().mapToInt(RpcService::getWeight).sum();

        RpcService resultService = null;
        for(RpcService service : serviceList) {
            if(resultService == null||resultService.getWeight() < service.getWeight()){
                resultService = service;
            }
        }

        assert resultService != null;

        SERVICE_MAP.put(resultService.toString(),resultService.getWeight() - totalWeight);

        serviceList.forEach(data -> {
            Integer currentWeight = SERVICE_MAP.get(data.toString());
            SERVICE_MAP.put(data.toString(),currentWeight + data.getWeight());
        });

        return resultService;
    }
}
