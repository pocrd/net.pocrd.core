package net.pocrd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheParameter {
    CacheKeyType type() default CacheKeyType.Normal;
    public static enum CacheKeyType {
        Normal, PageIndex, PageSize, Filter
    }
}
