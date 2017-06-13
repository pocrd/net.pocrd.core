package net.pocrd.core.test;

import net.pocrd.define.HttpApiExecutor;
import net.pocrd.entity.ApiReturnCode;
import net.pocrd.entity.ReturnCodeException;
import net.pocrd.responseEntity.ObjectArrayResp;

/**
 * Created by gkq on 2014/6/6.
 */
public class DeviceTestExecutor implements HttpApiExecutor {
    private Object instance;

    public void setInstance(Object obj) {
        this.instance = obj;
    }

    public Object execute(String[] paramArrayOfString) {
        if (paramArrayOfString[0] == null) {
            throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : s=" + paramArrayOfString[0]);
        }
        short s;
        try {
            s = Short.parseShort(paramArrayOfString[0]);
        } catch (Exception localException) {
            throw new ReturnCodeException(ApiReturnCode.PARAMETER_ERROR, "parameter validation failed : s=" + paramArrayOfString[0], localException);
        }
        return ObjectArrayResp.convert(((HttpApiUtilTest)this.instance).execute(1, "a"));
    }
}
