package net.pocrd.core.test.model;

import net.pocrd.annotation.Description;
import net.pocrd.annotation.HttpDataMixer;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rendong on 2018/6/8.
 */
@HttpDataMixer(name = "demo.demoMix", desc = "测试mixer", owner = "demo", pagePath = "/demo.html")
public class DemoMixer {
    @Description("Result")
    public static class Result implements Serializable {
        @Description("a")
        public A a;
        @Description("b")
        public B b;
    }

    @Description("A")
    public static class A implements Serializable {
        @Description("a1")
        public int     a1;
        @Description("a2")
        public long    a2;
        @Description("a3")
        public byte    a3;
        @Description("a4")
        public String  a4;
        @Description("a5")
        public boolean a5;
        @Description("a6")
        public A       a6;
        @Description("a7")
        public List<A> a7;

        public A() {
        }
    }

    @Description("B")
    public static class B implements Serializable {
        @Description("b1")
        public int     b1;
        @Description("b2")
        public long    b2;
        @Description("b3")
        public byte    b3;
        @Description("b4")
        public String  b4;
        @Description("b5")
        public boolean b5;
        @Description("b6")
        public B       b6;
        @Description("b7")
        public List<B> b7;

        public B() {
        }
    }

    public static Result mix(A a, B b) {
        Result r = new Result();
        r.a = a;
        r.b = b;
        return r;
    }
}
