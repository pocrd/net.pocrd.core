package net.pocrd.dubboext;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;

/**
 * @author guankaiqiang
 */
@Activate(group = Constants.CONSUMER)
public class NotificationConsumerFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext.getContext().clearNotifications();
        Result result = invoker.invoke(invocation);
        DubboExtProperty.addNotifications(RpcContext.getContext().getNotifications());
        RpcContext.getContext().clearNotifications();
        return result;
    }
}