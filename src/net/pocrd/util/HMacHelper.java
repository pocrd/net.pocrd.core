package net.pocrd.util;

import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.pocrd.define.ConstField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HMacHelper {
    private static final Logger logger  = LogManager.getLogger("net.pocrd.util");
    private Mac mac;
    
    /**
     * MAC算法可选以下多种算法
     * 
     * <pre>
     * HmacMD5  
     * HmacSHA1  
     * HmacSHA256  
     * HmacSHA384  
     * HmacSHA512
     * </pre>
     */
    private static final String  KEY_MAC = "HmacMD5";

    public HMacHelper(String key) {
        SecretKey secretKey = new SecretKeySpec(key.getBytes(ConstField.UTF8), KEY_MAC);
        try {
            mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
        } catch (Exception e) {
            logger.error(e);
        }
    }
    
    public boolean verify(String sig, String content){
        try {
            return Arrays.equals(Base64Util.decode(sig), mac.doFinal(content.getBytes(ConstField.UTF8)));
        } catch (Exception e) {
            logger.error(e);
        }
        return false;
    }

}
