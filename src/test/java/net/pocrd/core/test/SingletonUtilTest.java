package net.pocrd.core.test;

import net.pocrd.util.SingletonUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SingletonUtilTest {

    public static class TestClass1 {
        public TestClass1() {}
    }

    public static class TestClass2 {
        TestClass2() {}
    }

    @Test
    public void testSingleton1() {
        TestClass1 c = SingletonUtil.getSingleton(TestClass1.class);
        assertEquals(c, SingletonUtil.getSingleton(TestClass1.class));
    }

    @Test
    public void testSingleton2() {
        try {
            TestClass2 c = SingletonUtil.getSingleton(TestClass2.class);
            assertEquals(c, SingletonUtil.getSingleton(TestClass2.class));
        } catch (Exception e) {
            return;
        }
        fail("should create failed.");
    }

    @Test
    public void testMultithreadSingleton() {
        TestClass1 c = SingletonUtil.getSingleton(TestClass1.class);
        assertEquals(c, SingletonUtil.getSingleton(TestClass1.class));
    }
}
