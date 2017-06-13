package net.pocrd.annotation;

import net.pocrd.define.ApiOpenState;
import net.pocrd.define.SecurityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpApi {
    /**
     * Http 接口名
     */
    String name();

    /**
     * Http 接口注释
     */
    String desc();

    /**
     * Http 接口详细描述
     */
    String detail() default "";

    /**
     * 调用接口所需的安全级别
     */
    SecurityType security();

    /**
     * 接口开放状态
     */
    ApiOpenState state() default ApiOpenState.OPEN;

    /**
     * 接口负责人
     */
    String owner();

    /**
     * SecurityType.Integrated 级别接口是否需要apigw进行签名验证,false:验证由服务提供方完成,true:apigw负责签名验证
     */
    boolean needVerify() default true;
}
