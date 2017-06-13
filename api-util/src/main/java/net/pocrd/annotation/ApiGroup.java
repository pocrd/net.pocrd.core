package net.pocrd.annotation;

import net.pocrd.entity.AbstractReturnCode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiGroup {
    /**
     * 错误码下限
     *
     * @return
     */
    int minCode();

    /**
     * 错误码上限
     *
     * @return
     */
    int maxCode();

    /**
     * ApiGroup名称
     *
     * @return
     */
    String name();

    /**
     * 错误码定义
     *
     * @return
     */
    Class<? extends AbstractReturnCode> codeDefine();

    /**
     * ApiGroup负责人
     *
     * @return
     */
    String owner() default "";
}
