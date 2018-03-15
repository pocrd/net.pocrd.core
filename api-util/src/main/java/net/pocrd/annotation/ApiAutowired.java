package net.pocrd.annotation;

import net.pocrd.define.AutowireableParameter;
import net.pocrd.define.ServiceInjectable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by rendong on 14-4-24.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiAutowired {
    AutowireableParameter value();

    /**
     * 不为默认值时表明该参数接受服务端注入
     * 注入的参数名即为serviceInject的值
     */
    Class<? extends ServiceInjectable> serviceInject() default ServiceInjectable.class;
}
