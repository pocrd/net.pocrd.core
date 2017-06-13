package net.pocrd.core.test;

import net.pocrd.annotation.ApiGroup;
import net.pocrd.core.ApiManager;
import net.pocrd.core.test.DocumentUtilTest.RC;
import net.pocrd.entity.AbstractReturnCode;
import org.junit.Test;

@ApiGroup(name = "test", minCode = 0, maxCode = 100, codeDefine = RC.class, owner = "guankaiqiang")
public class DocumentUtilTest {

    public static class RC extends AbstractReturnCode {
        protected RC(String desc, int code) {
            super(desc, code);
        }
    }

    @Test
    public void test() {
        try {
            ApiManager manager = new ApiManager();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

}
