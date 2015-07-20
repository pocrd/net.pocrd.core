package net.pocrd.dubboext;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;

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