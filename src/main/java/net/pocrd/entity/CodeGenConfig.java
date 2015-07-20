package net.pocrd.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by guankaiqiang521 on 2014/9/28.
 */
public class CodeGenConfig {
    private static final Logger logger = LoggerFactory.getLogger(CodeGenConfig.class);
    private static CodeGenConfig instance;

    private CodeGenConfig() {}

    public static final void init(Properties prop) {
        synchronized (CodeGenConfig.class) {
            if (instance == null) {
                instance = new CodeGenConfig();
            }
            if (prop == null) {
                throw new RuntimeException("codegen config init failed.");
            } else {
                instance.setApiInfoXslSite(prop.getProperty("net.pocrd.apiInfoXslSite"));
                instance.setHtmlApiDocLocation(prop.getProperty("net.pocrd.htmlApiDocLocation", "/info.html"));
                instance.setJavaXsltSite(prop.getProperty("net.pocrd.javaXsltSite"));
                instance.setApiSdkJavaLocation(appendSuffixWhenNotEndWith(prop.getProperty("net.pocrd.apiSdkJavaLocation", "/java/"), "/"));
                instance.setApiSdkJavaPkgName(prop.getProperty("net.pocrd.apiSdkJavaPkgName", "net.pocrd.autogen"));
                instance.setObjcXsltSite(prop.getProperty("net.pocrd.objcXsltSite"));
                instance.setApiSdkObjcLocation(appendSuffixWhenNotEndWith(prop.getProperty("net.pocrd.apiSdkObjcLocation", "/objc/"), "/"));
                instance.setJsXsltSite(prop.getProperty("net.pocrd.jsXsltSite"));
                instance.setApiSdkJsLocation(appendSuffixWhenNotEndWith(prop.getProperty("net.pocrd.apiSdkJsLocation", "/js/"), "/"));
                instance.setApiSdkJsPkgName(prop.getProperty("net.pocrd.apiSdkJsPkgName", "net.pocrd.autogen"));
                instance.setApiSdkObjcClassPrefix(prop.getProperty("net.pocrd.apiSdkObjcClassPrefix", "HT"));
            }
        }
    }

    public static final CodeGenConfig getInstance() {
        if (instance == null) {
            init(null);
        }
        return instance;
    }

    private String apiInfoXslSite;

    /**
     * 下载apiInfo.xsl的地址，全路径地址
     *
     * @param apiInfoXslSite
     */
    void setApiInfoXslSite(String apiInfoXslSite) {
        this.apiInfoXslSite = apiInfoXslSite;
        logger.info("[CodeGenConfig.init]net.pocrd.apiInfoXslSite:{}", this.apiInfoXslSite);
    }
    public String getApiInfoXslSite() {
        return this.apiInfoXslSite;
    }

    private String htmlApiDocLocation;
    /**
     * APIdoc生成的路径
     *
     * @param location
     */
    void setHtmlApiDocLocation(String location) {
        this.htmlApiDocLocation = location;
        logger.info("[CodeGenConfig.init]net.pocrd.htmlApiDocLocation:{}", this.htmlApiDocLocation);
    }
    public String getHtmlApiDocLocation() {
        return this.htmlApiDocLocation;
    }

    private String javaXsltSite;
    /**
     * 下载java.xslt的地址，全路径地址
     *
     * @param javaXsltSite
     */
    void setJavaXsltSite(String javaXsltSite) {
        this.javaXsltSite = javaXsltSite;
        logger.info("[CodeGenConfig.init]net.pocrd.javaXsltSite:{}", this.javaXsltSite);
    }
    public String getJavaXsltSite() {
        return this.javaXsltSite;
    }

    private String apiSdkJavaLocation;
    /**
     * 生成的java调用代码的所在目录
     *
     * @param location
     */
    void setApiSdkJavaLocation(String location) {
        this.apiSdkJavaLocation = location;
        logger.info("[CodeGenConfig.init]net.pocrd.apiSdkJavaLocation:{}", this.apiSdkJavaLocation);
    }

