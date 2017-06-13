package net.pocrd.dubboext;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcResult;

/**
 * 不要在非dubbo provider中使用该Filter
 *
 * @author rendong
 */
@Activate(group = Constants.PROVIDER)
public class NotificationProviderFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        DubboExtProperty.clearNotificaitons();
        Result res = invoker.invoke(invocation);
        if (RpcResult.class.isInstance(res)) {
            RpcResult rpcResult = (RpcResult)res;
            rpcResult.setNotifications(DubboExtProperty.getCurrentNotifications());
            DubboExtProperty.clearNotificaitons();
            return rpcResult;
        }
        return res;
    }
}