package net.pocrd.dubboext;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import net.pocrd.define.AttachmentKey;
import net.pocrd.entity.CompileConfig;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by rendong on 2017/7/11.
 */
@Activate(group = Constants.CONSUMER)
public class TraceInfoConsumerFilter implements Filter {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TraceInfoProviderFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        TraceInfo traceInfo = TraceInfo.getTraceInfo();
        if (traceInfo != null) {
            RpcContext context = RpcContext.getContext();
            context.setAttachment(AttachmentKey.TRACE_ID, traceInfo.traceid);
            context.setAttachment(AttachmentKey.SYS_INFO, traceInfo.sysinfo);
            context.setAttachment(AttachmentKey.USER_INFO, traceInfo.userinfo);
        }
        Result res = invoker.invoke(invocation);
        if (CompileConfig.isDebug) {
            if (res.getNotifications() != null && res.getNotifications().size() > 0) {
                StringBuilder sb = new StringBuilder("got sync notifications ----> ");
                for (Map.Entry<String, String> entry : res.getNotifications().entrySet()) {
                    sb.append(entry.getKey()).append(":").append(entry.getValue()).append("; ");
                }
                logger.info(sb.toString());
            } else {
                logger.info("got sync notificaion: empty!");
            }
        }
        return res;
    }
}
