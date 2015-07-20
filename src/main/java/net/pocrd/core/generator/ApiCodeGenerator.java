package net.pocrd.core.generator;

import net.pocrd.define.SecurityType;
import net.pocrd.entity.CodeGenConfig;
import net.pocrd.entity.CommonConfig;
import net.pocrd.util.WebRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<String>       apiGroupToGen        = null;
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
        String apiEvaluate = null, seTmp = null, groupTmp = null;
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
        if (seTmp == null && groupTmp == null) {
            apiEvaluate = "//Document/apiList/api[" + DEFAULT_API_EVALUATE_CONDITION + "]";
        } else if (seTmp != null && groupTmp == null) {
            apiEvaluate = "//Document/apiList/api[" + seTmp + "]";
        } else if (groupTmp != null && seTmp == null) {
            apiEvaluate = "//Document/apiList/api[" + DEFAULT_API_EVALUATE_CONDITION + " and " + groupTmp + "]";
        } else {
            apiEvaluate = "//Document/apiList/api[" + seTmp + " and " + groupTmp + "]";
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

    public Source getXsltSource(String targetSite, Source defaultSource) {
        Source xslSource = defaultSource;
        InputStream swapStream = null;
        if (targetSite != null && !targetSite.isEmpty()) {
            try {
                byte[] xslt = WebRequestUtil.getResponseBytes(targetSite, null, true);
                swapStream = transformInputStream(new ByteArrayInputStream(xslt));
                xslSource = new StreamSource(swapStream);
            } catch (Exception e) {
                logger.warn("get xslt failed from site:{},will use default xslt to generate doc", CommonConfig.getInstance().getApiInfoXslSite());
                System.out.println("get xslt failed from site:" +
                                           CommonConfig.getInstance().getApiInfoXslSite() + ",will use default xslt to generate doc");
            }
        }
        return xslSource;
    }
    /**
     * 转换模板，替换xslt中的定制元素
     *
     * @param inputStream
     *
     * @return
     */
    public abstract InputStream transformInputStream(InputStream inputStream);

    /**
     * 使用xslt进行代码生成
     *
     * @param inputStream
     */
    public abstract void generate(InputStream inputStream);

    /**
     * 访问指定站点获取数据源
     *
     * @param website xml下载地址
     */
    public void generateWithNetResource(String website) {
        byte[] bytes = WebRequestUtil.getResponseBytes(website, null, true);
        if (bytes != null) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            generate(byteArrayInputStream);
        } else {
            logger.error("generate code failed with resource form " + website);
            throw new RuntimeException("generate code failed with resource form " + website);
        }
    }
}
