package net.pocrd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于将内部系统的异常码转换成当前系统的异常码
 * 格式为 {当前系统异常码, 内部异常码}
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

    int[] mapping10() default {};

    int[] mapping11() default {};

    int[] mapping12() default {};

    int[] mapping13() default {};

    int[] mapping14() default {};

    int[] mapping15() default {};

    int[] mapping16() default {};

    int[] mapping17() default {};

    int[] mapping18() default {};

    int[] mapping19() default {};
}
