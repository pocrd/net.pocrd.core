package net.pocrd.annotation;

import net.pocrd.define.ServiceInjectable;

import java.lang.annotation.*;

/**
 * 用于描述该接口返回时输出给其他接口隐式注入的参数列表
 * 目前参数值仅支持半角逗号分隔的字符串
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ParamsExport.class)
public @interface ParamExport {
    String name();

    Class<? extends ServiceInjectable.InjectionData> dataType();
}