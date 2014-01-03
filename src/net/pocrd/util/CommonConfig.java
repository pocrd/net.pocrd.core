package net.pocrd.util;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@XmlRootElement
public class CommonConfig {
    public static final CommonConfig Instance;

    public static final boolean      isDebug = true;

    private CommonConfig() {}

    static {
        CommonConfig tmp = ConfigUtil.load("Common.config", CommonConfig.class);
        // 默认值设置
        if (tmp == null) {
            tmp = new CommonConfig();
            tmp.autogenPath = "E:\\software\\jd\\";
            tmp.tokenPwd = "KaKS8hro1Ljf7YXIK+iiag5ofiPmaucUqqfBTu7eVVI=";
            tmp.accessLoggerName = "net.pocrd.api.access";
            tmp.staticSignPwd = "pocrd@gmail.com";
            tmp.cacheVersion = "v1.0";
            tmp.cacheType = CacheDBType.Redis;
        }
        Instance = tmp;
        Instance.accessLogger = LogManager.getLogger(Instance.accessLoggerName);
        Instance.tokenHelper = new TokenHelper(Instance.tokenPwd);
    }

    public String           accessLoggerName;
    public String           tokenPwd;
    public String           staticSignPwd;
    public String           autogenPath;
    public String           cacheVersion;
    public CacheDBType      cacheType;
    
    @XmlElementWrapper(name = "dbConfigs")
    @XmlElement(name = "entry")
    public JDBCPoolConfig[] dbConfigs;

    @XmlTransient
    public TokenHelper      tokenHelper;

    @XmlTransient
    public Logger           accessLogger;

    /**
     * 缓存实现机制
     * 
     * @author guankaiqiang
     */
    public enum CacheDBType {
        Redis, Memcache
    }
}
