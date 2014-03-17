package net.pocrd.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import net.pocrd.util.ConfigUtil;
import net.pocrd.util.JDBCPoolConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@XmlRootElement
public class CommonConfig {
    private static CommonConfig instance;

    private CommonConfig() {}

    public static final CommonConfig getInstance() {
        if (instance == null) {
            synchronized (CommonConfig.class) {
                if (instance == null) {
                    CommonConfig tmp = ConfigUtil.load("Common.config", CommonConfig.class);
                    if (tmp == null) {
                        throw new RuntimeException("load common config failed.");
                    }
                    tmp.accessLogger = LogManager.getLogger(tmp.accessLoggerName);
                    instance = tmp;
                }
            }
        }
        return instance;
    }

    public String           accessLoggerName;
    public String           autogenPath;
    public String           cacheVersion;
    public CacheDBType      cacheType;
    public boolean          useHttpGzip = true;

    @XmlElementWrapper(name = "dbConfigs")
    @XmlElement(name = "entry")
    public JDBCPoolConfig[] dbConfigs;

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
