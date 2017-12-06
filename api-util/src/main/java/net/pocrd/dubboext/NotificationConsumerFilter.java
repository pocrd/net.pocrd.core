package net.pocrd.dubboext;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;

import java.util.Map;

/**
 * dubbo进行同步调用时由该filter获取notification并存储到NotificationCallback的ThreadLocal成员中.
 * 如果当前服务需要把下游系统返回的notification继续返回给上游系统, 需要手工进行合并
 *
 * @author guankaiqiang
 */
@Activate(group = Constants.CONSUMER)
public class NotificationConsumerFilter implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        NotificationManager.clear();
        Result result = invoker.invoke(invocation);
        Map<String, String> ns = result.getNotifications();
        if (ns != null && ns.size() > 0) {
            NotificationManager.set(ns);
        }
        return result;
    }
}