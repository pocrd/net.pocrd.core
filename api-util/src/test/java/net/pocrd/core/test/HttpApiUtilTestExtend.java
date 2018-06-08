package net.pocrd.core.test;

import net.pocrd.annotation.*;
import net.pocrd.core.ApiManager;
import net.pocrd.core.test.HttpApiUtilTest.RC;
import net.pocrd.define.SecurityType;
import org.junit.Test;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@ApiGroup(name = "test", minCode = 0, maxCode = 100, codeDefine = RC.class, owner = "guankaiqiang")
public class HttpApiUtilTestExtend {
    @Description("test")
    public static class Entity implements Serializable {
        @Description("string")
        public  String     str;
        @Description("entity1")
        public  Entity1    entity1;
        @Description("enum1")
        public  Enum       anEnum;
        @Description("enumList")
        public  List<Enum> enumList;
        @EnumDef(Enum.class)
        @Description("stringAsEnum")
        public  String     sEnum;
        @Description("private int")
        private int        intValue;
    }

    @Description("Entity2")
    public static class Entity2 implements Serializable {
        @Description("EntityList")
        public Collection<Entity1> entityList;
    }

    @Description("测试Enum")
    public static enum Enum {
        @Description("AA")
        A,
        @Description("BB")
        B,
        @Description("CC")
        C;
    }

    @Description("test1")
    public static class Entity1 implements Serializable {
        @Description("string1")
        public String str;
    }

    @HttpApi(name = "test.test1", desc = "测试1", security = SecurityType.None, owner = "guankaiqiang")
    public int test1(
            @ApiParameter(required = false, name = "str1", desc = "testtest")
                    String str1,
            @ApiParameter(required = false, name = "str2", defaultValue = "", desc = "testtest")
                    String str2,
            @ApiParameter(required = false, name = "str3", defaultValue = "a", desc = "testtest")
                    String str3,
            @ApiParameter(required = false, defaultValue = "{\"str\":\"abcde\"}", name = "test", desc = "testtest")
                    Entity entity,
            @ApiParameter(required = false, defaultValue = "[{\"str\":\"abcde\"}]", name = "test3", desc = "testtest")
                    Entity1[] entity3,
            @ApiParameter(required = false, defaultValue = "[1, 2]", name = "test4", desc = "testtest")
                    int[] intArrray,
            @ApiParameter(required = false, defaultValue = "[\"str1\", \"str2\"]", name = "test5", desc = "testtest")
                    String[] strArray,
            @ApiParameter(required = false, defaultValue = "[\"A\", \"C\"]", name = "eArray", desc = "testtest", enumDef = Enum.class)
                    String[] eArray,
            @ApiParameter(required = false, defaultValue = "[\"A\"]", name = "enumArray", desc = "testtest")
                    Enum[] enumArray,
            @ApiParameter(required = false, defaultValue = "[\"C\",\"C\"]", name = "senumList", desc = "testtest", enumDef = Enum.class)
                    List<String> senumList,
            @ApiParameter(required = false, defaultValue = "[\"A\"]", name = "enumList", desc = "testtest")
                    List<Enum> enumList,
            @ApiParameter(required = false, defaultValue = "A", name = "enumenum", desc = "enumenum")
                    Enum enumEnum,
            @ApiParameter(required = false, defaultValue = "C", name = "senum", desc = "senum", enumDef = Enum.class)
                    String senum) {
        assertNull(str1);
        assertNull(str2);
        assertEquals("a", str3);
        assertEquals("abcde", entity.str);
        assertNull(entity.anEnum);
        assertEquals("abcde", entity3[0].str);
        assertEquals(2, intArrray[1]);
        assertEquals("str2", strArray[1]);
        assertEquals("C", eArray[1]);
        assertEquals("C", senumList.get(1));
        assertEquals(Enum.A, enumArray[0]);
        assertEquals(Enum.A, enumList.get(0));
        assertEquals(Enum.A, enumEnum);
        assertEquals("C", senum);
        return 0;
    }

    //    @HttpApi(name = "test.test2", desc = "测试2,Collection不支持", security = SecurityType.None)
    //    public int test2(
    //            @ApiParameter(required = true, name = "enumCollection", desc = "testtest")
    //            Collection<Enum> enumCollection) {
    //        return 0;
    //    }

    @HttpApi(name = "test.test3", desc = "测试3", security = SecurityType.None, owner = "guankaiqiang")
    public int test3(
            @ApiParameter(required = true, name = "entity2", desc = "testtest")
                    Entity2 entity2) {
        String str = entity2.entityList.iterator().next().str;
        System.out.println(str);
        assertEquals(str, "abc");
        return 0;
    }

    @HttpApi(name = "test.test4", desc = "测试4", security = SecurityType.None, owner = "guankaiqiang")
    public int test4(
            @ApiParameter(required = true, name = "entity1", desc = "testtest")
                    List<Entity2> entity2List,
            @ApiParameter(required = true, name = "entity2", desc = "testtest")
                    Entity2[] entity2Array) {
        String str = entity2List.get(0).entityList.iterator().next().str;
        System.out.println(str);
        assertEquals(str, "abc");
        str = entity2Array[0].entityList.iterator().next().str;
        System.out.println(str);
        assertEquals(str, "abc");
        return 0;
    }

    @HttpApi(name = "test.test5", desc = "测试5", security = SecurityType.None, owner = "guankaiqiang")
    public int test5(
            @ApiParameter(required = true, name = "entity1", desc = "testtest")
                    List<Enum> enums) {
        System.out.println(enums.get(0).toString());
        assertEquals(enums.get(0), Enum.A);
        return 0;
    }

    @Test
    public void test1Test() {
        ApiManager manager = new ApiManager();
        manager.register(ApiManager.parseApi(HttpApiUtilTestExtend.class), new HttpApiUtilTestExtend());
        manager.processRequest("test.test1", new String[13]);
    }

    //    @Test
    //    public void test2Test() {
    //        try {
    //            ApiManager manager = new ApiManager();
    //            manager.register(ApiManager.parseApi(HttpApiUtilTestExtend.class), new HttpApiUtilTestExtend());
    //        } catch (RuntimeException e) {
    //            assertTrue(e.getMessage().contains("only list is support when using collection"));
    //        }
    //    }

    @Test
    public void test3Test() {
        ApiManager manager = new ApiManager();
        manager.register(ApiManager.parseApi(HttpApiUtilTestExtend.class), new HttpApiUtilTestExtend());
        manager.processRequest("test.test3", new String[] { "{\"entityList\":[{\"str\":\"abc\"}]}" });
    }

    @Test
    public void test4Test() {
        ApiManager manager = new ApiManager();
        manager.register(ApiManager.parseApi(HttpApiUtilTestExtend.class), new HttpApiUtilTestExtend());
        manager.processRequest("test.test4", new String[] { "[{\"entityList\":[{\"str\":\"abc\"}]}]", "[{\"entityList\":[{\"str\":\"abc\"}]}]" });
    }

    @Test
    public void test5Test() {
        ApiManager manager = new ApiManager();
        manager.register(ApiManager.parseApi(HttpApiUtilTestExtend.class), new HttpApiUtilTestExtend());
        manager.processRequest("test.test5", new String[] { "[\"A\"]" });
    }
}
