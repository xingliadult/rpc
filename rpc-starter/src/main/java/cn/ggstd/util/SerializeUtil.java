package cn.ggstd.util;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import java.io.*;

/**
 * Created by lixing on 2021-2-25 下午 6:14.
 */
public class SerializeUtil {

    /**
     * java序列化
     */
    public static byte[] serialize(Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(byteArrayOutputStream);
            outputStream.writeObject(obj);
            outputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            closeOutputStream(byteArrayOutputStream);
            closeOutputStream(outputStream);
        }
        return null;
    }

    /**
     * java反序列化
     */
    public static Object deserialize(byte[] bytes){
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream inputStream = null;
        try {
            inputStream = new ObjectInputStream(byteArrayInputStream);
            return inputStream.readObject();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            closeInputStream(byteArrayInputStream);
            closeInputStream(inputStream);
        }
        return null;
    }

    /**
     * 基于proto stuff的序列化
     */
    public static <T> byte[] protoStuffSerialize(T obj){
        Schema<T> schema = RuntimeSchema.getSchema((Class<T>) obj.getClass());
        LinkedBuffer linkedBuffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        byte[] result;
        try {
            result = ProtobufIOUtil.toByteArray(obj,schema,linkedBuffer);
        }finally {
            linkedBuffer.clear();
        }
        return result;
    }

    /**
     * 基于proto stuff的反序列化
     */
    public static <T> T protoStuffDeserialize(byte[] bytes,Class<T> clazz){
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T result = schema.newMessage();
        ProtobufIOUtil.mergeFrom(bytes,result,schema);
        return result;
    }

    public static void closeInputStream(InputStream inputStream){
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeOutputStream(OutputStream outputStream){
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
