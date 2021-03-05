package cn.ggstd.common.protocol;

/**
 * Created by lixing on 2021-2-25 下午 6:06.
 * 定义编组、解组，序列化协议
 */
public interface MessageProtocolPolicy {

    byte[] marshalling(Object obj) throws Exception;
    Object unmarshalling(byte[] bytes,Class<?> clazz) throws Exception;

}
