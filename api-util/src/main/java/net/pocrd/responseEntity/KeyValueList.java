package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rendong on 14-4-24.
 */
@Description("键值对列表")
public final class KeyValueList implements Serializable {
    private static final long serialVersionUID = 1L;
    @Description("键值对列表")
    public List<KeyValuePair> keyValue;
}
