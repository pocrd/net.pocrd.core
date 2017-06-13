package net.pocrd.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by rendong on 16/8/12.
 */

/**
 * Designed Parameter 用来定义业务执行需要的一些随机值或当前时间值
 * 在业务逻辑中通过 System.currentTimeMillis() 或 Math.random()
 * 等方法获取的值参与业务计算会导致该业务逻辑无法被重入。
 * 因此会导致自动化测试用例编写的困难, 通过在接口注册时声明参数为
 * DesignedParameter 并由自定义的 ParamCreator 来产生所需的参数,
 * 使得业务逻辑本身恢复到可重入状态。
 * 参数值使用String来进行描述, 非基本类型参数值由fastjson进行序列化
 * 后生成String值返回。
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface DesignedParameter {
    Class<?> value();
}
