package net.pocrd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用枚举来描述某个字符串类型的取值范围
 * 用于向客户端声明该字段可能的取值范围
 * 只能标记在String或数组或List类型的字段上
 * 注意:被描述的字段有可能返回其他值, 请为不被识别的值准备默认行为(例如忽略)
 * Created by rendong on 14/11/3.
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumDef {
    /**
     * 枚举类型定义
     */
    Class<? extends Enum> value();
}
