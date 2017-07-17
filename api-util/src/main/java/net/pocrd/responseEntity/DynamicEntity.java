package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;

/**
 * Created by rendong on 2017/7/17.
 */
public class DynamicEntity<T extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Description("动态类型对象")
    public T entity;

    @Description("动态类型名")
    public String typeName;

    public DynamicEntity() {
    }

    public DynamicEntity(T entity) {
        if (entity != null) {
            this.entity = entity;
            typeName = entity.getClass().getSimpleName();
        }
    }
}
