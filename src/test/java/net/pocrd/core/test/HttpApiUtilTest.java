package net.pocrd.core.test;

import com.alibaba.fastjson.JSON;
import net.pocrd.annotation.*;
import net.pocrd.core.ApiManager;
import net.pocrd.core.test.HttpApiUtilTest.RC;
import net.pocrd.define.SecurityType;
import net.pocrd.define.Serializer;
import net.pocrd.entity.AbstractReturnCode;
import net.pocrd.responseEntity.ObjectArrayResp;
import net.pocrd.util.POJOSerializerProvider;
import net.pocrd.util.RawString;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.junit.Assert.assertEquals;

@ApiGroup(name = "test", minCode = 0, maxCode = 100, codeDefine = RC.class, owner = "guankaiqiang")
public class HttpApiUtilTest {

    public static class RC extends AbstractReturnCode {

        protected RC(String desc, int code) {
            super(desc, code);
        }
    }

    @Description("entity")
    public static class TestEntity implements Serializable {
        public TestEntity(String str) {
            this.str = str;
            this.date = new Date(1234567890000L);
        }

        @Description("str")
        public String str;

        @Description("date")
        public Date date;
    }

    @HttpApi(name = "test.test1", desc = "测试1", security = SecurityType.None, owner = "guankaiqiang")
    public RawString execute(
            @ApiParameter(required = true, name = "i1", desc = "参数1")
                    int i1,
            @ApiParameter(required = true, name = "i2", verifyRegex = "^\\d+$", desc = "参数2")
                    int i2) {
        RawString s = new RawString();
        s.value = String.valueOf(i1 + i2);
        return s;
    }

    @HttpApi(name = "test.test2", desc = "测试2", security = SecurityType.None, owner = "guankaiqiang")
    public RawString execute(
            @ApiParameter(required = true, name = "b1", desc = "参数1")
                    boolean b1,
            @ApiParameter(required = false, name = "b2", desc = "参数2", defaultValue = "true")
                    boolean b2,
            @ApiParameter(required = true, name = "b3", verifyRegex = "^(true)|(false)$", desc = "参数3")
                    boolean b3,
            @ApiParameter(required = false, name = "b4", desc = "参数4", defaultValue = "false")
                    boolean b4,

            @ApiParameter(required = true, name = "by1", desc = "参数5")
                    byte by1,
            @ApiParameter(required = false, name = "by2", desc = "参数6", defaultValue = "127")
                    byte by2,
            @ApiParameter(required = true, name = "by3", verifyRegex = "^-?\\d+$", desc = "参数7")
                    byte by3,
            @ApiParameter(required = false, name = "by4", desc = "参数8", defaultValue = "-128")
                    byte by4,

            @ApiParameter(required = true, name = "c1", desc = "参数9")
                    char c1,
            @ApiParameter(required = false, name = "c2", desc = "参数10", defaultValue = "" + (int)'c')
                    char c2,
            @ApiParameter(required = true, name = "c3", verifyRegex = "^\\+?\\d+$", desc = "参数11")
                    char c3,
            @ApiParameter(required = false, name = "c4", desc = "参数12", defaultValue = "" + (int)'z')
                    char c4,

            @ApiParameter(required = true, name = "s1", desc = "参数13")
                    short s1,
            @ApiParameter(required = false, name = "s2", desc = "参数14", defaultValue = "" + Short.MAX_VALUE)
                    short s2,
            @ApiParameter(required = true, name = "s3", verifyRegex = "^-?\\d+$", desc = "参数15")
                    short s3,
            @ApiParameter(required = false, name = "s4", desc = "参数16", defaultValue = "" + Short.MIN_VALUE)
                    short s4,

            @ApiParameter(required = true, name = "i1", desc = "参数17")
                    int i1,
            @ApiParameter(required = false, name = "i2", desc = "参数18", defaultValue = "0" + Integer.MAX_VALUE)
                    int i2,
            @ApiParameter(required = true, name = "i3", verifyRegex = "^-?\\d+$", desc = "参数19")
                    int i3,
            @ApiParameter(required = false, name = "i4", desc = "参数20", defaultValue = "" + Integer.MIN_VALUE)
                    int i4,

            @ApiParameter(required = true, name = "l1", desc = "参数21")
                    long l1,
            @ApiParameter(required = false, name = "l2", desc = "参数22", defaultValue = "" + Long.MAX_VALUE)
                    long l2,
            @ApiParameter(required = true, name = "l3", verifyRegex = "^-?\\d+$", desc = "参数23")
                    long l3,
            @ApiParameter(required = false, name = "l4", desc = "参数24", defaultValue = "" + Long.MIN_VALUE)
                    long l4,

            @ApiParameter(required = true, name = "f1", desc = "参数25")
                    float f1,
            @ApiParameter(required = false, name = "f2", desc = "参数26", defaultValue = "" + Float.MAX_VALUE)
                    float f2,
            @ApiParameter(required = true, name = "f3", verifyRegex = "^-?\\d+\\.\\d+$", desc = "参数27")
                    float f3,
            @ApiParameter(required = false, name = "f4", desc = "参数28", defaultValue = "" + Float.MIN_VALUE)
                    float f4,

            @ApiParameter(required = true, name = "d1", desc = "参数29")
                    double d1,
            @ApiParameter(required = false, name = "d2", desc = "参数30", defaultValue = "" + Double.MAX_VALUE)
                    double d2,
            @ApiParameter(required = true, name = "d3", verifyRegex = "^-?\\d+\\.\\d+$", desc = "参数31")
                    double d3,
            @ApiParameter(required = false, name = "d4", desc = "参数32", defaultValue = "" + Double.MIN_VALUE)
                    double d4,

            @ApiParameter(required = true, name = "st1", desc = "参数33")
                    String st1,
            @ApiParameter(required = false, name = "st2", desc = "参数34", defaultValue = "xxx")
                    String st2) {

        RawString r = new RawString();
        r.value =
                "result " + b1 + " " + b2 + " " + b3 + " " + b4 + " " + by1 + " " + by2 + " " + by3 + " " + by4 + " " + c1 + " " + c2 + " " + c3 + " "
                        +
                        c4 + " " + s1 + " " + s2 + " " + s3 + " " + s4 + " " + i1 + " " + i2 + " " + i3 + " " + i4 + " " + l1 + " " + l2 + " " + l3
                        + " " + l4 + " " + f1 + " " + f2 + " " +
                        "" + f3 + " " + f4 + " " + d1 + " " + d2 + " " + d3 + " " + d4 + " " + st1 + " " + st2 + " ";
        return r;
    }

