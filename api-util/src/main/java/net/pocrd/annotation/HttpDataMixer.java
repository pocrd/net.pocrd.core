package net.pocrd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by rendong on 2018/4/20.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpDataMixer {
    /**
     * 数据混合器名称
     */
    String name();

    /**
     * 数据混合器描述
     */
    String desc();

    /**
     * 接口负责人
     */
    String owner();

    /**
     * 使用这个数据混合器的页面路径, 例如  /orderlist.html
     */
    String pagePath();
}
