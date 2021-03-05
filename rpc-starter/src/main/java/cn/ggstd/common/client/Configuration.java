package cn.ggstd.common.client;

import cn.ggstd.common.client.balance.LoadBalancePolicy;
import cn.ggstd.common.client.net.NetClient;
import cn.ggstd.common.model.service.RpcService;
import cn.ggstd.common.protocol.MessageProtocolPolicy;
import cn.ggstd.discovery.ServerDiscovery;
import cn.ggstd.discovery.ServerDiscoveryCache;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by lixing on 2021-3-1 下午 3:49.
 */
@Data
public class Configuration {

    private final LoadBalancePolicy loadBalance;
    private final Map<String,MessageProtocolPolicy> supportProtocolPolicy;
    private final NetClient netClient;
    private final ServerDiscovery serverDiscovery;

    public RpcService getRpcService(String serviceName){
        List<RpcService> serviceList = ServerDiscoveryCache.getService(serviceName);
        if(!serviceList.isEmpty()) {
            return loadBalance.chooseOne(serviceList);
        }else {
            serviceList = serverDiscovery.findListService(serviceName);
            if (!serviceList.isEmpty()) {
                ServerDiscoveryCache.putService(serviceName,serviceList);
                return loadBalance.chooseOne(serviceList);
            }
        }
        return null;
    }

    public Configuration(LoadBalancePolicy loadBalance, Map<String,MessageProtocolPolicy> supportProtocolPolicy, NetClient netClient, ServerDiscovery serverDiscovery) {
        this.loadBalance = loadBalance;
        this.supportProtocolPolicy = supportProtocolPolicy;
        this.netClient = netClient;
        this.serverDiscovery = serverDiscovery;
    }

    public MessageProtocolPolicy getProtocolPolicy(String protocol) {
        return this.supportProtocolPolicy.get(protocol);
    }
}
