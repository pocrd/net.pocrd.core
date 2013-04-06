package net.pocrd.entity;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 标示和记录服务端功能点，在有限范围内实现对老版本客户端的兼容 对于不再需要兼容老版本的功能点，应该将其移除
 * 
 * @author rendong
 */
public class BaseFunctionFlag {
    private static HashMap<String, BaseFunctionFlag> map = new HashMap<String, BaseFunctionFlag>(10);

    private String                                   name;

    public static BaseFunctionFlag findFlag(String name) {
        return map.get(name);
    }

    protected BaseFunctionFlag(String name) {
        this.name = name;
        if (map.containsKey(name)) {
            throw new RuntimeException("ambiguous name definition. " + name);
        }
        map.put(name, this);
    }

    public boolean supportFunction(HashSet<String> functionFlags) {
        return functionFlags != null && functionFlags.contains(name);
    }
}
