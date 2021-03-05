package cn.ggstd.common.protocol;

import cn.ggstd.annotation.MessageProtocol;
import cn.ggstd.common.constant.Constant;
import cn.ggstd.common.model.http.RpcRequest;
import cn.ggstd.common.model.http.RpcResponse;
import cn.ggstd.util.SerializeUtil;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by lixing on 2021-3-3 上午 9:20.
 */
@MessageProtocol(Constant.KRYO)
public class KryoMessageProtocol implements MessageProtocolPolicy{

    private final ThreadLocal<Kryo> threadLocal = new ThreadLocal<Kryo>(){
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.register(RpcRequest.class);
            kryo.register(RpcResponse.class);
            Kryo.DefaultInstantiatorStrategy strategy = (Kryo.DefaultInstantiatorStrategy)kryo.getInstantiatorStrategy();
            strategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }
    };

    @Override
    public byte[] marshalling(Object obj) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        threadLocal.get().writeClassAndObject(output,obj);
        byte[] bytes = output.toBytes();
        output.flush();
        SerializeUtil.closeOutputStream(output);
        SerializeUtil.closeOutputStream(outputStream);
        return bytes;
    }

    @Override
    public Object unmarshalling(byte[] bytes,Class<?> clazz) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Input input = new Input(byteArrayInputStream);
        Object result = threadLocal.get().readClassAndObject(input);
        SerializeUtil.closeInputStream(input);
        SerializeUtil.closeInputStream(byteArrayInputStream);
        return result;
    }
}
