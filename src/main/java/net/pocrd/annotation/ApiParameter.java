package net.pocrd.annotation;

import net.pocrd.define.EnumNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiParameter {
    /**
     * 是否为必要参数
     */
    boolean required();

    /**
     * 是否为rsa加密参数，使用非对称加密
     *
     * @return
     */
    boolean rsaEncrypted() default false;

    /**
     * 参数名称
     */
    String name();

    /**
     * 默认值
     */
    String defaultValue() default "";

    /**
     * 验证参数是否合法的
     */
    String verifyRegex() default "";

    /**
     * 参数验证失败的提示信息
     */
    String verifyMsg() default "";

    /**
     * 由于安全原因需要在日志系统中忽略的参数
     */
    boolean ignoreForSecurity() default false;

    /**
     * 枚举类型定义, 用于
     */
    Class<? extends Enum> enumDef() default EnumNull.class;

    /**
     * 该参数在接口中的次序, 与类型相关. 当前可能的取值有 int0, int1...int9 str0, str1...str9
     * 目前被用在etl处理接口调用日志时按照该顺序放置各个参数
     */
    String sequence() default "";

    /**
     * 参数注释
     */
    String desc();
}
