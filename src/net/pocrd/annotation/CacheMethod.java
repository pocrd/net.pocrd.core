package net.pocrd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheMethod {
    /**
     * 是否开启缓存
     * @return
     */
    boolean enable();
    
    /**
     * 缓存键
     * @return
     */
    String key();
    
    /**
     * 缓存过期时间
     * @return
     */
    int expire();
    
    /**
     * 本地缓存过期时间，小于等于0时不开启
     * @return
     */
    int localExpire() default 0;
    
    /**
     * 是否开启自动分页缓存
     * @return
     */
    boolean autoPaging() default false;
    
    /**
     * 开启自动分页缓存时的最大缓存页数
     * @return
     */
    int maxPage() default 0;
    
    /**
     * 开启自动分页缓存时每页的记录数
     * @return
     */
    int pageSize() default 0;
}
