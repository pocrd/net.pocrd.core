package net.pocrd.dubboext;

import com.alibaba.dubbo.remoting.exchange.ResponseCallback;
import com.alibaba.dubbo.rpc.RpcResult;
import net.pocrd.entity.CompileConfig;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * dubbo进行异步调用时需要通过设置该callback来获得返回的notification
 * Created by rendong on 2017/5/18.
 */
public class NotificationManager implements ResponseCallback {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(NotificationManager.class);
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
        saveNotifications(response);
    }

    public static void saveNotifications(Object response) {
        if (RpcResult.class.isInstance(response)) {
            RpcResult rpcResult = (RpcResult)response;
            Map<String, String> ns = rpcResult.getNotifications();
            if (ns != null && ns.size() > 0) {
                Notifications.set(ns);
            }
            if (CompileConfig.isDebug) {
                if (ns != null) {
                    StringBuilder sb = new StringBuilder("got async notifications ----> ");
                    for (Map.Entry<String, String> entry : ns.entrySet()) {
                        sb.append(entry.getKey()).append(":").append(entry.getValue()).append("; ");
                    }
                    logger.info(sb.toString());
                } else {
                    logger.info("got async notificaion: null");
                }
            }
        }
    }

    @Override
    public void caught(Throwable exception) {
        callback.caught(exception);
    }
}
