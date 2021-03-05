package cn.ggstd.common.protocol;

import cn.ggstd.annotation.MessageProtocol;
import cn.ggstd.common.constant.Constant;
import cn.ggstd.util.SerializeUtil;

/**
 * Created by lixing on 2021-3-3 上午 10:26.
 */
@MessageProtocol(Constant.PROTO_STUFF)
public class ProtoStuffMessageProtocol implements MessageProtocolPolicy{
    @Override
    public byte[] marshalling(Object obj) throws Exception {
        return SerializeUtil.protoStuffSerialize(obj);
    }

    @Override
    public Object unmarshalling(byte[] bytes,Class<?> clazz) throws Exception {
        return SerializeUtil.protoStuffDeserialize(bytes,clazz);
    }
}
