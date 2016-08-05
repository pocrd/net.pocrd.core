package net.pocrd.core.test;

import com.alibaba.fastjson.JSON;
import net.pocrd.core.test.model.Evaluater_Left;
import net.pocrd.core.test.model.Evaluater_Right;
import net.pocrd.define.ConstField;
import net.pocrd.define.Evaluater;
import net.pocrd.entity.CallerInfo;
import net.pocrd.util.EvaluaterProvider;
import org.junit.Test;

import java.util.Date;

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
        public  Date date;
        public  long date2;
        public  long date3;
        public  Date date4;
        private Date date5;
        public Date getDate5() {
            return date5;
        }
        public void setDate5(Date date5) {
            this.date5 = date5;
        }
        private long date6;
        public long getDate6() {
            return date6;
        }
        public void setDate6(long date6) {
            this.date6 = date6;
        }
        private Date date7;
        public Date getDate7() {
            return date7;
        }
        public void setDate7(Date date7) {
            this.date7 = date7;
        }
        private long date8;
        public long getDate8() {
            return date8;
        }
        public void setDate8(long date8) {
            this.date8 = date8;
        }
    }

    public static class Right {
        public  long date;
        public  Date date2;
        private Date date3;
        public Date getDate3() {
            return date3;
        }
        public void setDate3(Date date3) {
            this.date3 = date3;
        }
        private long date4;
        public long getDate4() {
            return date4;
        }
        public void setDate4(long date4) {
            this.date4 = date4;
        }
        public  long date5;
        public  Date date6;
        private long date7;
        public long getDate7() {
            return date7;
        }
        public void setDate7(long date7) {
            this.date7 = date7;
        }
        private Date date8;
        public Date getDate8() {
            return date8;
        }
        public void setDate8(Date date8) {
            this.date8 = date8;
        }
    }
    @Test
    public void testDate2LongEvaluate() {
        Left l = new Left();
        Right r = new Right();
        r.date = System.currentTimeMillis();
        r.date2 = new Date(System.currentTimeMillis() + 1000);
        r.setDate3(new Date(System.currentTimeMillis() + 2000));
        r.setDate4(System.currentTimeMillis() + 3000);
        r.date5 = System.currentTimeMillis() + 4000;
        r.date6 = new Date(System.currentTimeMillis() + 5000);
        r.setDate7(System.currentTimeMillis() + 6000);
        r.setDate8(new Date(System.currentTimeMillis() + 7000));
        Evaluater<Left, Right> evaluater = EvaluaterProvider.getEvaluater(Left.class, Right.class);
        evaluater.evaluate(l, r);
        assertEquals(l.date.getTime(), r.date);
        assertEquals(l.date2, r.date2.getTime());
        assertEquals(l.date3, r.getDate3().getTime());
        assertEquals(l.date4.getTime(), r.getDate4());
        assertEquals(l.getDate5().getTime(), r.date5);
        assertEquals(l.getDate6(), r.date6.getTime());
        assertEquals(l.getDate7().getTime(), r.getDate7());
        assertEquals(l.getDate8(), r.getDate8().getTime());
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
