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
     *
     * @return
     */
    String name();

    /**
     * Http 接口注释
     *
     * @return
     */
    String desc();

    /**
     * Http 接口短描述
     *
     * @return
     */
    String detail() default "";

    /**
     * 调用接口所需的安全级别
     *
     * @return
     */
    SecurityType security();

    /**
     * 接口开放状态
     *
     * @return
     */
    ApiOpenState state() default ApiOpenState.OPEN;

    /**
     * 接口负责人
     *
     * @return
     */
    String owner() default "";

    /**
     * Integrated级别接口，需要指定允许访问的第三方编号
     * 第三方集成接口需要明确的指定可以访问该资源的合作方
     *
     * @return
     *
     * @Deprecated 使用diamond进行配置，不由业务方指定
     */
    @Deprecated int[] allowThirdPartyIds() default {};

    /**
     * @return
     *
     * @see SecurityType.Integrated 级别接口是否需要apigw进行签名验证,false:验证由服务提供方完成,true:apigw负责签名验证
     */
    boolean needVerify() default false;
}
