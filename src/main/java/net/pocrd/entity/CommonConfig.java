package net.pocrd.entity;

import net.pocrd.util.Md5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Properties;

public class CommonConfig {
    private static final Logger logger = LoggerFactory.getLogger(CommonConfig.class);
    private static CommonConfig instance;

    private CommonConfig() {
    }

    public static final void init(Properties prop) {
        synchronized (CommonConfig.class) {
            if (instance == null) {
                instance = new CommonConfig();
            }
            if (prop == null) {
                throw new RuntimeException("common config init failed.");
            } else {
                instance.setAutogenPath(prop.getProperty("net.pocrd.autogenPath", "/tmp/autogen"));
                instance.setApigwVersion(prop.getProperty("net.pocrd.apigwVersion", "develop"));
                instance.setApiInfoXslSite(prop.getProperty("net.pocrd.apiInfoXslSite"));
                instance.setOriginWhiteList(prop.getProperty("net.pocrd.originWhiteList"));
                instance.setDubboAsyncString(prop.getProperty("net.pocrd.dubboAsync"));
            }
            try {
                InetAddress addr = InetAddress.getLocalHost();
                instance.serverAddress = Md5Util.computeToHex(addr.getHostAddress().getBytes("UTF-8")).substring(0, 6);
                logger.info("apigw server,address:{},hash:{}", addr.getHostAddress(), instance.serverAddress);
            } catch (Exception e) {
                //启动时获取当前机器ip
                logger.error("can not get server address,_cid may be not unique", e);
            }
        }
    }

    public static final CommonConfig getInstance() {
        if (instance == null) {
            if (CompileConfig.isDebug) {
                init(new Properties());
            } else {
                throw new RuntimeException("common config not init.");
            }
        }
        return instance;
    }

    private String accessFileLoggerName;

    private void setAccessFileLoggerName(String accessFileLoggerName) {
        this.accessFileLoggerName = accessFileLoggerName;
        if (CompileConfig.isDebug) {
            logger.info("[CommonConfig.init]net.pocrd.accessLoggerName:{}", this.accessFileLoggerName);
        }
    }

    private String autogenPath;

    private void setAutogenPath(String autogenPath) {
        this.autogenPath = autogenPath;
        if (CompileConfig.isDebug) {
            logger.info("[CommonConfig.init]net.pocrd.autogenPath:{}", this.autogenPath);
        }
    }

    public String getAutogenPath() {
        return autogenPath;
    }

    /**
     * md5hash(server ip address)，用来串联_cid,_cid=address|thread|time
     */
    private String serverAddress;

    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * 当前apigw发布的版本号
     */
    private String apigwVersion;

    private void setApigwVersion(String apigwVersion) {
        this.apigwVersion = apigwVersion;
        if (CompileConfig.isDebug) {
            logger.info("[CommonConfig.init]net.pocrd.apigwVersion:{}", this.apigwVersion);
        }
    }

    public String getApigwVersion() {
        return apigwVersion;
    }

    /**
     * 下载apiInfo.xsl的地址，全路径地址
     */
    private String apiInfoXslSite;

    private void setApiInfoXslSite(String apiInfoXslSite) {
        this.apiInfoXslSite = apiInfoXslSite;
        if (CompileConfig.isDebug) {
            logger.info("[CommonConfig.init]net.pocrd.apiInfoXslSite:{}", this.apiInfoXslSite);
        }
    }

    public String getApiInfoXslSite() {
        return this.apiInfoXslSite;
    }

    private HashMap<String, String> originWhiteList = new HashMap<String, String>();

    private void setOriginWhiteList(String list) {
        if (list != null && list.length() > 0) {
            String[] os = list.split(",");
            for (String o : os) {
                String domain = o.trim();
                int index = domain.lastIndexOf('.', domain.lastIndexOf('.', domain.length()) - 1);
                originWhiteList.put(domain, domain.substring(index));
            }
        }
    }

    /**
     * dubbo 以异步方式进行调用，会影响到接口执行时间统计
     */
    private boolean dubboAsync = false;

    public boolean getDubboAsync() {
        return this.dubboAsync;
    }

    private void setDubboAsyncString(String async) {
        this.dubboAsync = "true".equalsIgnoreCase(async);
    }

    private void setDubboAsync(boolean async) {
        this.dubboAsync = async;
    }

    public HashMap<String, String> getOriginWhiteList() {
        return originWhiteList;
    }
}
