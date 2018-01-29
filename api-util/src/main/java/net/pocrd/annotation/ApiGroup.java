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
     */
    int minCode();

    /**
     * 错误码上限
     */
    int maxCode();

    /**
     * ApiGroup名称
     */
    String name();

    /**
     * 错误码定义
     */
    Class<? extends AbstractReturnCode> codeDefine();

    /**
     * ApiGroup负责人
     */
    String owner();
}
