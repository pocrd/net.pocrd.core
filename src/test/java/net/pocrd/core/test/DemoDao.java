package net.pocrd.core.test;

import java.util.List;

import net.pocrd.annotation.CacheMethod;
import net.pocrd.annotation.CacheParameter;
import net.pocrd.annotation.CacheParameter.CacheKeyType;

public class DemoDao {
    /**
     * 继承
     * 
     * @param condition
     * @param condition2
     * @param conditionBool
     * @param condtionByte
     * @param condtionShort
     * @param condtionChar
     * @param condtionLong
     * @param condtionFloat
     * @param condtionDouble
     * @param conditionInteger
     * @param conditionList
     * @return
     */
    @CacheMethod(enable = true, expire = 100, key = "getDemoEntity")
    public DemoEntity getDemoEntity(@CacheParameter(type = CacheKeyType.Normal) int condition,
            @CacheParameter(type = CacheKeyType.Normal) String condition2, @CacheParameter(type = CacheKeyType.Normal) boolean conditionBool,
            @CacheParameter(type = CacheKeyType.Normal) byte condtionByte, @CacheParameter(type = CacheKeyType.Normal) short condtionShort,
            @CacheParameter(type = CacheKeyType.Normal) char condtionChar, @CacheParameter(type = CacheKeyType.Normal) long condtionLong,
            @CacheParameter(type = CacheKeyType.Normal) float condtionFloat, @CacheParameter(type = CacheKeyType.Normal) double condtionDouble) {
        if (condition == 0) {
            DemoEntity entity = new DemoEntity();
            entity.setParam1(1);
            entity.setParam2("I am demo");
            // System.out.println("Success 1");
            return entity;
        }
        return null;
    }

    @CacheMethod(enable = true, expire = 100, key = "getDemoEntity")
    public int getDemoEntity(@CacheParameter(type = CacheKeyType.Normal) int condition) throws Exception {
        // System.out.println("Success 5");
        if (condition == 1000)
            throw new Exception();
        else return 0;
    }

    /**
     * @param condition
     */
    @CacheMethod(enable = true, expire = 100, key = "getDemoEntity")
    public int[] getDemoEntity(@CacheParameter(type = CacheKeyType.Normal) int[] condition,
            @CacheParameter(type = CacheKeyType.Normal) DemoEntity[] obj, @CacheParameter(type = CacheKeyType.Normal) TestEnum test,
            @CacheParameter(type = CacheKeyType.Normal) int valueType) {
        // System.out.println("Success 3");
        return null;
    }

    /**
     * @param condition
     * @param obj
     * @param test
     * @param valueType
     * @return
     */
    @CacheMethod(enable = false, expire = 100, key = "getDemoEntity")
    public int[] getDemoEntity(@CacheParameter(type = CacheKeyType.Normal) double conditionDouble,
            @CacheParameter(type = CacheKeyType.Normal) long conditionlong) {
        // System.out.println("Success 4");
        return null;
    }

    /**
     * @param condition
     */
    @CacheMethod(enable = true, expire = 100, key = "getDemoEntity")
    public List<String> getDemoEntity(@CacheParameter(type = CacheKeyType.Normal) List<String> condition) {
        // System.out.println("Success 5");
        return null;
    }

    public enum TestEnum {
        a, b
    }

}
