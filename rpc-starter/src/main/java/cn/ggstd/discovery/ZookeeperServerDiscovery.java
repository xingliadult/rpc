package cn.ggstd.discovery;

import cn.ggstd.common.constant.Constant;
import cn.ggstd.common.model.service.RpcService;
import cn.ggstd.common.serializer.ZookeeperSerializer;
import com.alibaba.fastjson.JSONObject;
import org.I0Itec.zkclient.ZkClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by lixing on 2021-3-1 上午 9:49.
 */
public class ZookeeperServerDiscovery implements ServerDiscovery{

    private final ZkClient zkClient;

    public ZookeeperServerDiscovery(String zookeeperAddress) {
        this.zkClient = new ZkClient(zookeeperAddress);
        this.zkClient.setZkSerializer(new ZookeeperSerializer());
    }

    @Override
    public List<RpcService> findListService(String serviceName) {
        String path = Constant.SLASH + Constant.RPC + Constant.SLASH + serviceName + Constant.SLASH + Constant.SERVICE;
        List<String> serviceList = zkClient.getChildren(path);
        return Optional.ofNullable(serviceList).orElse(new ArrayList<>()).stream().map(str -> {
            String url = null;
            try {
                url = URLDecoder.decode(str,Constant.UTF8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return JSONObject.parseObject(url,RpcService.class);
        }).collect(Collectors.toList());
    }

    public void registerZKChild() {
        ServerDiscoveryCache.SERVICE_CLASS_CACHE.forEach(data -> {
            String path = Constant.SLASH + Constant.RPC + Constant.SLASH + data + Constant.SLASH + Constant.SERVICE;
            zkClient.subscribeChildChanges(path,new ZkChildListenerImpl());
        });
    }
}
