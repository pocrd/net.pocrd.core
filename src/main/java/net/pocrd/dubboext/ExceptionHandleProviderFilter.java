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
 * 为dubbo provider统一提供异常捕获处理。由于该Filter会侵入异常处理流程，因此不强制使用。需服务开发者自行在META-INF中配置
 *
 * @author rendong
 */
@Activate(group = Constants.PROVIDER)
public class ExceptionHandleProviderFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandleProviderFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Result res = null;
        try {
            res = invoker.invoke(invocation);
        } catch (ServiceRuntimeException sre) {
            if (sre.getCause() != null) {
                logger.error("api failed.", sre);
            }
            res = new RpcResult(new ServiceException("api failed.", sre));
        } catch (Throwable t) {
            if (t instanceof ServiceException) {
                if (t.getCause() != null) {
                    logger.error("api failed.", t);
                }
                res = new RpcResult(t);
            } else {
                logger.error("api failed.", t);
                res = new RpcResult(new ServiceException(ApiReturnCode.DUBBO_SERVICE_ERROR, "api failed."));
            }
        }

        return res;
    }
}