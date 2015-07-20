package net.pocrd.dubboext;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import net.pocrd.define.CommonParameter;
import org.slf4j.MDC;

/**
 * 拓传调用编号
 *
 * @author guankaiqiang
 */
@Activate(group = Constants.PROVIDER)
public class ApiInvokeLoggerFilter implements Filter {
    private static boolean hasSlf4jMDC;
    private static boolean hasLog4jMDC;
    static {
        try {
            Class.forName("org.slf4j.MDC");
            hasSlf4jMDC = true;
        } catch (ClassNotFoundException cnfe) {
            hasSlf4jMDC = false;
        }
        try {
            if (!hasSlf4jMDC) {
                Class.forName("org.apache.log4j.MDC");
                hasLog4jMDC = true;
            }
        } catch (ClassNotFoundException cnfe) {
            hasLog4jMDC = false;
        }
    }
    private void put(String key, String value) {
        if (key != null && value != null) {
            if (hasSlf4jMDC) {
                MDC.put(key, value);
            } else if (hasLog4jMDC) {
                org.apache.log4j.MDC.put(key, value);
            }
        }
    }
    private void remove(String key) {
        if (key != null) {
            if (hasSlf4jMDC) {
                MDC.remove(key);
            } else if (hasLog4jMDC) {
                org.apache.log4j.MDC.remove(key);
            }
        }
    }
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext context = RpcContext.getContext();
        String cid = context.getAttachment(CommonParameter.callId);
        put(CommonParameter.callId, cid);
        Result res = invoker.invoke(invocation);
        remove(CommonParameter.callId);
        return res;
    }
}
