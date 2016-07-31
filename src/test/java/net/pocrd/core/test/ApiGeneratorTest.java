package net.pocrd.core.test;

import net.pocrd.annotation.ApiGroup;
import net.pocrd.annotation.ApiParameter;
import net.pocrd.annotation.HttpApi;
import net.pocrd.core.ApiManager;
import net.pocrd.core.generator.ApiSdkJavaGenerator;
import net.pocrd.core.generator.ApiSdkJavaScriptGenerator;
import net.pocrd.core.generator.ApiSdkObjectiveCGenerator;
import net.pocrd.core.generator.HtmlApiDocGenerator;
import net.pocrd.define.ApiOpenState;
import net.pocrd.define.SecurityType;
import net.pocrd.entity.AbstractReturnCode;
import net.pocrd.entity.ApiMethodInfo;
import net.pocrd.util.RawString;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

/**
 * Created by guankaiqiang521 on 2014/9/24.
 */
public class ApiGeneratorTest {
    public static class ApiTestReturnCode extends AbstractReturnCode {
        public static final int _C_TEST_UNKNOW_ERROR = 2900000;

        protected ApiTestReturnCode(String desc, int code) {
            super(desc, code);
        }

        public static final ApiTestReturnCode TEST_UNKNOW_ERROR = new ApiTestReturnCode("测试类未知错误", 2900000);
    }

    @ApiGroup(name = "apitest", minCode = 0, maxCode = 3000000, codeDefine = ApiTestReturnCode.class, owner = "sunji180")
    public abstract interface ApiFunctionTestService {
        @HttpApi(name = "apitest.testWeiXin", desc = "微信接口测试", security = SecurityType.Integrated, owner = "guankaiqiang",
                 state = ApiOpenState.OPEN)
        public abstract RawString testWeiXin(
                @ApiParameter(required = false, name = "msg", desc = "test")
                        String paramString);
    }

    @Test
    public void testHtmlDocGenertor() {
        List<ApiMethodInfo> infoList = ApiManager.parseApi(ApiFunctionTestService.class, new Object());
        new HtmlApiDocGenerator.Builder().setOutputPath("/home/admin/tmp/").setXsltPath("http://dl.fengqucdn.com/tmp/java.xslt").build()
                .generateViaApiMethodInfo(infoList);
    }

    @Test
    public void testJavaGenertor() throws ParserConfigurationException, IOException, SAXException {
        List<ApiMethodInfo> infoList = ApiManager.parseApi(ApiFunctionTestService.class, new Object());
        new ApiSdkJavaGenerator.Builder().setOutputPath("/home/admin/tmp/").build().generateViaApiMethodInfo(infoList);
    }

    @Test
    public void testJavaGenertorViaJar() throws ParserConfigurationException, IOException, SAXException {
        new ApiSdkJavaGenerator.Builder().setOutputPath("/home/admin/tmp/").build()
                .generateViaJar("/home/admin/api/tmp/discovery-api-0.2.5-SNAPSHOT.jar");
    }

    @Test
    public void testObjcGenertor() throws ParserConfigurationException, IOException, SAXException {
        List<ApiMethodInfo> infoList = ApiManager.parseApi(ApiFunctionTestService.class, new Object());
        new ApiSdkObjectiveCGenerator.Builder().setOutputPath("/home/admin/tmp/").setClassPrefix("SF").build().generateViaApiMethodInfo(infoList);
    }

    @Test
    public void testGenerateFromNetResource() {
        new ApiSdkObjectiveCGenerator.Builder().setOutputPath("/home/admin/autogen/oc").setClassPrefix("SF").build().generateWithApiInfo(
                "http://localhost:8080/info.api?raw");
        new ApiSdkJavaGenerator.Builder().setOutputPath("/home/admin/autogen/java").setPackagePrefix("com.sfht.m.app.client").build()
                .generateWithApiInfo("http://localhost:8080/info.api?raw");
    }

    @Test
    public void testJsGenertor() throws ParserConfigurationException, IOException, SAXException {
        List<ApiMethodInfo> infoList = ApiManager.parseApi(ApiFunctionTestService.class, new Object());
        new ApiSdkJavaScriptGenerator.Builder().setOutputPath("/home/admin/autogen/js/").build().generateViaApiMethodInfo(infoList);
    }

    @Test
    public void testGenerateJsSdkFromNetResource() {
        ApiSdkJavaScriptGenerator instance = new ApiSdkJavaScriptGenerator.Builder().setOutputPath("/home/admin/autogen/js")
                .setPackagePrefix("sf.b2c.mall").build();
        instance.setApiGroups("logistics,order,payment,products,shopcart,user");
        instance.setSecurityTypes(SecurityType.UserLogin, SecurityType.None, SecurityType.RegisteredDevice);
        instance.generateWithApiInfo("http://115.28.160.84/info.api?raw");
    }
}
