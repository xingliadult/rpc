package cn.ggstd.common.model.http;

import cn.ggstd.common.constant.ResponseStatusEnum;
import lombok.Data;

/**
 * Created by lixing on 2021-2-25 下午 5:56.
 */
@Data
public class RpcResponse extends RpcHttp {

    private Object result;
    private Exception exception;
    private ResponseStatusEnum responseStatus;

}
