package net.pocrd.dubboext;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import net.pocrd.define.AttachmentKey;
import net.pocrd.define.CommonParameter;
import net.pocrd.entity.CompileConfig;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;

/**
 * 暂存访问者信息并在当前日志中打印调用编号
 *
 * @author guankaiqiang
 */
@Activate(group = Constants.PROVIDER)
public class TraceInfoProviderFilter implements Filter {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TraceInfoProviderFilter.class);

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
        String traceid = invocation.getAttachment(AttachmentKey.TRACE_ID);
        String sysinfo = invocation.getAttachment(AttachmentKey.SYS_INFO);
        String userinfo = invocation.getAttachment(AttachmentKey.USER_INFO);
        put(CommonParameter.callId, traceid);
        TraceInfo.setTraceInfo(new TraceInfo(traceid, sysinfo, userinfo));
        Result res = invoker.invoke(invocation);
        if (CompileConfig.isDebug) {
            if (res.getNotifications() != null && res.getNotifications().size() > 0) {
                StringBuilder sb = new StringBuilder("set sync notifications ----> ");
                for (Map.Entry<String, String> entry : res.getNotifications().entrySet()) {
                    sb.append(entry.getKey()).append(":").append(entry.getValue()).append("; ");
                }
                logger.info(sb.toString());
            } else {
                logger.info("set sync notificaion: empty");
            }
        }

        TraceInfo.clear();
        remove(CommonParameter.callId);
        return res;
    }
}
