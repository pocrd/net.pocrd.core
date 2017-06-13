package net.pocrd.core.test;

import net.pocrd.core.test.model.KVData;
import net.pocrd.core.test.model.TestObj2;
import net.pocrd.define.ConstField;
import net.pocrd.util.POJOSerializerProvider;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class POJOSerializerProviderTest {
    @Test
    public void test() {
        POJOSerializerProvider.getSerializer(KVData.class);
        TestObj2 t = new TestObj2();
//        t.testEnum = TestEnum.a;
        t.c = 'a';
        t.b = true;
        t.bs = new boolean[]{false, true, false, true};

        t.d = 1.234567D;
        t.ds = new double[]{1.23D, 1.234D, 1.2345D};

        t.f = 1.234567f;
        t.fs = new float[]{1.23f, 1.234f, 1.2345f};

        t.i = 1;
        t.is = new int[]{2, 3, 4};

        t.l = 123456789L;
        t.ls = new long[]{123456789L, 1234567890L, 9876543210L};

        t.s = "hello \"world\"";
        t.ss = new ArrayList<String>();
        t.ss.add("hello");
        t.ss.add("<![CDATA[<xml>]]>");
        t.ss.add("!");

        TestObj2 t1 = new TestObj2();
        t1.b = true;
        t1.bs = new boolean[]{false, true, false, true};

        t1.d = 1.234567D;
        t1.ds = new double[]{1.23D, 1.234D, 1.2345D};

        t1.f = 1.234567f;
        t1.fs = new float[]{1.23f, 1.234f, 1.2345f};

        t1.i = 1;
        t1.is = new int[]{2, 3, 4};

        t1.l = 123456789L;
        t1.ls = new long[]{123456789L, 1234567890L, 9876543210L};

        t1.s = "hello \"world\"";
        t1.ss = new ArrayList<String>();
        t1.ss.add("hello");
        t1.ss.add("<![CDATA[<xml>]]>");
        t1.ss.add("!");
        //        t.t2 = t1;//循环引用
        t1.t2 = t;
        t1.t2s = new ArrayList<TestObj2>();
        t1.t2s.add(t);
        t1.t2s.add(t);
        t1.t2s.add(t);
        t1.t2s.add(t);

        ByteArrayOutputStream out1 = new ByteArrayOutputStream();
        POJOSerializerProvider.getSerializer(TestObj2.class).toXml(t1, out1, true);
        System.out.println(new String(out1.toByteArray(), ConstField.UTF8));
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        POJOSerializerProvider.getSerializer(TestObj2.class).toJson(t1, out2, true);
        System.out.println(new String(out2.toByteArray(), ConstField.UTF8));

        //        long start = System.currentTimeMillis();
        //        String jsonStr = null;
        //        for (int i = 0; i < 10000; i++) {
        //            jsonStr = JSON.toJSONString(t1);
        //        }
        //        long end = System.currentTimeMillis();
        //        System.out.println("tojsonStr cost:" + (end - start) + " jsonStr:" + jsonStr);
        //        start = System.currentTimeMillis();
        //        Object jsonObj = null;
        //        for (int i = 0; i < 10000; i++) {
        //            jsonObj = JSON.toJSON(t1);
        //        }
        //        end = System.currentTimeMillis();
        //        System.out.println("tojson cost:" + (end - start) + " jsonStr:" + jsonObj.toString());
    }
}
