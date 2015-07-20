package net.pocrd.core.test;

import net.pocrd.annotation.ApiGroup;
import net.pocrd.annotation.ApiParameter;
import net.pocrd.annotation.HttpApi;
import net.pocrd.core.ApiDocumentationHelper;
import net.pocrd.core.ApiManager;
import net.pocrd.core.generator.ApiSdkJavaGenerator;
import net.pocrd.core.generator.ApiSdkJavaScriptGenerator;
import net.pocrd.core.generator.ApiSdkObjectiveCGenerator;
import net.pocrd.core.generator.HtmlApiDocGenerator;
import net.pocrd.define.ApiOpenState;
import net.pocrd.define.SecurityType;
import net.pocrd.define.Serializer;
import net.pocrd.document.Document;
import net.pocrd.entity.AbstractReturnCode;
import net.pocrd.entity.ApiMethodInfo;
import net.pocrd.entity.CodeGenConfig;
import net.pocrd.responseEntity.RawString;
import net.pocrd.util.POJOSerializerProvider;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

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
        Properties prop = new Properties();
        prop.setProperty("net.pocrd.htmlApiDocLocation", "/tmp/test.html");
        prop.setProperty("net.pocrd.apiInfoXslSite", "http://115.29.16.189/xslt/java.xsl");
        CodeGenConfig.init(prop);
        List<ApiMethodInfo> infoList = ApiManager.parseApi(ApiFunctionTestService.class, new Object());
        ApiMethodInfo[] array = new ApiMethodInfo[infoList.size()];
        infoList.toArray(array);
        Document document = new ApiDocumentationHelper().getDocument(array);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Serializer<Document> docs = POJOSerializerProvider.getSerializer(Document.class);
        docs.toXml(document, outputStream, true);
        ByteArrayInputStream swapStream = new ByteArrayInputStream(outputStream.toByteArray());
        HtmlApiDocGenerator.getInstance().generate(swapStream);
    }

    @Test
    public void testJavaGenertor() throws ParserConfigurationException, IOException, SAXException {
        Properties prop = new Properties();
        prop.setProperty("net.pocrd.apiSdkJavaLocation", "/tmp/");
        CodeGenConfig.init(prop);
        List<ApiMethodInfo> infoList = ApiManager.parseApi(ApiFunctionTestService.class, new Object());
        ApiMethodInfo[] array = new ApiMethodInfo[infoList.size()];
        infoList.toArray(array);
        Serializer<Document> docs = POJOSerializerProvider.getSerializer(Document.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        docs.toXml(new ApiDocumentationHelper().getDocument(array), outputStream, true);
        ByteArrayInputStream swapStream = new ByteArrayInputStream(outputStream.toByteArray());
        ApiSdkJavaGenerator.getInstance().generate(swapStream);
    }

    @Test
    public void testObjcGenertor() throws ParserConfigurationException, IOException, SAXException {
        Properties prop = new Properties();
        prop.setProperty("net.pocrd.apiSdkObjcLocation", "/tmp/");
        prop.setProperty("net.pocrd.apiSdkObjcClassPrefix", "SF");
        CodeGenConfig.init(prop);
        List<ApiMethodInfo> infoList = ApiManager.parseApi(ApiFunctionTestService.class, new Object());
        ApiMethodInfo[] array = new ApiMethodInfo[infoList.size()];
        infoList.toArray(array);
        Serializer<Document> docs = POJOSerializerProvider.getSerializer(Document.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        docs.toXml(new ApiDocumentationHelper().getDocument(array), outputStream, true);
        ByteArrayInputStream swapStream = new ByteArrayInputStream(outputStream.toByteArray());
        ApiSdkObjectiveCGenerator.getInstance().generate(swapStream);
    }

    @Test
    public void testGenerateFromNetResource() {
        Properties prop = new Properties();
        prop.setProperty("net.pocrd.apiSdkObjcLocation", "/tmp/");
        prop.setProperty("net.pocrd.apiSdkObjcClassPrefix", "SF");
        CodeGenConfig.init(prop);
        ApiSdkObjectiveCGenerator.getInstance().generateWithNetResource("http://115.28.145.123/info.api?raw");
    }

    @Test
    public void testJsGenertor() throws ParserConfigurationException, IOException, SAXException {
        Properties prop = new Properties();
        prop.setProperty("net.pocrd.apiSdkJsLocation", "/tmp/js/");
        CodeGenConfig.init(prop);
        List<ApiMethodInfo> infoList = ApiManager.parseApi(ApiFunctionTestService.class, new Object());
        ApiMethodInfo[] array = new ApiMethodInfo[infoList.size()];
        infoList.toArray(array);
        Serializer<Document> docs = POJOSerializerProvider.getSerializer(Document.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        docs.toXml(new ApiDocumentationHelper().getDocument(array), outputStream, true);
        ByteArrayInputStream swapStream = new ByteArrayInputStream(outputStream.toByteArray());
        ApiSdkJavaScriptGenerator.getInstance().generate(swapStream);
    }

    @Test
    public void testGenerateJsSdkFromNetResource() {
        Properties prop = new Properties();
        prop.setProperty("net.pocrd.apiSdkJsLocation", "/tmp/js/");
        prop.setProperty("net.pocrd.apiSdkJsPkgName", "sf.b2c.mall");
        CodeGenConfig.init(prop);
        ApiSdkJavaScriptGenerator instance = ApiSdkJavaScriptGenerator.getInstance();
        instance.setApiGroups(new String[] { "logistics", "order", "payment", "products", "shopcart", "user" });
        instance.setSecurityTypes(SecurityType.UserLogin, SecurityType.None, SecurityType.RegisteredDevice);
        instance.generateWithNetResource("http://115.28.145.123/info.api?raw");
    }
}
