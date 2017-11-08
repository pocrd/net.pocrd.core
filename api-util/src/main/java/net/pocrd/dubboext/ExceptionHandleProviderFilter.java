package net.pocrd.dubboext;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import net.pocrd.entity.ApiReturnCode;
import net.pocrd.entity.ServiceException;
import net.pocrd.entity.ServiceRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 为dubbo provider统一提供异常捕获处理。
 *
 * @author rendong
 */
@Activate(group = Constants.PROVIDER, order = 1)
public class ExceptionHandleProviderFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandleProviderFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Result res;
        try {
            res = invoker.invoke(invocation);
            if (res.hasException()) {
                RpcResult result = new RpcResult(wrapException(res.getException()));
                result.setNotifications(res.getNotifications());
                result.setValue(res.getValue());
                res = result;
            }
        } catch (Throwable t) {
            res = new RpcResult(wrapException(t));
        }

        return res;
    }

    private ServiceException wrapException(Throwable t) {
        ServiceException e;
        if (t instanceof ServiceRuntimeException) {
            logger.error("api service runtime exception.", t);
            ServiceRuntimeException sre = (ServiceRuntimeException)t;
            e = new ServiceException("error code catched.", sre);
        } else if (t instanceof ServiceException) {
            logger.error("api service exception.", t);
            e = (ServiceException)t;
        } else {
            logger.error("api undesigned exception.", t);
            e = new ServiceException(ApiReturnCode.DUBBO_SERVICE_ERROR, "api failed. msg:" + t.getMessage(), t);
        }
        return e;
    }
}