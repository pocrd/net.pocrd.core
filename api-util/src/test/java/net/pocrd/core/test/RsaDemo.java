package net.pocrd.core.test;

import net.pocrd.util.Base64Util;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * Created by gkq on 16/3/29.
 */
public class RsaDemo {
    private static final Logger logger = LoggerFactory.getLogger(RsaDemo.class);

    //regist Security Provider,use BouncyCastleProvider
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * sign
     *
     * @param privateKeyStr privatekey base64
     * @param content
     * @return
     */
    public static String sign(String privateKeyStr, byte[] content) {
        if (privateKeyStr == null || privateKeyStr.length() == 0) {
            throw new RuntimeException("private key is empty.");
        }
        if (content == null) {
            return null;
        }
        RSAPrivateCrtKey privateKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = (RSAPrivateCrtKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64Util.decode(privateKeyStr)));
        } catch (Exception e) {
            throw new RuntimeException("init private key failed!", e);
        }
        String sig = null;
        try {
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(privateKey);
            signature.update(content);
            sig = Base64Util.encodeToString(signature.sign());//do base64 encoding
        } catch (Exception e) {
            throw new RuntimeException("sign failed!", e);
        }
        return sig;
    }

    /**
     * verify
     *
     * @param publicKeyStr publickey base64
     * @param sign
     * @param content
     * @return
     */
    public static boolean verify(String publicKeyStr, byte[] sign, byte[] content) {
        if (publicKeyStr == null || publicKeyStr.length() == 0) {
            throw new RuntimeException("publickey is empty.");
        }
        if (sign == null || content == null) {
            return false;
        }
        RSAPublicKey publicKey = null;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(Base64Util.decode(publicKeyStr)));
        } catch (Exception e) {
            throw new RuntimeException("init public key failed!", e);
        }
        boolean isOk = false;
        try {
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(publicKey);
            signature.update(content);
            isOk = signature.verify(sign);
        } catch (Exception e) {
            logger.error("rsa verify failed.", e);
        }
        return isOk;
    }

    //在当前目录生成公私密钥
    //openssl genrsa -out privatekey.pem 1024
    //openssl rsa -in privatekey.pem -pubout -out publickey.pem
    private static final String DEMO_PRIKEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI0mZ2v4Aw6VheWeeVBoV3s1zShbip/O79TH/wsnJTzGtNes7UH3OqoinnL8bJYhDrGXINtEN2PhZ12BTJas1abogNYgd+TlnUN7F4oUbejnafbSQEflImctq/GSUQVw55ZYeWcDrqY3jbzu8hQ9nL/n7H1gr7CDVuG5/cJedpYTAgMBAAECgYBBfJ3jI/AXJhw3Sm0ydxaPXYjPvpj0SAhRinCz9fd62R4yKlLxlgirwuAMrTyb8r5kep+N23pvZraUVVb5WTrOQt8FeX78Ka407kUr+/Jb4aK/YGACHVUMMSX1ZKxH42U3xP3mx9UAzjMtNZaMV1JuZwu3kWK+w8POYkMjrYwFQQJBAMdMO+6ufVBDEq4ZcYx+mAkVUkUGxS7Iog+Cw6D5oG27FPoq+soPtyhCa6PwqgjRz8xeXJm9FYOud4vejXcQGHECQQC1TwCrw1QlVm4w4R04N5biWY0aRX7lPKSVaW6Dn2R57a/XdtInCFsnk20ariXO5Q6kCFJomOu8gAAcsqF1L3jDAkEAwS91fOTpFe8eYWLKfZyM9WkUiVKJutLCvPRNe3Hd/9/z8pfM6CcZrM3Nl2mG+OugWQMzEdeGzlFFEnDVgg85EQJAfoHBVhfUfrjv6espGDCxdv5FFi+newv1Sstl8TEmSm0uZIjj2ZhbAiI73oHkn5fzv0CPP6xzz/MAIQYC4mFdQQJAIzZ73p+gN3KHDCMQ8OpsHXPB6uiOLdWGrKYPYfLImUpLhLt16R84ZcDTgC6fDDHzvpRsUP8upksTtMdajx877Q==";
    private static final String DEMO_PUBKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCNJmdr+AMOlYXlnnlQaFd7Nc0oW4qfzu/Ux/8LJyU8xrTXrO1B9zqqIp5y/GyWIQ6xlyDbRDdj4WddgUyWrNWm6IDWIHfk5Z1DexeKFG3o52n20kBH5SJnLavxklEFcOeWWHlnA66mN4287vIUPZy/5+x9YK+wg1bhuf3CXnaWEwIDAQAB";

    @Test
    public void testRsaSignAndVerify() throws UnsupportedEncodingException {
        //1.模拟请求构造
        Map<String, String> params = new HashMap<String, String>();
        params.put("paramName2", "value2");
        params.put("paramName1", "value1");
        //2.参数名排序
        List<String> paramNames = new ArrayList<String>(params.keySet());
        Collections.sort(paramNames);
        //3.构造签名前数据
        StringBuilder sb = new StringBuilder();
        for (String key : paramNames) {
            sb.append(key);
            sb.append('=');
            sb.append(params.get(key));
        }
        System.out.println(sb.toString());
        //4.进行签名
        byte[] content = sb.toString().getBytes("utf-8");
        String sig = sign(DEMO_PRIKEY, content);
        System.out.println("_sig=" + sig);
        //5.模拟服务端签名验证
        assertTrue(verify(DEMO_PUBKEY, Base64Util.decode(sig), content));
    }
}
