package net.pocrd.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.pocrd.entity.CallerInfo;

public class TokenHelper {
    private static final Logger logger = LogManager.getLogger("net.pocrd.util");
    private AesHelper           aes;

    public TokenHelper(String pwd) {
        aes = new AesHelper(Base64Util.decode(pwd), null);
    }

    public CallerInfo parse(String token) {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(aes.decrypt(Base64Util.decode(token))));

        try {
            CallerInfo caller = new CallerInfo();
            caller.expire = dis.readLong();
            caller.securityLevel = dis.readInt();
            caller.deviceId = dis.readLong();
            caller.uid = dis.readLong();
            
            return caller;
        } catch (Exception e) {
            logger.error("token parse failed.", e);
        }
        return null;
    }

    public String generateToken(CallerInfo caller) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(9);
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.writeLong(caller.expire);
            dos.writeInt(caller.securityLevel);
            dos.writeLong(caller.deviceId);
            dos.writeLong(caller.uid);

            byte[] bs = baos.toByteArray();
            return Base64Util.encodeToString(aes.encrypt(bs));
        } catch (IOException e) {
            throw new RuntimeException("generate token failed.", e);
        }
    }
}
