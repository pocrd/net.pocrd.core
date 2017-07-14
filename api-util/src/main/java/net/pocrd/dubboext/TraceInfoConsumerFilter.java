package net.pocrd.dubboext;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import net.pocrd.define.AttachmentKey;

/**
 * Created by rendong on 2017/7/11.
 */
@Activate(group = Constants.CONSUMER)
public class TraceInfoConsumerFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        TraceInfo traceInfo = TraceInfo.getTraceInfo();
        RpcContext context = RpcContext.getContext();
        context.setAttachment(AttachmentKey.TRACE_ID, traceInfo.traceid);
        context.setAttachment(AttachmentKey.SYS_INFO, traceInfo.sysinfo);
        context.setAttachment(AttachmentKey.USER_INFO, traceInfo.userinfo);

        return invoker.invoke(invocation);
    }
}
