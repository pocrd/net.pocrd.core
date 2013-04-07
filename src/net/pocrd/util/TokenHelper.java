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
            caller.appid = dis.readInt();
            caller.expire = dis.readLong();
            caller.level = dis.readInt();
            caller.securityLevel = dis.readInt();
            caller.sn = dis.readLong();
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
            dos.writeInt(caller.appid);
            dos.writeLong(caller.expire);
            dos.writeInt(caller.level);
            dos.writeInt(caller.securityLevel);
            dos.writeLong(caller.sn);
            dos.writeLong(caller.uid);

            byte[] bs = baos.toByteArray();
            return Base64Util.encodeToString(aes.encrypt(bs));
        } catch (IOException e) {
            throw new RuntimeException("generate token failed.", e);
        }
    }
}
