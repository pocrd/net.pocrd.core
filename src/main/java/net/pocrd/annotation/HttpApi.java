package net.pocrd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.pocrd.define.ApiOpenState;
import net.pocrd.define.SecurityType;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpApi {
    /**
     * Http 接口名
     * @return
     */
    String name();

    /**
     * Http 接口注释
     * @return
     */
    String desc();
    
    /**
     * 调用接口所需的安全级别
     * @return
     */
    SecurityType security();
    
    /**
     * 接口开放状态
     * @return
     */
    ApiOpenState state() default ApiOpenState.OPEN;
}
