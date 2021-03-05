package cn.ggstd.server.register;

import cn.ggstd.common.model.service.Service;
import lombok.Data;

/**
 * Created by lixing on 2021-2-25 上午 8:27.
 */
@Data
public class ServiceObject extends Service{

    private Class<?> clazz;//服务对象
    private Object obj;//具体服务

    public ServiceObject() {
    }

    public ServiceObject(String serviceName, Class<?> clazz, Object obj) {
        super.serviceName = serviceName;
        this.clazz = clazz;
        this.obj = obj;
    }
}
