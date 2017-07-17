package net.pocrd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by rendong on 2017/7/17.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorCodeMapping {
    int[] mapping();

    int[] mapping1() default {};

    int[] mapping2() default {};

    int[] mapping3() default {};

    int[] mapping4() default {};

    int[] mapping5() default {};

    int[] mapping6() default {};

    int[] mapping7() default {};

    int[] mapping8() default {};

    int[] mapping9() default {};
}
