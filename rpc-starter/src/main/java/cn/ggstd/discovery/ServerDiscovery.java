package cn.ggstd.discovery;

import cn.ggstd.common.model.service.RpcService;

import java.util.List;

/**
 * Created by lixing on 2021-2-26 下午 4:01.
 */
public interface ServerDiscovery {

    List<RpcService> findListService(String serviceName);

}
