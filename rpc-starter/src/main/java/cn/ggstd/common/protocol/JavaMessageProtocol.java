package cn.ggstd.common.protocol;

import cn.ggstd.annotation.MessageProtocol;
import cn.ggstd.common.constant.Constant;
import cn.ggstd.util.SerializeUtil;

/**
 * Created by lixing on 2021-2-25 下午 6:13.
 * java序列化协议
 */
@MessageProtocol(Constant.JAVA)
public class JavaMessageProtocol implements MessageProtocolPolicy {

    @Override
    public byte[] marshalling(Object obj) throws Exception {
        return SerializeUtil.serialize(obj);
    }

    @Override
    public Object unmarshalling(byte[] bytes,Class<?> clazz) throws Exception {
        return SerializeUtil.deserialize(bytes);
    }
}
