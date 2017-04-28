package net.pocrd.core.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;

import net.pocrd.core.test.model.Evaluater_Left;
import net.pocrd.core.test.model.Evaluater_Right;
import net.pocrd.define.ConstField;
import net.pocrd.define.Evaluater;
import net.pocrd.entity.CallerInfo;
import net.pocrd.util.EvaluaterProvider;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class EvaluaterProviderTest {
    private static Evaluater<Evaluater_Left, Evaluater_Right> evaluater;

    static {
        evaluater = EvaluaterProvider.getEvaluater(Evaluater_Left.class, Evaluater_Right.class);
    }

    @Test
    public void testEvaluater() {
        Evaluater_Left a = new Evaluater_Left();
        Evaluater_Right b = new Evaluater_Right();
        b.name = "123";
        b.age = 456;
        b.expire = Long.MAX_VALUE;
        CallerInfo caller1 = new CallerInfo();
        b.setCaller1(caller1);
        caller1.expire = Long.MIN_VALUE;
        caller1.role = "1";
        caller1.key = "aaa".getBytes(ConstField.UTF8);
        caller1.securityLevel = 333;
        caller1.deviceId = Long.MAX_VALUE;
        caller1.uid = Long.MAX_VALUE;
        b.caller2 = new CallerInfo();
        b.caller2.expire = Long.MIN_VALUE;
        b.caller2.role = "2";
        b.caller2.key = "bbb".getBytes(ConstField.UTF8);
        b.caller2.securityLevel = 444;
        b.caller2.deviceId = Long.MIN_VALUE;
        b.caller2.uid = Long.MIN_VALUE;
        CallerInfo caller3 = new CallerInfo();
        b.setCaller3(caller3);
        caller3.expire = Long.MIN_VALUE;
        caller3.role = "x";
        caller3.key = "ccc".getBytes(ConstField.UTF8);
        caller3.securityLevel = 555;
        caller3.deviceId = Long.MAX_VALUE;
        caller3.uid = Long.MIN_VALUE;
        evaluater.evaluate(a, b);
        assertEquals(a.name, b.name);
        assertEquals(a.age, b.age);
        assertEquals(a.expire, b.expire);

        assertEquals(a.caller1.expire, b.getCaller1().expire);
        org.junit.Assert.assertEquals(a.caller1.role, b.getCaller1().role);
        org.junit.Assert.assertArrayEquals(caller1.key, b.getCaller1().key);
        assertEquals(a.caller1.securityLevel, b.getCaller1().securityLevel);
        assertEquals(a.caller1.deviceId, b.getCaller1().deviceId);
        assertEquals(a.caller1.uid, b.getCaller1().uid);

        assertEquals(a.getCaller2().expire, b.caller2.expire);
        org.junit.Assert.assertEquals(a.getCaller2().role, b.caller2.role);
        assertArrayEquals(a.getCaller2().key, b.caller2.key);
        assertEquals(a.getCaller2().securityLevel, b.caller2.securityLevel);
        assertEquals(a.getCaller2().deviceId, b.caller2.deviceId);
        assertEquals(a.getCaller2().uid, b.caller2.uid);

        assertEquals(a.getCaller3().expire, b.getCaller3().expire);
        org.junit.Assert.assertEquals(a.getCaller3().role, b.getCaller3().role);
        assertEquals(a.getCaller3().key, b.getCaller3().key);
        assertEquals(a.getCaller3().securityLevel, b.getCaller3().securityLevel);
        assertEquals(a.getCaller3().deviceId, b.getCaller3().deviceId);
        assertEquals(a.getCaller3().uid, b.getCaller3().uid);
    }

    public static class Left {
        public Date data;
        public long data2;
        public long data3;
        public Date data4;
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

        public List<Left> data9;
        public List<Left> data10;
    }

    public static class Right {
        public long data;
        public Date data2;
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

        public long data5;
        public Date data6;
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

        public List<Right> data9;
        public List<Left> data10;
    }

    @Test
    public void testDate2LongEvaluate() {
        Left l = new Left();
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
        Right rr = new Right();
        r.data9.add(rr);
        r.data10 = new ArrayList<>();
        Left ll = new Left();
        r.data10.add(ll);
        Evaluater<Left, Right> evaluater = EvaluaterProvider.getEvaluater(Left.class, Right.class);
        evaluater.evaluate(l, r);
        assertEquals(l.data.getTime(), r.data);
        assertEquals(l.data2, r.data2.getTime());
        assertEquals(l.data3, r.getData3().getTime());
        assertEquals(l.data4.getTime(), r.getData4());
        assertEquals(l.getData5().getTime(), r.data5);
        assertEquals(l.getData6(), r.data6.getTime());
        assertEquals(l.getData7().getTime(), r.getData7());
        assertEquals(l.getData8(), r.getData8().getTime());
        if (l.data9 != null && l.data9.size() > 0) {
            assertEquals(l.data9.get(0).data, r.data9.get(0).data);
        }
        if (l.data10 != null && l.data10.size() > 0) {
            assertEquals(l.data10.get(0).data, r.data10.get(0).data);
        }
        System.out.println(JSON.toJSONString(l));
        System.out.println(JSON.toJSONString(r));
    }

    @Test
    public void testBooleanCopy() {
        Evaluater_Right right = new Evaluater_Right();
        right.setSuccess(true);
        right.success1 = true;
        right.setSuccess2(true);
        right.success3 = true;
        right.setSuccess4(true);
        //        right.isSuccess5 = true;
        //        right.setSuccess6(true);
        right.isSuccess7 = true;
        Evaluater_Left left = new Evaluater_Left();
        EvaluaterProvider.getEvaluater(Evaluater_Left.class, Evaluater_Right.class).evaluate(left, right);
        assertEquals(left.isSuccess(), right.isSuccess());
        assertEquals(left.isSuccess1(), right.success1);
        assertEquals(left.success2, right.isSuccess2());
        assertEquals(left.success3, right.success3);
        assertEquals(left.isSuccess4(), right.isSuccess4());
        //        assertEquals(left.isSuccess5(), right.isSuccess5);
        //        assertEquals(left.isSuccess6, right.isSuccess6());
        assertEquals(left.isSuccess7, right.isSuccess7);
    }
}
