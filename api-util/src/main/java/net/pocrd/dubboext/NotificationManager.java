package net.pocrd.dubboext;

import com.alibaba.dubbo.remoting.exchange.ResponseCallback;
import com.alibaba.dubbo.rpc.RpcResult;

import java.util.Map;

/**
 * dubbo进行异步调用时需要通过设置该callback来获得返回的notification
 * Created by rendong on 2017/5/18.
 */
public class NotificationManager implements ResponseCallback {
    private ResponseCallback callback;
    final private static ThreadLocal<Map<String, String>> Notifications = new ThreadLocal<Map<String, String>>();

    public NotificationManager(ResponseCallback callback) {
        this.callback = callback;
    }

    public static Map<String, String> getNotifications() {
        return Notifications.get();
    }

    public static void clear() {
        Notifications.remove();
    }

    static void set(Map<String, String> ns) {
        Notifications.set(ns);
    }

    @Override
    public void done(Object response) {
        callback.done(response);
        if (RpcResult.class.isInstance(response)) {
            RpcResult rpcResult = (RpcResult)response;
            Map<String, String> ns = rpcResult.getNotifications();
            if (ns != null && ns.size() > 0) {
                Notifications.set(ns);
            }
        }
    }

    @Override
    public void caught(Throwable exception) {
        callback.caught(exception);
    }
}
