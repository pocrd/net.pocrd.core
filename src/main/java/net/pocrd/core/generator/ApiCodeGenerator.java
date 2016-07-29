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
    private static final Logger logger = LoggerFactory.getLogger(ApiCodeGenerator.class);
    /**
     * only generte sdk when securityLevel is None, RegisteredDevice, UserTrustedDevice, MobileOwner, MobileOwnerTrustedDevice, UserLogin, UserLoginAndMobileOwner
     */
    private static String DEFAULT_API_EVALUATE_CONDITION;
    private Set<SecurityType> securityTypeSetToGen = null;
    private Set<String> apiGroupToGen = null;
    private Set<String> rejectApis = null;

    static {
        StringBuilder sb = new StringBuilder("(securityLevel =");
        for (SecurityType securityType : SecurityType.values()) {
            if (securityType != SecurityType.Document && securityType != SecurityType.Test) {
                sb.append("'").append(securityType).append("' or securityLevel = ");
            }
        }
        String tmp = sb.toString();
        tmp = tmp.substring(0, tmp.lastIndexOf(" or securityLevel = ")) + ")";
        DEFAULT_API_EVALUATE_CONDITION = tmp;
    }

    protected String getApiEvaluate() {
        String apiEvaluate = null, seTmp = null, groupTmp = null, rejectApiTmp = null;
        if (securityTypeSetToGen != null && securityTypeSetToGen.size() > 0) {
            StringBuilder sb = new StringBuilder("(securityLevel =");
            for (SecurityType securityType : securityTypeSetToGen) {
                sb.append("'").append(securityType).append("' or securityLevel = ");
            }
            seTmp = sb.toString();
            seTmp = seTmp.substring(0, seTmp.lastIndexOf(" or securityLevel = "));
            seTmp = seTmp + ")";
        }
        if (apiGroupToGen != null && apiGroupToGen.size() > 0) {
            StringBuilder sb = new StringBuilder("(groupName =");
            for (String groupName : apiGroupToGen) {
                sb.append("'").append(groupName).append("' or groupName = ");
            }
            groupTmp = sb.toString();
            groupTmp = groupTmp.substring(0, groupTmp.lastIndexOf(" or groupName = "));
            groupTmp = groupTmp + ")";
        }
        if (rejectApis != null && rejectApis.size() > 0) {
            StringBuilder sb = new StringBuilder("(methodName !=");
            for (String api : rejectApis) {
                sb.append("'").append(api).append("' and methodName != ");
            }
            rejectApiTmp = sb.toString();
            rejectApiTmp = rejectApiTmp.substring(0, rejectApiTmp.lastIndexOf(" and methodName != "));
            rejectApiTmp = rejectApiTmp + ")";
        }
        seTmp = seTmp == null ? DEFAULT_API_EVALUATE_CONDITION : seTmp;
        if (groupTmp == null && rejectApiTmp == null) {
            apiEvaluate = "//Document/apiList/api[" + seTmp + "]";
        } else if (groupTmp != null && rejectApiTmp == null) {
            apiEvaluate = "//Document/apiList/api[" + seTmp + " and " + groupTmp + "]";
        } else if (groupTmp == null) {
            apiEvaluate = "//Document/apiList/api[" + seTmp + " and " + rejectApiTmp + "]";
        } else {
            apiEvaluate = "//Document/apiList/api[" + seTmp + " and " + groupTmp + " and " + rejectApiTmp + "]";
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
            securityTypeSetToGen = new HashSet<SecurityType>();
            securityTypeSetToGen.addAll(Arrays.asList(securityTypes));
        }
    }

    public void setSecurityTypes(List<SecurityType> securityTypes) {
        if (securityTypes != null && securityTypes.size() > 0) {
            securityTypeSetToGen = new HashSet<SecurityType>();
            securityTypeSetToGen.addAll(securityTypes);
        }
    }

    public void setApiGroups(List<String> apiGroups) {
        if (apiGroups != null && apiGroups.size() > 0) {
            apiGroupToGen = new HashSet<String>();
            apiGroupToGen.addAll(apiGroups);
        }
    }

    public void setApiGroups(String[] apiGroups) {
        if (apiGroups != null && apiGroups.length > 0) {
            apiGroupToGen = new HashSet<String>();
            apiGroupToGen.addAll(Arrays.asList(apiGroups));
        }
    }

    public Set<String> getRejectApis() {
        return rejectApis;
    }

    public void setRejectApis(List<String> rejectApis) {
        if (rejectApis != null && rejectApis.size() > 0) {
            this.rejectApis = new HashSet<String>();
            this.rejectApis.addAll(rejectApis);
        }
    }

    public void setRejectApis(String[] rejectApis) {
        if (rejectApis != null && rejectApis.length > 0) {
            this.rejectApis = new HashSet<String>();
            this.rejectApis.addAll(Arrays.asList(rejectApis));
        }
    }

    public Source getXsltSource(String targetSite, Source defaultSource) {
        Source xslSource = defaultSource;
        InputStream swapStream = null;
        if (targetSite != null && !targetSite.isEmpty()) {
            try {
                byte[] xslt = WebRequestUtil.getResponseBytes(targetSite, null, true);
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
        byte[] bytes = WebRequestUtil.getResponseBytes(apiInfoUrl, null, true);
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
                    new URL[]{new URL("file:" + jarFilePath)},
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
