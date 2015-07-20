package net.pocrd.util;

import net.pocrd.annotation.NotThreadSafe;
import net.pocrd.entity.CallerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 处理使用AES秘钥加密用户信息而产生的token
 *
 * @author rendong
 */
@NotThreadSafe
public class AESTokenHelper {
    private static final Logger logger                   = LoggerFactory.getLogger(AESTokenHelper.class);
    private static final short  DEVICE_TOKEN_VERSION_1_0 = 10;
    private AesHelper aes;
    public AESTokenHelper(String pwd) {
        aes = new AesHelper(Base64Util.decode(pwd), null);
    }

    public AESTokenHelper(AesHelper helper) {aes = helper;}

    /**
     * 从base64编码的字符串中解析调用者信息
     */
    public CallerInfo parseToken(String token) {
        try {
            return parseToken(Base64Util.decode(token));
        } catch (Exception e) {
            logger.error("token parse failed.", e);
        }
        return null;
    }

    /**
     * 解析调用者信息
     */
    public CallerInfo parseToken(byte[] token) {
        DataInputStream dis = null;
        CallerInfo caller = null;
        try {
            dis = new DataInputStream(new ByteArrayInputStream(aes.decrypt(token)));
            short tokenVersion = dis.readShort(); // token version for backward compliance
            if (tokenVersion != DEVICE_TOKEN_VERSION_1_0) {
                logger.error("token version mismatch!");
                return null;
            }
            caller = new CallerInfo();
            caller.expire = dis.readLong();
            caller.securityLevel = dis.readInt();
            caller.appid = dis.readInt();
            caller.deviceId = dis.readLong();
            caller.uid = dis.readLong();
            short len = dis.readShort();
            if (len > 0) {
                caller.key = new byte[len];
                if (len != dis.read(caller.key)) {
                    return null;
                }
            }
        } catch (Exception e) {
            logger.error("token parse failed.", e);
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    logger.error("token parse failed.close input stream failed!", e);
                }
            }
        }
        return caller;
    }

    /**
     * 生成用户token
     */
    public byte[] generateUserToken(CallerInfo caller) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8);
        DataOutputStream dos = new DataOutputStream(baos);
        byte[] token = null;
        try {
            dos.writeShort(DEVICE_TOKEN_VERSION_1_0);
            dos.writeLong(caller.expire);
            dos.writeInt(caller.securityLevel);
            dos.writeInt(caller.appid);
            dos.writeLong(caller.deviceId);
            dos.writeLong(caller.uid);
            short len = caller.key == null ? 0 : (short)caller.key.length;
            dos.writeShort(len);
            if (caller.key != null) {
                dos.write(caller.key);
            }
            byte[] bs = baos.toByteArray();
            token = aes.encrypt(bs);
        } catch (IOException e) {
            throw new RuntimeException("generator token failed.", e);
        } finally {
            try {
                dos.close();
            } catch (IOException e) {
                logger.error("generator token failed.close output stream failed!", e);
            }
        }
        return token;
    }
    /**
     * 生成设备token
     *
     * @param caller
     *
     * @return
     */
    public byte[] generateDeviceToken(CallerInfo caller) {
        caller.uid = 0;
        return generateUserToken(caller);
    }
    /**
     * 生成用户token
     *
     * @param caller
     *
     * @return
     */
    public String generateStringUserToken(CallerInfo caller) {
        return Base64Util.encodeToString(this.generateUserToken(caller));
    }
    /**
     * 生成设备token
     *
     * @param caller
     *
     * @return
     */
    public String generateStringDeviceToken(CallerInfo caller) {
        return Base64Util.encodeToString(this.generateDeviceToken(caller));
    }
}
