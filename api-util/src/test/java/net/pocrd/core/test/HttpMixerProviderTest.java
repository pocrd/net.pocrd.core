package net.pocrd.core.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.pocrd.core.ApiManager;
import net.pocrd.core.test.model.DemoMixer;
import net.pocrd.core.test.model.MixData_A;
import net.pocrd.core.test.model.MixData_B;
import net.pocrd.define.ApiMixer;
import net.pocrd.entity.ApiMethodInfo;
import net.pocrd.util.HttpMixerProvider;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by rendong on 2018/6/8.
 */

public class HttpMixerProviderTest {
    public static final MixData_A a = new MixData_A();
    public static final MixData_B b = new MixData_B();

    static {
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();//disable循环引用
        //            JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteMapNullValue;//null属性，序列化为null,do by guankaiqiang,android sdk中 JSON.optString()将null convert成了"null",故关闭该特性
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.NotWriteRootClassName.getMask();
        //            JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteEnumUsingToString.getMask();
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteNullNumberAsZero.getMask();
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteNullBooleanAsFalse.getMask();
    }

    static {
        a.a6 = new MixData_A();
        a.a7 = new ArrayList<>(3);
        a.a7.add(new MixData_A());
        a.a7.add(new MixData_A());
        a.a7.add(new MixData_A());

        b.b6 = new MixData_B();
        b.b7 = new ArrayList<>(3);
        b.b7.add(new MixData_B());
        b.b7.add(new MixData_B());
        b.b7.add(new MixData_B());
    }

    @Test
    public void testMixer() {
        ApiManager manager = new ApiManager();
        ApiMethodInfo info = ApiManager.parseMixer(DemoMixer.class);
        ApiMixer mixer = HttpMixerProvider.getMixerExecutor("tester", info);
        DemoMixer.Result r = (DemoMixer.Result)mixer.execute(new Object[] { a, b });
        String as = JSON.toJSONString(a);
        String ars = JSON.toJSONString(r.a);
        Assert.assertEquals(as, ars);
        System.out.println(as);
        System.out.println(ars);

        String bs = JSON.toJSONString(b);
        String brs = JSON.toJSONString(r.b);
        Assert.assertEquals(bs, brs);
        System.out.println(bs);
        System.out.println(brs);
    }

}
