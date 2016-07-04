package net.pocrd.core.test;

import net.pocrd.define.ConstField;
import net.pocrd.define.SecurityType;
import net.pocrd.entity.CallerInfo;
import net.pocrd.util.AESTokenHelper;
import net.pocrd.util.AesHelper;
import net.pocrd.util.Base64Util;
import net.pocrd.util.HexStringUtil;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;

import static org.junit.Assert.*;

public class AESTokenHelperTest {

    @Test
    public void testTokenHelper() {
        String tokenPwd = Base64Util.encodeToString(AesHelper.randomKey(256));
        AESTokenHelper th = new AESTokenHelper(tokenPwd);
        CallerInfo ci = new CallerInfo();
        ci.expire = 987654321;
        ci.groups = new String[] { "TEST", "VIP" };
        ci.key = "1111111".getBytes(ConstField.UTF8);
        ci.securityLevel = 9;
        ci.deviceId = 22222222222222L;
        ci.appid = 321;
        ci.oauthid = "1234567890987654321";
        String token = th.generateStringDeviceToken(ci);
        CallerInfo caller = th.parseToken(token);
        assertEquals(ci.expire, caller.expire);
        assertNull(caller.groups);
        assertTrue(Arrays.equals(ci.key, caller.key));
        assertEquals(ci.securityLevel, caller.securityLevel);
        assertEquals(ci.deviceId, caller.deviceId);
        assertEquals(ci.appid, caller.appid);
        assertEquals(ci.oauthid, caller.oauthid);
    }

    @Test
    public void testMultithread() {
        String tokenPwd = Base64Util.encodeToString(AesHelper.randomKey(256));
        final AESTokenHelper th = new AESTokenHelper(tokenPwd);
        final CallerInfo ci = new CallerInfo();
        ci.expire = 987654321;
        ci.groups = new String[] { "TEST", "VIP" };
        ci.key = "1111111".getBytes(ConstField.UTF8);
        ci.securityLevel = 9;
        ci.deviceId = 22222222222222L;
        //        ci.uid = 33333333333333L;
        ci.appid = 102;

        MultithreadTestHelper.runInMultithread(5, 10000, new Runnable() {

            @Override
            public void run() {
                String token = th.generateStringDeviceToken(ci);
                CallerInfo caller = th.parseToken(token);
                assertEquals(ci.expire, caller.expire);
                assertNull(caller.groups);
                assertTrue(Arrays.equals(ci.key, caller.key));
                assertTrue(caller.securityLevel > 0);
                assertTrue(caller.deviceId > 0);
                //                assertTrue(caller.uid > 0);
                assertTrue(caller.appid > 0);
            }
        });
    }

    @Test
    public void generateTokenTest() {
        CallerInfo callerInfo = new CallerInfo();
        callerInfo.uid = 123456789L;
        callerInfo.appid = 1;
        callerInfo.deviceId = 123456789L;
        callerInfo.expire = System.currentTimeMillis() + 10000000000L;
        callerInfo.key = "demo key".getBytes(ConstField.UTF8);
        callerInfo.oauthid = "1234567890987654321";
        callerInfo.securityLevel = SecurityType.RegisteredDevice.authorize(0);
        AESTokenHelper aesTokenHelper = new AESTokenHelper("eqHSs48SCL2VoGsW1lWvDWKQ8Vu71UZJyS7Dbf/e4zo=");
        String tk = aesTokenHelper.generateStringUserToken(callerInfo);
        System.out.println("tk:" + tk);
        CallerInfo callerInfo1 = aesTokenHelper.parseToken(tk);
        assertEquals(callerInfo.uid, callerInfo1.uid);
        assertEquals(callerInfo.appid, callerInfo1.appid);
        assertEquals(callerInfo.deviceId, callerInfo1.deviceId);
        assertEquals(callerInfo.expire, callerInfo1.expire);
        assertArrayEquals(callerInfo.key, callerInfo1.key);
        assertEquals(callerInfo.securityLevel, callerInfo1.securityLevel);
        assertEquals(callerInfo.oauthid, callerInfo1.oauthid);
    }

    @Test
    public void generateTokenTest_2() throws UnsupportedEncodingException {
        AESTokenHelper aesTokenHelper = new AESTokenHelper("eqHSs48SCL2VoGsW1lWvDWKQ8Vu71UZJyS7Dbf/e4zo=");
        CallerInfo callerInfo1 = aesTokenHelper.parseToken(URLDecoder
                .decode("836RJ9i%2BuFv1eXkdJX6VYziHRYTQHJoM0qX7FcCLiRUS9QHFeEmEpFdKdsCZzPhg09KTfIPxr47k65%2FLyqKm%2F5k4V8JgBXmBvFfFBpOh3I8%3D",
                        "utf-8"));
        System.out.println(HexStringUtil.toHexString(callerInfo1.key));
    }
}