    @HttpApi(name = "test.test3", desc = "测试3", security = SecurityType.None, owner = "guankaiqiang")
    public boolean execute(
            @ApiParameter(required = true, name = "b", desc = "参数")
                    boolean b) {
        return b;
    }

    @HttpApi(name = "test.test4", desc = "测试4", security = SecurityType.None, owner = "guankaiqiang")
    public boolean[] execute(
            @ApiParameter(required = true, name = "b1", desc = "参数")
                    boolean b1,
            @ApiParameter(required = true, name = "b2", desc = "参数")
                    boolean b2,
            @ApiParameter(required = true, name = "b3", desc = "参数")
                    boolean b3) {
        return new boolean[] { b1, b2, b3 };
    }

    @HttpApi(name = "test.test5", desc = "测试5", security = SecurityType.None, owner = "guankaiqiang")
    public byte execute(
            @ApiParameter(required = true, name = "b", desc = "参数")
                    byte b) {
        return b;
    }

    @HttpApi(name = "test.test6", desc = "测试6", security = SecurityType.None, owner = "guankaiqiang")
    public byte[] execute(
            @ApiParameter(required = true, name = "b1", desc = "参数")
                    byte b1,
            @ApiParameter(required = true, name = "b2", desc = "参数")
                    byte b2,
            @ApiParameter(required = true, name = "b3", desc = "参数")
                    byte b3) {
        return new byte[] { b1, b2, b3 };
    }

    @HttpApi(name = "test.test7", desc = "测试7", security = SecurityType.None, owner = "guankaiqiang")
    public short execute(
            @ApiParameter(required = true, name = "s", desc = "参数")
                    short s) {
        return s;
    }

    @HttpApi(name = "test.test8", desc = "测试8", security = SecurityType.None, owner = "guankaiqiang")
    public short[] execute(
            @ApiParameter(required = true, name = "s1", desc = "参数")
                    short s1,
            @ApiParameter(required = true, name = "s2", desc = "参数")
                    short s2,
            @ApiParameter(required = true, name = "s3", desc = "参数")
                    short s3) {
        return new short[] { s1, s2, s3 };
    }

