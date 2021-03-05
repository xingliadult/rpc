package cn.ggstd.server.register;

import cn.ggstd.common.constant.Constant;
import cn.ggstd.common.model.service.RpcService;
import cn.ggstd.common.serializer.ZookeeperSerializer;
import com.alibaba.fastjson.JSON;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;

/**
 * Created by lixing on 2021-2-25 上午 9:07.
 */
@Slf4j
public class ZookeeperServerRegister extends BaseServerRegister {

    private final ZkClient zkClient;
    private final String host;

    public ZookeeperServerRegister(String zookeeperAddress, Integer weight, Integer port, String protocol,String host) {
        this.zkClient = new ZkClient(zookeeperAddress);
        this.zkClient.setZkSerializer(new ZookeeperSerializer());
        this.weight = weight;
        this.port = port;
        this.protocol = protocol;
        this.host = host;
    }

    @Override
    public void register(ServiceObject serviceObject) throws Exception {
        super.register(serviceObject);
        String address = StringUtil.isNullOrEmpty(this.host)?InetAddress.getLocalHost().getHostAddress():this.host
                + Constant.COLON + this.port;
        super.setServiceName(serviceObject.getServiceName());
        super.setAddress(address);
        registerIntoZookeeper();
    }

    private void registerIntoZookeeper() {

        String serviceName = super.getServiceName();
        String uri = JSON.toJSONString(this);
        try {
            uri = URLEncoder.encode(uri,Constant.UTF8);
        }catch (UnsupportedEncodingException e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
        String servicePath = Constant.SLASH + Constant.RPC + Constant.SLASH + serviceName + Constant.SLASH + Constant.SERVICE;
        if(!zkClient.exists(servicePath)){
            //创建节点
            zkClient.createPersistent(servicePath,true);
        }
        String uriPath = servicePath + Constant.SLASH + uri;
        /*if(zkClient.exists(uriPath)){
        }*/
        zkClient.delete(uriPath);
        //创建临时节点（会话失效即被清理）
        zkClient.createEphemeral(uriPath);
    }
}
