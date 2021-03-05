package cn.ggstd.annotation;

import java.lang.annotation.*;

/**
 * Created by lixing on 2021-2-25 上午 8:18.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcServiceAno {
    String value() default "";
}
