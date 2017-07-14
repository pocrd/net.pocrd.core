package net.pocrd.entity;

import net.pocrd.core.HttpRequestExecutor;
import net.pocrd.util.Md5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Properties;

public class CommonConfig {
    private static final Logger logger = LoggerFactory.getLogger(CommonConfig.class);
    private static Properties prop;

    private CommonConfig() {
    }

    public static final void init(Properties properties) {
        prop = properties;
    }

    private static class Singleton {
        static CommonConfig instance = null;

        static {
            instance = new CommonConfig();
            if (prop == null) {
                try {
                    logger.warn("missing call CommonConfig.init(), try to load net.pocrd.core.config");
                    InputStream is = CommonConfig.class.getResourceAsStream("/net.pocrd.core.config");
                    if (is != null) {
                        prop = new Properties();
                        prop.load(is);
                    }
                } catch (Exception e) {
                    logger.warn("load /net.pocrd.core.config failed.", e);
                }
            }
            if (prop == null) {
                logger.warn("load config failed. use default settings.");
                prop = new Properties();
            }

            instance.autogenPath = prop.getProperty("net.pocrd.autogenPath", "/tmp/autogen");
            instance.apiInfoXslSite = prop.getProperty("net.pocrd.apiInfoXslSite", "/");
            instance.setOriginWhiteList(prop.getProperty("net.pocrd.originWhiteList"));
            instance.dubboAsync = "true".equals(prop.getProperty("net.pocrd.dubboAsync", "true"));
            instance.internalPort = Integer.parseInt(prop.getProperty("net.pocrd.internalPort", "8088"));
            instance.sslPort = Integer.parseInt(prop.getProperty("net.pocrd.sslPort", "8443"));
            instance.staticSignPwd = prop.getProperty("net.pocrd.staticSignPwd", "pocrd@2016");
            instance.rsaDecryptSecret = prop.getProperty("net.pocrd.rsaDecryptSecret");
            instance.tokenAes = prop.getProperty("net.pocrd.tokenAes");
            instance.executorName = prop.getProperty("net.pocrd.httpRequestExecutor");

            //启动时获取当前机器ip
            try {
                InetAddress addr = InetAddress.getLocalHost();
                instance.serverAddress = Md5Util.computeToHex(addr.getHostAddress().getBytes("UTF-8")).substring(0, 6);
                logger.info("apigw server,address:{},hash:{}", addr.getHostAddress(), instance.serverAddress);
            } catch (Exception e) {
                logger.error("can not get server address,_cid may be not unique", e);
            }
        }
    }

    public static final CommonConfig getInstance() {
        return Singleton.instance;
    }

    private String autogenPath;

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
     * 下载apiInfo.xsl的地址，全路径地址
     */
    private String apiInfoXslSite;

    public String getApiInfoXslSite() {
        return this.apiInfoXslSite;
    }

    /**
     * 域名白名单
     */
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

    public HashMap<String, String> getOriginWhiteList() {
        return originWhiteList;
    }

    /**
     * dubbo 以异步方式进行调用，会影响到接口执行时间统计
     */
    private boolean dubboAsync = false;

    public boolean getDubboAsync() {
        return this.dubboAsync;
    }

    /**
     * 内网端口号, 允许调用 Internal 接口
     */
    private int internalPort = Integer.MIN_VALUE;

    public int getInternalPort() {
        return internalPort;
    }

    /**
     * ssl 入口端口号, 允许调用要求加密传输的接口
     */
    private int sslPort = Integer.MIN_VALUE;

    public int getSslPort() {
        return sslPort;
    }

    /**
     * 静态加密字符串, 用于对接口做简单签名验证
     */
    private String staticSignPwd = null;

    public String getStaticSignPwd() {
        return staticSignPwd;
    }

    /**
     * 与客户端进行密文通信的rsa私钥
     */
    private String rsaDecryptSecret = null;

    public String getRsaDecryptSecret() {
        return rsaDecryptSecret;
    }

    /**
     * 用于token解密的aes秘钥
     */
    private String tokenAes = null;

    public String getTokenAes() {
        return tokenAes;
    }

    private static String executorName = null;

    private static class executorFactoryLazyLoader {
        /**
         * 用于处理http请求的执行器, 必须是 HttpRequestExecutor 的子类
         */
        private static Constructor executorFactory = null;

        static {
            Class clazz = null;
            try {
                if (executorName != null) {
                    clazz = Class.forName(executorName);
                } else {
                    clazz = HttpRequestExecutor.class;
                }
                executorFactory = clazz.getDeclaredConstructor();
                executorFactory.setAccessible(true);
            } catch (Throwable e) {
                logger.error("load http request executor failed. name:" + executorName, e);
            }
        }

        public static Constructor getExecutorFactory() {
            return executorFactory;
        }
    }

    public Constructor getExecutorFactory() {
        return executorFactoryLazyLoader.getExecutorFactory();
    }
}
