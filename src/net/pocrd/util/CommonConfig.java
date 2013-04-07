package net.pocrd.util;

import javax.xml.bind.annotation.XmlTransient;

public class CommonConfig{
    public static final CommonConfig Instance;

    public static final boolean isDebug = true;
    
    private CommonConfig() {
    }

    static {
        Instance = FileConfig.load("Common.config", CommonConfig.class);
        
        Instance.tokenHelper = new TokenHelper(Instance.tokenPwd);
    }
    
    public String tokenPwd;
    
    @XmlTransient
    public TokenHelper tokenHelper;
    
}
