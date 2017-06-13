package net.pocrd.dubboext;

import com.alibaba.dubbo.remoting.exchange.ResponseCallback;
import com.alibaba.dubbo.rpc.RpcResult;

/**
 * Created by rendong on 2017/5/18.
 */
public class NotificationCallback implements ResponseCallback {
    private ResponseCallback callback;

    public NotificationCallback(ResponseCallback callback) {
        this.callback = callback;
    }

    @Override
    public void done(Object response) {
        callback.done(response);
        if (RpcResult.class.isInstance(response)) {
            RpcResult rpcResult = (RpcResult)response;
            DubboExtProperty.addNotifications(rpcResult.getNotifications());
        }
    }

    @Override
    public void caught(Throwable exception) {
        callback.caught(exception);
    }
}