    @HttpApi(name = "test.test9", desc = "测试9", security = SecurityType.None, owner = "guankaiqiang")
    public char execute(
            @ApiParameter(required = true, name = "c", desc = "参数")
                    char c) {
        return c;
    }

    @HttpApi(name = "test.test10", desc = "测试10", security = SecurityType.None, owner = "guankaiqiang")
    public char[] execute(
            @ApiParameter(required = true, name = "c1", desc = "参数")
                    char c1,
            @ApiParameter(required = true, name = "c2", desc = "参数")
                    char c2,
            @ApiParameter(required = true, name = "c3", desc = "参数")
                    char c3) {
        return new char[] { c1, c2, c3 };
    }

    @HttpApi(name = "test.test11", desc = "测试11", security = SecurityType.None, owner = "guankaiqiang")
    public int execute(
            @ApiParameter(required = true, name = "c", desc = "参数")
                    int c) {
        return c;
    }

    @HttpApi(name = "test.test12", desc = "测试12", security = SecurityType.None, owner = "guankaiqiang")
    public int[] execute(
            @ApiParameter(required = true, name = "c1", desc = "参数")
                    int c1,
            @ApiParameter(required = true, name = "c2", desc = "参数")
                    int c2,
            @ApiParameter(required = true, name = "c3", desc = "参数")
                    int c3) {
        return new int[] { c1, c2, c3 };
    }

    @HttpApi(name = "test.test13", desc = "测试13", security = SecurityType.None, owner = "guankaiqiang")
    public long execute(
            @ApiParameter(required = true, name = "c", desc = "参数")
                    long c) {
        return c;
    }

    @HttpApi(name = "test.test14", desc = "测试14", security = SecurityType.None, owner = "guankaiqiang")
    public long[] execute(
            @ApiParameter(required = true, name = "c1", desc = "参数")
                    long c1,
            @ApiParameter(required = true, name = "c2", desc = "参数")
                    long c2,
            @ApiParameter(required = true, name = "c3", desc = "参数")
                    long c3) {
        return new long[] { c1, c2, c3 };
    }

    @HttpApi(name = "test.test15", desc = "测试15", security = SecurityType.None, owner = "guankaiqiang")
    public float execute(
            @ApiParameter(required = true, name = "c", desc = "参数")
                    float c) {
        return c;
    }

    @HttpApi(name = "test.test16", desc = "测试16", security = SecurityType.None, owner = "guankaiqiang")
    public float[] execute(
            @ApiParameter(required = true, name = "c1", desc = "参数")
                    float c1,
            @ApiParameter(required = true, name = "c2", desc = "参数")
                    float c2,
            @ApiParameter(required = true, name = "c3", desc = "参数")
                    float c3) {
        return new float[] { c1, c2, c3 };
    }

    @HttpApi(name = "test.test17", desc = "测试17", security = SecurityType.None, owner = "guankaiqiang")
    public double execute(
            @ApiParameter(required = true, name = "c", desc = "参数")
                    double c) {
        return c;
    }

    @HttpApi(name = "test.test18", desc = "测试18", security = SecurityType.None, owner = "guankaiqiang")
    public double[] execute(
            @ApiParameter(required = true, name = "c1", desc = "参数")
                    double c1,
            @ApiParameter(required = true, name = "c2", desc = "参数")
                    double c2,
            @ApiParameter(required = true, name = "c3", desc = "参数")
                    double c3) {
        return new double[] { c1, c2, c3 };
    }

    @HttpApi(name = "test.test19", desc = "测试19", security = SecurityType.None, owner = "guankaiqiang")
    public String execute(
            @ApiParameter(required = true, name = "c", desc = "参数")
                    String c) {
        return c;
    }

    @HttpApi(name = "test.test20", desc = "测试20", security = SecurityType.None, owner = "guankaiqiang")
    public String[] execute(
            @ApiParameter(required = true, name = "c1", desc = "参数")
                    String c1,
            @ApiParameter(required = true, name = "c2", desc = "参数")
                    String c2,
            @ApiParameter(required = true, name = "c3", desc = "参数")
                    String c3) {
        return new String[] { c1, c2, c3 };
    }