    public String getApiSdkJavaLocation() {
        return this.apiSdkJavaLocation;
    }

    private String objcXsltSite;
    /**
     * 下载objc.xslt的地址，全路径地址
     *
     * @param objcXsltSite
     */
    void setObjcXsltSite(String objcXsltSite) {
        this.objcXsltSite = objcXsltSite;
        logger.info("[CodeGenConfig.init]net.pocrd.objcXsltSite:{}", this.objcXsltSite);
    }
    public String getObjcXsltSite() {
        return this.objcXsltSite;
    }

    private String apiSdkObjcLocation;
    /**
     * 生成的Objective-C调用代码的所在目录
     *
     * @param location
     */
    void setApiSdkObjcLocation(String location) {
        this.apiSdkObjcLocation = location;
        logger.info("[CodeGenConfig.init]net.pocrd.apiSdkObjcLocation:{}", this.apiSdkObjcLocation);
    }

    public String getApiSdkObjcLocation() {
        return this.apiSdkObjcLocation;
    }

    private String jsXsltSite;
    /**
     * 下载js.xslt的地址，全路径地址
     *
     * @param JsXsltSite
     */
    void setJsXsltSite(String JsXsltSite) {
        this.jsXsltSite = jsXsltSite;
        logger.info("[CodeGenConfig.init]net.pocrd.jsXsltSite:{}", this.jsXsltSite);
    }
    public String getJsXsltSite() {
        return this.jsXsltSite;
    }

    private String apiSdkJsLocation;
    /**
     * 生成的javaScript调用代码的所在目录
     *
     * @param location
     */
    void setApiSdkJsLocation(String location) {
        this.apiSdkJsLocation = location;
        logger.info("[CodeGenConfig.init]net.pocrd.apiSdkJsLocation:{}", this.apiSdkJsLocation);
    }

    public String getApiSdkJsLocation() {
        return this.apiSdkJsLocation;
    }

    private String apiSdkJsPkgName;

    /**
     * 生成的js代码的包名
     *
     * @param pkgName
     */
    void setApiSdkJsPkgName(String pkgName) {
        this.apiSdkJsPkgName = pkgName;
        logger.info("[CodeGenConfig.init]net.pocrd.apiSdkJsPkgName:{}", this.apiSdkJsPkgName);
    }

    public String getApiSdkJsPkgName() {
        return this.apiSdkJsPkgName;
    }

    private String apiSdkJavaPkgName;

    /**
     * 生成的java代码的包名
     *
     * @param pkgName
     */
    void setApiSdkJavaPkgName(String pkgName) {
        this.apiSdkJavaPkgName = pkgName;
        logger.info("[CodeGenConfig.init]net.pocrd.apiSdkJavaPkgName:{}", this.apiSdkJavaPkgName);
    }

    public String getApiSdkJavaPkgName() {
        return this.apiSdkJavaPkgName;
    }

    /**
     * 如果结尾不是suffix，进行append
     *
     * @param str
     * @param suffix
     *
     * @return
     */
    private static String appendSuffixWhenNotEndWith(String str, String suffix) {
        if (str != null && str.length() > 0 && suffix != null && suffix.length() > 0) {
            if (str.endsWith(suffix)) {
                return str;
            } else {
                return str + suffix;
            }
        }
        return str;
    }

    private String apiSdkObjcClassPrefix;

    void setApiSdkObjcClassPrefix(String apiSdkObjcClassPrefix) {
        this.apiSdkObjcClassPrefix = apiSdkObjcClassPrefix;
        logger.info("[CodeGenConfig.init]net.pocrd.apiSdkObjcClassPrefix:{}", this.apiSdkObjcClassPrefix);
    }

    public String getApiSdkObjcClassPrefix() {
        return this.apiSdkObjcClassPrefix;
    }
}
