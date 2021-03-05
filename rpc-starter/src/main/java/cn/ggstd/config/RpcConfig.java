package cn.ggstd.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;

/**
 * Created by lixing on 2021-3-1 下午 6:19.
 */
@ConfigurationProperties(prefix = "cn.ggstd.rpc")
@Data
public class RpcConfig {

    /**
     * 服务注册中心地址
     */
    private String registerAddress = "127.0.0.1:2181";
    /**
     * 服务暴露端口
     */
    private Integer port = 9999;
    /**
     * 序列化协议
     */
    private String protocol = "java";
    /**
     * 负载均衡策略
     */
    private String loadBalance = "full round";
    /**
     * 默认权重
     */
    private Integer weight = 1;
    /**
     * 本机默认地址
     */
    private String host = "127.0.0.1";

}
