package net.pocrd.core.generator;

import net.pocrd.core.ApiDocumentationHelper;
import net.pocrd.core.ApiManager;
import net.pocrd.define.SecurityType;
import net.pocrd.define.Serializer;
import net.pocrd.document.Document;
import net.pocrd.entity.ApiMethodInfo;
import net.pocrd.entity.CommonConfig;
import net.pocrd.util.POJOSerializerProvider;
import net.pocrd.util.WebRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarFile;

/**
 * Created by guankaiqiang521 on 2014/9/26.
 */
public abstract class ApiCodeGenerator {
    private static final Logger logger                = LoggerFactory.getLogger(ApiCodeGenerator.class);
    /**
     * only generte sdk when securityLevel is None, RegisteredDevice, UserTrustedDevice, MobileOwner, MobileOwnerTrustedDevice, UserLogin, UserLoginAndMobileOwner
     */
    private              String accept_security_types = null; // default all
    private              String accept_group_names    = null; // default all
    private              String reject_api_names      = null; // default all

    protected String getApiEvaluate() {
        String apiEvaluate = null;
        StringBuilder acceptSecurityTypes = new StringBuilder();
        StringBuilder acceptGroupNames = new StringBuilder();
        StringBuilder acceptApiNames = new StringBuilder();

        if (accept_security_types != null && accept_security_types.length() > 0) {
            acceptSecurityTypes.append("(securityLevel='");
            acceptSecurityTypes.append(accept_security_types.replace(",", "' or securityLevel='"));
            acceptSecurityTypes.append("')");
        }
        if (accept_group_names != null && accept_group_names.length() > 0) {
            acceptGroupNames.append("(groupName='");
            acceptGroupNames.append(accept_group_names.replace(",", "' or groupName='"));
            acceptGroupNames.append("')");
        }
        if (reject_api_names != null && reject_api_names.length() > 0) {
            acceptApiNames.append("(methodName!='");
            acceptApiNames.append(reject_api_names.replace(",", "' and methodName!='"));
            acceptApiNames.append("')");
        }

        String query = "";
        if (accept_security_types != null) {
            query += acceptSecurityTypes.toString();
        }
        if (accept_group_names != null) {
            query += (query.length() > 0 ? " and " : "") + acceptGroupNames.toString();
        }
        if (reject_api_names != null) {
            query += (query.length() > 0 ? " and " : "") + acceptApiNames.toString();
        }
        if (query.length() == 0) {
            apiEvaluate = "//Document/apiList/api";
        } else {
            apiEvaluate = "//Document/apiList/api[" + query + "]";
        }
        System.out.println("[API EVALUATE] " + apiEvaluate);
        return apiEvaluate;
    }

    /**
     * 设置需要生成的接口安全级别
     *
     * @param securityTypes
     */
    public void setSecurityTypes(SecurityType... securityTypes) {
        if (securityTypes != null && securityTypes.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (SecurityType type : securityTypes) {
                sb.append(type.name()).append(",");
            }
            sb.setLength(sb.length() - 1);
            accept_security_types = sb.toString();
        }
    }

    public void setSecurityTypes(String securityTypes) {
        accept_security_types = securityTypes;
    }

    public void setApiGroups(String apiGroups) {
        accept_group_names = apiGroups;
    }
    public void setRejectApis(String rejectApis) {
        reject_api_names = rejectApis;
    }

    public Source getXsltSource(String targetSite, Source defaultSource) {
        Source xslSource = defaultSource;
        InputStream swapStream = null;
        if (targetSite != null && !targetSite.isEmpty()) {
            try {
                byte[] xslt = WebRequestUtil.getResponseBytes(targetSite, null);
                swapStream = customizeXslt(new ByteArrayInputStream(xslt));
                xslSource = new StreamSource(swapStream);
            } catch (Exception e) {
                logger.warn("get xslt failed from site:{},will use default xslt to generate doc", CommonConfig.getInstance().getApiInfoXslSite());
                System.out.println("get xslt failed from site:" +
                        CommonConfig.getInstance().getApiInfoXslSite() + ", will use default xslt to generate doc");
            }
        }
        return xslSource;
    }

    /**
     * 转换模板，替换xslt中的定制元素
     *
     * @param xslt
     */
    protected abstract InputStream customizeXslt(InputStream xslt);

    /**
     * 使用xslt进行代码生成
     *
     * @param apiInfo
     */
    public abstract void generate(InputStream apiInfo);

    /**
     * 访问指定站点获取数据源
     *
     * @param apiInfoUrl xml下载地址
     */
    public void generateWithApiInfo(String apiInfoUrl) {
        byte[] bytes = WebRequestUtil.getResponseBytes(apiInfoUrl, null);
        if (bytes != null) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            generate(byteArrayInputStream);
        } else {
            logger.error("generate code failed with resource form " + apiInfoUrl);
            throw new RuntimeException("generate code failed with resource form " + apiInfoUrl);
        }
    }

    public void generateViaJar(String jarFilePath) {
        JarFile jf = null;
        List<ApiMethodInfo> infoList = new LinkedList<ApiMethodInfo>();
        try {
            jf = new JarFile(jarFilePath);
            ClassLoader loader = URLClassLoader.newInstance(
                    new URL[] { new URL("file:" + jarFilePath) },
                    getClass().getClassLoader()
            );
            Thread.currentThread().setContextClassLoader(loader);
            if ("dubbo".equals(jf.getManifest().getMainAttributes().getValue("Api-Dependency-Type"))) {
                String ns = jf.getManifest().getMainAttributes().getValue("Api-Export");
                String[] names = ns.split(" ");
                for (String name : names) {
                    if (name != null) {
                        name = name.trim();
                        if (name.length() > 0) {
                            Class<?> clazz = Class.forName(name, true, loader);
                            infoList.addAll(ApiManager.parseApi(clazz, new Object()));
                        }
                    }
                }
            }
            if (infoList.size() > 0) {
                ApiMethodInfo[] array = new ApiMethodInfo[infoList.size()];
                infoList.toArray(array);
                Document document = new ApiDocumentationHelper().getDocument(array);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                Serializer<Document> docs = POJOSerializerProvider.getSerializer(Document.class);
                docs.toXml(document, outputStream, true);
                ByteArrayInputStream swapStream = new ByteArrayInputStream(outputStream.toByteArray());
                generate(swapStream);
            } else {
                logger.warn("info list is empty.");
            }
        } catch (Exception e) {
            throw new RuntimeException("generateViaJar failed.", e);
        }
    }

    public void generateViaApiMethodInfo(List<ApiMethodInfo> methods) {
        ApiMethodInfo[] array = new ApiMethodInfo[methods.size()];
        methods.toArray(array);
        Serializer<Document> docs = POJOSerializerProvider.getSerializer(Document.class);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        docs.toXml(new ApiDocumentationHelper().getDocument(array), outputStream, true);
        ByteArrayInputStream swapStream = new ByteArrayInputStream(outputStream.toByteArray());
        generate(swapStream);
    }
}
