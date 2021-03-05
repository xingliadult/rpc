package cn.ggstd.discovery;

import cn.ggstd.common.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;

import java.util.List;

/**
 * Created by lixing on 2021-3-1 上午 10:28.
 */
@Slf4j
public class ZkChildListenerImpl implements IZkChildListener{
    @Override
    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
        log.info("zookeeper child change,parentPath:[{}] -- childList:[{}]",parentPath,currentChilds);
        ServerDiscoveryCache.removeService(parentPath.split(Constant.SLASH)[2]);
    }
}