    @HttpApi(name = "test.test21", desc = "测试21", security = SecurityType.None, owner = "guankaiqiang")
    public String execute() {
        return "x";
    }

    @HttpApi(name = "test.test22", desc = "测试22", security = SecurityType.None, owner = "guankaiqiang")
    public List<TestEntity> execute(
            @ApiParameter(required = true, name = "i1", desc = "参数1")
                    int i1,
            @ApiParameter(required = true, name = "str2", desc = "参数2")
                    String i2) {
        List result = new ArrayList<TestEntity>();
        TestEntity res = new TestEntity("testest");
        result.add(res);
        return result;
    }

    @HttpApi(name = "test.test23", desc = "测试24", security = SecurityType.None, owner = "guankaiqiang")
    public List<String> execute(
            @ApiParameter(required = true, name = "str1", desc = "参数1")
                    String i1,
            @ApiParameter(required = true, name = "str2", desc = "参数2")
                    String i2,
            @ApiParameter(required = true, name = "str3", desc = "参数3")
                    String i3,
            @ApiParameter(required = true, name = "str4", desc = "参数4")
                    String i4) {
        return new ArrayList<String>() {
            {add("a");}
        };
    }

    @HttpApi(name = "test.test24", desc = "测试24", security = SecurityType.None, owner = "guankaiqiang")
    public List<String> execute(
            @ApiParameter(required = true, name = "str1", desc = "参数1")
                    String i1,
            @ApiCookieAutowired({ "_c1", "_c2" })
                    Map<String, String> map,
            @ApiParameter(required = true, name = "str3", desc = "参数3")
                    String i3,
            @ApiParameter(required = true, name = "str4", desc = "参数4")
                    String i4) {
        List<String> list = new ArrayList<String>();
        for (String key : map.keySet()) {
            list.add(key + map.get(key));
        }
        return list;
    }

    @HttpApi(name = "test.test25", desc = "测试25", security = SecurityType.None, owner = "guankaiqiang")
    public List<Date> execute(
            @ApiParameter(required = true, name = "date1", desc = "参数1")
                    Date d1,
            @ApiParameter(required = true, name = "date2", desc = "参数2")
                    Date[] d2) {
        List<Date> dl = new ArrayList<Date>(d2 == null ? 1 : 1 + d2.length);
        StringBuilder sb = new StringBuilder();
        sb.append(d1.getTime());
        sb.append(";");
        dl.add(d1);
        if (d2 != null) {
            for (Date d : d2) {
                sb.append(d.getTime()).append(";");
                dl.add(d);
            }
        }
        System.out.println(sb.toString());
        return dl;
    }

    public static enum E1 {
        T1,
        T2,
        T3,
        T4,
        T5
    }

