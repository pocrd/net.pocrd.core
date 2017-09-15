package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;

/**
 * Created by rendong on 2017/7/17.
 */
@Description("用于作为动态类型容器在接口返回值中返回指定的动态类型(配合DynamicStruct标注)")
public final class DynamicEntity<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Description("动态类型对象")
    public final T entity;

    /**
     * 不需要设置任何值, 框架会根据 entity 的具体类型来赋值
     */
    @Description("动态类型名")
    public final String typeName;

    public DynamicEntity(T value) {
        entity = value;
        if (value != null) {
            typeName = value.getClass().getSimpleName();
        } else {
            typeName = null;
        }
    }
}
