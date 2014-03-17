package net.pocrd.util;

import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import net.pocrd.define.CompileConfig;
import net.pocrd.define.ConstField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 测试结论，对于HMacHelper这种，绝大部分时间都是同一个密钥在工作， 但是需要在多线程访问时进行同步的辅助类，使用ThreadLocal为每一个 线程缓存一个实例可以避免进行锁操作
 * 
 * @author rendong
 */
public class HMacHelper {
    private static final Logger            logger      = LogManager.getLogger("net.pocrd.util");
    private String                         pwd;
    private Mac                            mac;
    private static ThreadLocal<HMacHelper> localHelper = new ThreadLocal<HMacHelper>();

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
    private static final String            KEY_MAC     = "HmacMD5";

    private HMacHelper(String key) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(ConstField.UTF8), KEY_MAC);
            mac = Mac.getInstance(secretKey.getAlgorithm());
            pwd = key;
            mac.init(secretKey);
        } catch (Exception e) {
            logger.error("create hmac helper failed.", e);
        }
    }

    public static HMacHelper getThreadLocalInstance(String pwd) {
        HMacHelper h = localHelper.get();

        if (h == null) {
            h = new HMacHelper(pwd);
            localHelper.set(h);
        }
        if (CompileConfig.isDebug) {
            if(!pwd.equals(h.pwd)){
                throw new RuntimeException("使用了不同的pwd产生hmac实例。");
            }
        }
        return h;
    }

    public byte[] sign(byte[] content) {
        synchronized (this) {
            return mac.doFinal(content);
        }
    }

    public boolean verify(byte[] sig, byte[] content) {
        try {
            byte[] result = null;
            synchronized (this) {
                result = mac.doFinal(content);
            }
            return Arrays.equals(sig, result);
        } catch (Exception e) {
            logger.error("varify sig failed.", e);
        }
        return false;
    }

}