    @Test
    public void test() throws UnsupportedEncodingException {
        try {
            ApiManager manager = new ApiManager();
            manager.register(ApiManager.parseApi(HttpApiUtilTest.class, new HttpApiUtilTest()));
            {
                RawString result = (RawString)manager.processRequest("test.test1", new String[] { "123", "456" });
                System.out.println(result);
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                Serializer.rawStringSerializer.toJson(result, baos, true);
                String re = baos.toString("UTF-8");
                assertEquals(String.valueOf(123 + 456), re);
            }
            {
                RawString result = (RawString)manager.processRequest("test.test2",
                        new String[] { "true", null, "true", null, "-128", null, "-128", null, String.valueOf(
                                (int)'c'), null, String.valueOf(
                                (int)'c'), null, "32767", null, "32767", null, "12345678", null, "12345678", null, "12345678900", null, "12345678900",
                                null, "3.14159", null, "3.14159", null, "3.14159265", null, "3.14159265", null, "end", null });
                System.out.println(result);
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                Serializer.rawStringSerializer.toJson(result, baos, true);
                String re = baos.toString("UTF-8");
                assertEquals(
                        "result true true true false -128 127 -128 -128 c c c z 32767 32767 32767 -32768 12345678 2147483647 12345678 -2147483648 12345678900 9223372036854775807 12345678900 -9223372036854775808 3.14159 3.4028235E38 3.14159 1.4E-45 3.14159265 1.7976931348623157E308 3.14159265 4.9E-324 end xxx ",
                        re);
            }
            {
                RawString result = (RawString)manager.processRequest("test.test2",
                        new String[] { "true", "false", "true", "false", "-128", "127", "-128", "127", String.valueOf(
                                (int)'c'), String.valueOf((int)'z'), String.valueOf(
                                (int)'c'), String.valueOf(
                                (int)'x'), "32767", "-32768", "32767", "-32768", "12345678", "87654321", "12345678", "87654321", "12345678900",
                                "999876543210", "12345678900", "999876543210", "3.14159", "1.234567", "3.14159", "1.234567", "3.14159265",
                                "1.23456789", "3.14159265", "1.23456789", "end", "xxx" });
                System.out.println(result);
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                Serializer.rawStringSerializer.toJson(result, baos, true);
                String re = baos.toString();
                assertEquals(
                        "result true false true false -128 127 -128 127 c z c x 32767 -32768 32767 -32768 12345678 87654321 12345678 87654321 12345678900 999876543210 12345678900 999876543210 3.14159 1.234567 3.14159 1.234567 3.14159265 1.23456789 3.14159265 1.23456789 end xxx ",
                        re);
            }
            {
                Object result = manager.processRequest("test.test3", new String[] { "true" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test4", new String[] { "false", "true", "false" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test5", new String[] { "1" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test6", new String[] { "1", "2", "3" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test7", new String[] { "1" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test8", new String[] { "1", "2", "3" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test9", new String[] { "1" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test10", new String[] { "1", "2", "3" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test11", new String[] { "11111" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test12", new String[] { "111111", "123456", "5555555" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test13", new String[] { "1" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test14", new String[] { "1", "2", "3" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test15", new String[] { "1" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test16", new String[] { "1", "2", "3" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test17", new String[] { "1" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test18", new String[] { "1", "2", "3" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test19", new String[] { "1" });
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test20", new String[] { "1", "2", "3" });
                System.out.println(result);
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
                baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toXml(result, baos, true);
                re = baos.toString();
                System.out.println(re);
            }
            {
                ObjectArrayResp result = (ObjectArrayResp)manager.processRequest("test.test22", new String[] { "123", "456" });
                Assert.assertTrue(result != null);
                Assert.assertTrue(result.value != null);
                Assert.assertTrue(result.value.length > 0);
                Assert.assertTrue(result.value[0] instanceof TestEntity);
                Assert.assertEquals(((TestEntity)result.value[0]).str, "testest");
                Assert.assertEquals(((TestEntity)result.value[0]).date.getTime(), 1234567890000L);
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                Serializer.objectArrayRespSerializer.toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
                baos = new ByteArrayOutputStream(1024);
                Serializer.objectArrayRespSerializer.toXml(result, baos, true);
                re = baos.toString();
                System.out.println(re);
            }
            {
                Object result = manager.processRequest("test.test23", new String[] { "123", "456", "456", "456" });
                System.out.println(result);
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
                baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toXml(result, baos, true);
                re = baos.toString();
                System.out.println(re);
            }
            {
                Map<String, String> map = new HashMap<String, String>();
                map.put("a", "b");
                map.put("c", "d");
                Object result = manager.processRequest("test.test24", new String[] { "123", JSON.toJSONString(map), "456", "456" });
                System.out.println(result);
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
                String re = baos.toString();
                System.out.println(re);
                baos = new ByteArrayOutputStream(1024);
                ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toXml(result, baos, true);
                re = baos.toString();
                System.out.println(re);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDate() {
        ApiManager manager = new ApiManager();
        manager.register(ApiManager.parseApi(HttpApiUtilTest.class, new HttpApiUtilTest()));
        {
            String[] darray = new String[3];
            darray[0] = "1234567891000";
            darray[1] = "1234567892000";
            darray[2] = "1234567893000";
            Object result = manager.processRequest("test.test25", new String[] { "1234567890000", JSON.toJSONString(darray) });
            System.out.println(result);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toJson(result, baos, true);
            String re = baos.toString();
            System.out.println("json:" + re);
            baos = new ByteArrayOutputStream(1024);
            ((Serializer<Object>)POJOSerializerProvider.getSerializer(result.getClass())).toXml(result, baos, true);
            re = baos.toString();
            System.out.println("xml:" + re);
        }
    }
}
