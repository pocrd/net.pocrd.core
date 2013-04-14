package net.pocrd.util;

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
        Instance = ConfigUtil.load("Common.config", CommonConfig.class);

        Instance.accessLogger = LogManager.getLogger(Instance.accessLoggerName);
        Instance.tokenHelper = new TokenHelper(Instance.tokenPwd);
    }

    public String      accessLoggerName;
    public String      tokenPwd;
    public String      staticSignPwd;

    @XmlTransient
    public TokenHelper tokenHelper;

    @XmlTransient
    public Logger      accessLogger;

}
