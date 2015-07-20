package net.pocrd.core.test.model;

import net.pocrd.entity.ApiReturnCode;
import net.pocrd.entity.ServiceRuntimeException;
import net.pocrd.entity.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author haomin
 */
public class HelloAttachmentImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloAttachmentImpl.class);

    public String hello(String name) throws ServiceException {
        try {
            String retString = String.format("hello %s OUBAH!", name);
            business_work();
        } catch (ServiceRuntimeException sre) {
            logger.error("error", sre);
            throw new ServiceException("method hello failed.", sre);
        } catch (Throwable t) {
            logger.error("OMG!", t);
            if (t instanceof ServiceException) {
                throw (ServiceException)t;
            } else {
                throw new ServiceException(ApiReturnCode.INTERNAL_SERVER_ERROR, "method hello failed.");
            }
        }
        return null;
    }

    private void business_work() {
        try {
            // call other dubbo service
            otherDubboService();
        } catch (ServiceException se) {
            // 处理异常或将异常封装为BusinessException向上传递
            throw new ServiceRuntimeException("otherDubboService failed.", se);
        }

        throw new ServiceRuntimeException(ApiReturnCode.DYNAMIC_CODE_ERROR);
    }

    private void otherDubboService() throws ServiceException {

    }
}
