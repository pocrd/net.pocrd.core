package net.pocrd.util;

public class CommonConfig extends FileConfig {
    public static final CommonConfig Instance = new CommonConfig();

    public static final boolean isDebug = true;
    
    private CommonConfig() {
        super("Common.config");
    }

    static {
        FileConfig.fillConfig(Instance);
    }
    
    public String tokenPwd;
    
}
