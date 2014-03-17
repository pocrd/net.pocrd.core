package net.pocrd.core.test;

import static org.junit.Assert.assertEquals;
import net.pocrd.core.test.model.Evaluater_Left;
import net.pocrd.core.test.model.Evaluater_Right;
import net.pocrd.define.Evaluater;
import net.pocrd.entity.CallerInfo;
import net.pocrd.util.EvaluaterProvider;

import org.junit.Test;

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
        caller1.groups = new String[] { "1", "3", "5" };
        caller1.key = "aaa";
        caller1.securityLevel = 333;
        caller1.deviceId = Long.MAX_VALUE;
        caller1.uid = Long.MAX_VALUE;
        b.caller2 = new CallerInfo();
        b.caller2.expire = Long.MIN_VALUE;
        b.caller2.groups = new String[] { "2", "4", "6" };
        b.caller2.key = "bbb";
        b.caller2.securityLevel = 444;
        b.caller2.deviceId = Long.MIN_VALUE;
        b.caller2.uid = Long.MIN_VALUE;
        CallerInfo caller3 = new CallerInfo();
        b.setCaller3(caller3);
        caller3.expire = Long.MIN_VALUE;
        caller3.groups = new String[] { "x", "y", "z" };
        caller3.key = "ccc";
        caller3.securityLevel = 555;
        caller3.deviceId = Long.MAX_VALUE;
        caller3.uid = Long.MIN_VALUE;
        evaluater.evaluate(a, b);
        assertEquals(a.name, b.name);
        assertEquals(a.age, b.age);
        assertEquals(a.expire, b.expire);

        assertEquals(a.caller1.expire, b.getCaller1().expire);
        org.junit.Assert.assertArrayEquals(a.caller1.groups, b.getCaller1().groups);
        assertEquals(a.caller1.key, b.getCaller1().key);
        assertEquals(a.caller1.securityLevel, b.getCaller1().securityLevel);
        assertEquals(a.caller1.deviceId, b.getCaller1().deviceId);
        assertEquals(a.caller1.uid, b.getCaller1().uid);

        assertEquals(a.getCaller2().expire, b.caller2.expire);
        org.junit.Assert.assertArrayEquals(a.getCaller2().groups, b.caller2.groups);
        assertEquals(a.getCaller2().key, b.caller2.key);
        assertEquals(a.getCaller2().securityLevel, b.caller2.securityLevel);
        assertEquals(a.getCaller2().deviceId, b.caller2.deviceId);
        assertEquals(a.getCaller2().uid, b.caller2.uid);

        assertEquals(a.getCaller3().expire, b.getCaller3().expire);
        org.junit.Assert.assertArrayEquals(a.getCaller3().groups, b.getCaller3().groups);
        assertEquals(a.getCaller3().key, b.getCaller3().key);
        assertEquals(a.getCaller3().securityLevel, b.getCaller3().securityLevel);
        assertEquals(a.getCaller3().deviceId, b.getCaller3().deviceId);
        assertEquals(a.getCaller3().uid, b.getCaller3().uid);
    }

}
