package net.pocrd.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import net.pocrd.entity.CallerInfo;
import net.pocrd.util.AESTokenHelper;
import net.pocrd.util.AesHelper;
import net.pocrd.util.Base64Util;

import org.junit.Test;

public class AESTokenHelperTest {

    @Test
    public void testTokenHelper() {
        String tokenPwd = Base64Util.encodeToString(AesHelper.randomKey(256));
        AESTokenHelper th = new AESTokenHelper(tokenPwd);
        CallerInfo ci = new CallerInfo();
        ci.expire = 987654321;
        ci.groups = new String[] { "TEST", "VIP" };
        ci.key = "1111111";
        ci.securityLevel = 9;
        ci.deviceId = 22222222222222L;
        ci.uid = 33333333333333L;
        String token = th.generateToken(ci);
        CallerInfo caller = th.parse(token);
        assertEquals(ci.expire, caller.expire);
        assertNull(caller.groups);
        assertNull(caller.key);
        assertTrue(caller.securityLevel > 0);
        assertTrue(caller.deviceId > 0);
        assertTrue(caller.uid > 0);
    }

    @Test
    public void testMultithread() {
        String tokenPwd = Base64Util.encodeToString(AesHelper.randomKey(256));
        final AESTokenHelper th = new AESTokenHelper(tokenPwd);
        final CallerInfo ci = new CallerInfo();
        ci.expire = 987654321;
        ci.groups = new String[] { "TEST", "VIP" };
        ci.key = "1111111";
        ci.securityLevel = 9;
        ci.deviceId = 22222222222222L;
        ci.uid = 33333333333333L;

        MultithreadTestHelper.runInMultithread(5, 10000, new Runnable() {

            @Override
            public void run() {
                String token = th.generateToken(ci);
                CallerInfo caller = th.parse(token);
                assertEquals(ci.expire, caller.expire);
                assertNull(caller.groups);
                assertNull(caller.key);
                assertTrue(caller.securityLevel > 0);
                assertTrue(caller.deviceId > 0);
                assertTrue(caller.uid > 0);
            }
        });
    }
}
