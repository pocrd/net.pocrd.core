package net.pocrd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于描述该接口返回时输出给其他接口隐式注入的参数列表
 * 目前参数值仅支持半角逗号分隔的字符串
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamExport {
    String[] value();
}
