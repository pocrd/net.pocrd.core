package net.pocrd.core.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.pocrd.annotation.Description;
import net.pocrd.core.test.model.Evaluator_Left;
import net.pocrd.core.test.model.Evaluator_Right;
import net.pocrd.define.ConstField;
import net.pocrd.define.Evaluator;
import net.pocrd.entity.CallerInfo;
import net.pocrd.util.EvaluatorProvider;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class EvaluatorProviderTest {

    static {
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();//disable循环引用
        //            JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteMapNullValue;//null属性，序列化为null,do by guankaiqiang,android sdk中 JSON.optString()将null convert成了"null",故关闭该特性
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.NotWriteRootClassName.getMask();
        //            JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteEnumUsingToString.getMask();
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteNullNumberAsZero.getMask();
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteNullBooleanAsFalse.getMask();
    }

    @Test
    public void testEvaluator() {
        Evaluator_Left a = new Evaluator_Left();
        Evaluator_Right b = new Evaluator_Right();
        b.name = "123";
        b.age = 456;
        b.expire = Long.MAX_VALUE;
        CallerInfo caller1 = new CallerInfo();
        b.setCaller1(caller1);
        caller1.expire = Long.MIN_VALUE;
        caller1.subSystemRole = "1";
        caller1.key = "aaa".getBytes(ConstField.UTF8);
        caller1.securityLevel = 333;
        caller1.deviceId = Long.MAX_VALUE;
        caller1.uid = Long.MAX_VALUE;
        b.caller2 = new CallerInfo();
        b.caller2.expire = Long.MIN_VALUE;
        b.caller2.subSystemRole = "2";
        b.caller2.key = "bbb".getBytes(ConstField.UTF8);
        b.caller2.securityLevel = 444;
        b.caller2.deviceId = Long.MIN_VALUE;
        b.caller2.uid = Long.MIN_VALUE;
        CallerInfo caller3 = new CallerInfo();
        b.setCaller3(caller3);
        caller3.expire = Long.MIN_VALUE;
        caller3.subSystemRole = "x";
        caller3.key = "ccc".getBytes(ConstField.UTF8);
        caller3.securityLevel = 555;
        caller3.deviceId = Long.MAX_VALUE;
        caller3.uid = Long.MIN_VALUE;
        EvaluatorProvider.getEvaluator(Evaluator_Left.class, Evaluator_Right.class).evaluate(a, b);
        assertEquals(a.name, b.name);
        assertEquals(a.age, b.age);
        assertEquals(a.expire, b.expire);

        assertEquals(a.caller1.expire, b.getCaller1().expire);
        org.junit.Assert.assertEquals(a.caller1.subSystemRole, b.getCaller1().subSystemRole);
        org.junit.Assert.assertArrayEquals(caller1.key, b.getCaller1().key);
        assertEquals(a.caller1.securityLevel, b.getCaller1().securityLevel);
        assertEquals(a.caller1.deviceId, b.getCaller1().deviceId);
        assertEquals(a.caller1.uid, b.getCaller1().uid);

        assertEquals(a.getCaller2().expire, b.caller2.expire);
        org.junit.Assert.assertEquals(a.getCaller2().subSystemRole, b.caller2.subSystemRole);
        assertArrayEquals(a.getCaller2().key, b.caller2.key);
        assertEquals(a.getCaller2().securityLevel, b.caller2.securityLevel);
        assertEquals(a.getCaller2().deviceId, b.caller2.deviceId);
        assertEquals(a.getCaller2().uid, b.caller2.uid);

        assertEquals(a.getCaller3().expire, b.getCaller3().expire);
        org.junit.Assert.assertEquals(a.getCaller3().subSystemRole, b.getCaller3().subSystemRole);
        assertEquals(a.getCaller3().key, b.getCaller3().key);
        assertEquals(a.getCaller3().securityLevel, b.getCaller3().securityLevel);
        assertEquals(a.getCaller3().deviceId, b.getCaller3().deviceId);
        assertEquals(a.getCaller3().uid, b.getCaller3().uid);
    }

    @Description("X")
    public static class X extends Left {

    }

    @Description("Y")
    public static class Y extends Right {

    }

    @Description("Left")
    public static class Left {
        @Description("data")
        public  Date data;
        @Description("data2")
        public  long data2;
        @Description("data3")
        public  long data3;
        @Description("data4")
        public  Date data4;
        private Date data5;

        public Date getData5() {
            return data5;
        }

        public void setData5(Date data5) {
            this.data5 = data5;
        }

        private long data6;

        public long getData6() {
            return data6;
        }

        public void setData6(long data6) {
            this.data6 = data6;
        }

        private Date data7;

        public Date getData7() {
            return data7;
        }

        public void setData7(Date data7) {
            this.data7 = data7;
        }

        private long data8;

        public long getData8() {
            return data8;
        }

        public void setData8(long data8) {
            this.data8 = data8;
        }

        @Description("data9")
        public List<Left>  data9;
        @Description("data10")
        public List<Right> data10;
        @Description("data11")
        public Left        data11;
        @Description("data12")
        public String      data12;
        @Description("data13")
        public X           data13;
    }

    @Description("Right")
    public static class Right {
        @Description("data")
        public  long data;
        @Description("data2")
        public  Date data2;
        private Date data3;

        public Date getData3() {
            return data3;
        }

        public void setData3(Date data3) {
            this.data3 = data3;
        }

        private long data4;

        public long getData4() {
            return data4;
        }

        public void setData4(long data4) {
            this.data4 = data4;
        }

        @Description("data5")
        public  long data5;
        @Description("data6")
        public  Date data6;
        private long data7;

        public long getData7() {
            return data7;
        }

        public void setData7(long data7) {
            this.data7 = data7;
        }

        private Date data8;

        public Date getData8() {
            return data8;
        }

        public void setData8(Date data8) {
            this.data8 = data8;
        }

        @Description("data9")
        public List<Right> data9;
        @Description("data10")
        public List<Left>  data10;
        @Description("data11")
        public Right       data11;
        @Description("data12")
        public String      data12;
        @Description("data13")
        public Y           data13;
    }

    @Test
    public void testDate2LongEvaluate() {
        String[] x = "|".split("\\|");

        Left l = new Left();
        Right r = getRight();
        Left nl = null;
        r.data9.add(getRight());
        r.data9.add(getRight());
        r.data9.add(getRight());
        Evaluator<Left, Right> e1 = EvaluatorProvider.getEvaluator(Left.class, Right.class);
        e1.evaluate(l, r);
        Evaluator<Right, Left> e2 = EvaluatorProvider.getEvaluator(Right.class, Left.class);
        Right newR = new Right();
        e2.evaluate(newR, l);
        String json_r = JSON.toJSONString(r);
        String json_newR = JSON.toJSONString(newR);
        assertEquals(json_r, json_newR);
        //        long time1 = System.currentTimeMillis();
        //        for (int i = 0; i < 500000L; i++) {
        //            nl = JSON.parseObject(json_r, Left.class);
        //        }
        //        long time2 = System.currentTimeMillis();
        //        for (int i = 0; i < 500000L; i++) {
        //            nl = new Left();
        //            e1.evaluate(nl, r);
        //        }
        //        long time3 = System.currentTimeMillis();
        //        System.out.println("dt1 = " + (time2 - time1));
        //        System.out.println("dt2 = " + (time3 - time2));
        System.out.println(json_r);
        System.out.println(json_newR);
    }

    @Test
    public void testBooleanCopy() {
        Evaluator_Right right = new Evaluator_Right();
        right.setSuccess(true);
        right.success1 = true;
        right.setSuccess2(true);
        right.success3 = true;
        right.setSuccess4(true);
        //        right.isSuccess5 = true;
        //        right.setSuccess6(true);
        right.isSuccess7 = true;
        Evaluator_Left left = new Evaluator_Left();
        EvaluatorProvider.getEvaluator(Evaluator_Left.class, Evaluator_Right.class).evaluate(left, right);
        assertEquals(left.isSuccess(), right.isSuccess());
        assertEquals(left.isSuccess1(), right.success1);
        assertEquals(left.success2, right.isSuccess2());
        assertEquals(left.success3, right.success3);
        assertEquals(left.isSuccess4(), right.isSuccess4());
        //        assertEquals(left.isSuccess5(), right.isSuccess5);
        //        assertEquals(left.isSuccess6, right.isSuccess6());
        assertEquals(left.isSuccess7, right.isSuccess7);
    }

    private Right getRight() {
        Right r = new Right();
        r.data = System.currentTimeMillis();
        r.data2 = new Date(System.currentTimeMillis() + 1000);
        r.setData3(new Date(System.currentTimeMillis() + 2000));
        r.setData4(System.currentTimeMillis() + 3000);
        r.data5 = System.currentTimeMillis() + 4000;
        r.data6 = new Date(System.currentTimeMillis() + 5000);
        r.setData7(System.currentTimeMillis() + 6000);
        r.setData8(new Date(System.currentTimeMillis() + 7000));
        r.data9 = new ArrayList<>();
        r.data10 = new ArrayList<>();
        Left ll = new Left();
        r.data10.add(ll);
        r.data10.add(ll);
        r.data11 = new Right();
        r.data12 = "hello";
        r.data13 = getY();
        return r;
    }

    private Y getY() {
        Y r = new Y();
        r.data = System.currentTimeMillis();
        r.data2 = new Date(System.currentTimeMillis() + 1000);
        r.setData3(new Date(System.currentTimeMillis() + 2000));
        r.setData4(System.currentTimeMillis() + 3000);
        r.data5 = System.currentTimeMillis() + 4000;
        r.data6 = new Date(System.currentTimeMillis() + 5000);
        r.setData7(System.currentTimeMillis() + 6000);
        r.setData8(new Date(System.currentTimeMillis() + 7000));
        r.data9 = new ArrayList<>();
        Right rr = new Right();
        rr.data = System.currentTimeMillis();
        rr.data2 = new Date(System.currentTimeMillis() + 1000);
        rr.setData3(new Date(System.currentTimeMillis() + 2000));
        rr.setData4(System.currentTimeMillis() + 3000);
        rr.data5 = System.currentTimeMillis() + 4000;
        rr.data6 = new Date(System.currentTimeMillis() + 5000);
        rr.setData7(System.currentTimeMillis() + 6000);
        rr.setData8(new Date(System.currentTimeMillis() + 7000));
        rr.data12 = "hello world";
        r.data9.add(rr);
        r.data10 = new ArrayList<>();
        Left ll = new Left();
        r.data10.add(ll);
        r.data10.add(ll);
        r.data11 = new Right();
        r.data12 = "hello";

        return r;
    }
}
