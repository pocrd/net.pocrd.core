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
import net.pocrd.entity.CommonConfig;
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
    public interface ApiFunctionTestService {
        @HttpApi(name = "apitest.testWeiXin", desc = "微信接口测试", security = SecurityType.Integrated, owner = "guankaiqiang",
                 state = ApiOpenState.OPEN)
        RawString testWeiXin(
                @ApiParameter(required = false, name = "msg", desc = "test")
                        String paramString);
    }

    @Test
    public void testHtmlDocGenertor() {
        List<ApiMethodInfo> infoList = ApiManager.parseApi(ApiFunctionTestService.class);
        new HtmlApiDocGenerator.Builder().setOutputPath(CommonConfig.getInstance().getAutogenPath() + "/html")
                .setXsltPath("http://www.pocrd.net/xslt/java.xslt").build()
                .generateViaApiMethodInfo(infoList);
    }

    @Test
    public void testJavaGenertor() throws ParserConfigurationException, IOException, SAXException {
        List<ApiMethodInfo> infoList = ApiManager.parseApi(ApiFunctionTestService.class);
        new ApiSdkJavaGenerator.Builder().setOutputPath(CommonConfig.getInstance().getAutogenPath() + "/java").build()
                .generateViaApiMethodInfo(infoList);
    }

    @Test
    public void testJavaGenertorViaJar() throws ParserConfigurationException, IOException, SAXException {
        new ApiSdkJavaGenerator.Builder().setOutputPath(CommonConfig.getInstance().getAutogenPath() + "/javaFromJar").build()
                .generateViaJar("/Users/rendong/workspace/github/net.pocrd.api/apigw-test/apigwtest-api/target/apigwtest-api.jar");
    }

    @Test
    public void testObjcGenertor() throws ParserConfigurationException, IOException, SAXException {
        List<ApiMethodInfo> infoList = ApiManager.parseApi(ApiFunctionTestService.class);
        new ApiSdkObjectiveCGenerator.Builder().setOutputPath(CommonConfig.getInstance().getAutogenPath() + "/objectivec").setClassPrefix("PoC")
                .build()
                .generateViaApiMethodInfo(infoList);
    }

    @Test
    public void testGenerateFromNetResource() {
        new ApiSdkObjectiveCGenerator.Builder().setOutputPath(CommonConfig.getInstance().getAutogenPath() + "/ocFromNet").setClassPrefix("PoC")
                .build().generateWithApiInfo("http://www.pocrd.net/info.api?raw");
        new ApiSdkJavaGenerator.Builder().setOutputPath(CommonConfig.getInstance().getAutogenPath() + "/javaFromNet")
                .setPackagePrefix("net.pocrd.app.client").build()
                .generateWithApiInfo("http://www.pocrd.net/info.api?raw");
    }

    @Test
    public void testJsGenertor() throws ParserConfigurationException, IOException, SAXException {
        List<ApiMethodInfo> infoList = ApiManager.parseApi(ApiFunctionTestService.class);
        new ApiSdkJavaScriptGenerator.Builder().setOutputPath(CommonConfig.getInstance().getAutogenPath() + "/js")
                .build().generateViaApiMethodInfo(infoList);
    }

    @Test
    public void testGenerateJsSdkFromNetResource() {
        ApiSdkJavaScriptGenerator instance = new ApiSdkJavaScriptGenerator.Builder()
                .setOutputPath(CommonConfig.getInstance().getAutogenPath() + "/jsFromNet")
                .setPackagePrefix("net.pocrd.app.client").build();
        instance.setApiGroups("logistics,order,payment,products,shopcart,user");
        instance.setSecurityTypes(SecurityType.UserLogin, SecurityType.None, SecurityType.RegisteredDevice);
        instance.generateWithApiInfo("http://www.pocrd.net/info.api?raw");
    }
}
