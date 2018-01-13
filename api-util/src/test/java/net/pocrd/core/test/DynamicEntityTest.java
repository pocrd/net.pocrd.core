package net.pocrd.core.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import net.pocrd.annotation.Description;
import net.pocrd.annotation.DynamicStructure;
import net.pocrd.responseEntity.DynamicEntity;
import net.pocrd.responseEntity.KeyValueList;
import net.pocrd.responseEntity.KeyValuePair;
import net.pocrd.util.POJOSerializerProvider;
import net.pocrd.util.TypeCheckUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rendong on 2017/7/27.
 */
public class DynamicEntityTest {
    @Description("simple test entity")
    public static class SimpleTestEntity implements Serializable {
        @Description("string value")
        public String strValue;
        @Description("int array")
        public int[]  intArray;
    }

    @Description("测试bad response")
    public static class BadResponse implements Serializable {
        @Description("str")
        public String str;

        public BadResponse(String str) {
            this.str = str;
        }
    }

    @Description("ComplexTestEntity")
    public static class ComplexTestEntity implements Serializable {
        @Description("strValue")
        public String                 strValue;
        @Description("shortValue")
        public short                  shortValue;
        @Description("byteValue")
        public byte                   byteValue;
        @Description("doubleValue")
        public double                 doubleValue;
        @Description("floatValue")
        public float                  floatValue;
        @Description("boolValue")
        public boolean                boolValue;
        @Description("intValue")
        public int                    intValue;
        @Description("longValue")
        public long                   longValue;
        @Description("charValue")
        public char                   charValue;
        @Description("SimpleTestEntity List")
        public List<SimpleTestEntity> simpleTestEntityList;
        @Description("simpleTestEntity")
        public SimpleTestEntity       simpleTestEntity;
        @Description("dynamic entity")
        @DynamicStructure({ SimpleTestEntity.class, KeyValueList.class })
        public DynamicEntity          dynamicEntity;
        @Description("dynamic entity list")
        @DynamicStructure({ SimpleTestEntity.class, BadResponse.class })
        public List<DynamicEntity>    dynamicEntityList;
    }

    public static class CList implements Serializable {
        public String                  name;
        public List<ComplexTestEntity> list;
    }

    @Test
    public void testDynamicEntityUndeclear() {
        ComplexTestEntity e = new ComplexTestEntity();
        e.strValue = "...";
        KeyValueList ste = new KeyValueList();
        ste.keyValue = new ArrayList<KeyValuePair>(3);
        ste.keyValue.add(new KeyValuePair("a", "b"));
        ste.keyValue.add(new KeyValuePair("c", "d"));
        e.dynamicEntity = new DynamicEntity<KeyValueList>(ste);
        List<DynamicEntity> des = new ArrayList<DynamicEntity>(3);
        {
            SimpleTestEntity s = new SimpleTestEntity();
            s.intArray = new int[] { 4, 1, 4 };
            s.strValue = "kkkkkk";
            DynamicEntity de1 = new DynamicEntity(s);
            des.add(de1);

            BadResponse b = new BadResponse("nonono");
            DynamicEntity de2 = new DynamicEntity(b);
            des.add(de2);

            KeyValueList kvl = new KeyValueList();
            kvl.keyValue = new ArrayList<KeyValuePair>(2);
            kvl.keyValue.add(new KeyValuePair("x", "y"));
            kvl.keyValue.add(new KeyValuePair("n", "b"));
            DynamicEntity de3 = new DynamicEntity(kvl);
            des.add(de3);
        }
        e.dynamicEntityList = des;
        SerializeConfig.getGlobalInstance().addFilter(ComplexTestEntity.class, TypeCheckUtil.DynamicEntityDeclearChecker);
        try {
            JSON.toJSONString(e);
            Assert.fail("should error!");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Assert.assertTrue(ex.getMessage()
                    .equals("net.pocrd.responseEntity.KeyValueList not declear on net.pocrd.core.test.DynamicEntityTest$ComplexTestEntity  dynamicEntityList"));
        }
    }

    @Test
    public void testDynamicEntity() {
        ComplexTestEntity e = new ComplexTestEntity();
        e.strValue = "...";
        KeyValueList ste = new KeyValueList();
        ste.keyValue = new ArrayList<KeyValuePair>(3);
        ste.keyValue.add(new KeyValuePair("a", "b"));
        ste.keyValue.add(new KeyValuePair("c", "d"));
        e.dynamicEntity = new DynamicEntity<KeyValueList>(ste);
        List<DynamicEntity> des = new ArrayList<DynamicEntity>(3);
        {
            SimpleTestEntity s = new SimpleTestEntity();
            s.intArray = new int[] { 4, 1, 4 };
            s.strValue = "kkkkkk";
            DynamicEntity de1 = new DynamicEntity(s);
            des.add(de1);

            BadResponse b = new BadResponse("nonono");
            DynamicEntity de2 = new DynamicEntity(b);
            des.add(de2);
        }
        e.dynamicEntityList = des;

        SerializeConfig.getGlobalInstance().addFilter(ComplexTestEntity.class, TypeCheckUtil.DynamicEntityDeclearChecker);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        POJOSerializerProvider.getSerializer(e.getClass()).toJson(e, baos, true);
        System.out.println(baos.toString());
    }

    @Test
    public void testCList() {
        CList list = new CList();
        ComplexTestEntity e = new ComplexTestEntity();
        e.strValue = "...";

        KeyValueList ste = new KeyValueList();
        ste.keyValue = new ArrayList<KeyValuePair>(3);
        ste.keyValue.add(new KeyValuePair("a", "b"));
        ste.keyValue.add(new KeyValuePair("c", "d"));
        e.dynamicEntity = new DynamicEntity<KeyValueList>(ste);
        List<DynamicEntity> des = new ArrayList<DynamicEntity>(3);
        {
            SimpleTestEntity s = new SimpleTestEntity();
            s.intArray = new int[] { 4, 1, 4 };
            s.strValue = "kkkkkk";
            DynamicEntity de1 = new DynamicEntity(s);
            des.add(de1);

            BadResponse b = new BadResponse("nonono");
            DynamicEntity de2 = new DynamicEntity(b);
            des.add(de2);
        }
        e.dynamicEntityList = des;

        list.list = new ArrayList<>();
        list.list.add(e);
        list.name = "hello";

        SerializeConfig.getGlobalInstance().addFilter(ComplexTestEntity.class, TypeCheckUtil.DynamicEntityDeclearChecker);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        POJOSerializerProvider.getSerializer(list.getClass()).toJson(list, baos, true);
        System.out.println(baos.toString());

    }

    @Description("包含string的dynamic entity")
    public static class DynamicEntityWithString implements Serializable {
        @Description("str")
        @DynamicStructure(String.class)
        public DynamicEntity<String> str;

    }

    @Test
    public void testDynamicEntityWithString() {
        DynamicEntityWithString e = new DynamicEntityWithString();
        e.str = new DynamicEntity<>("x");
        try {
            TypeCheckUtil.recursiveCheckReturnType("test", e.getClass(), null);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            Assert.assertTrue(ex.getMessage()
                    .equals("cannot use java.lang.String as a DynamicStructure. net.pocrd.core.test.DynamicEntityTest$DynamicEntityWithString str. Please create a customize POJO."));
        }
    }
}
