package net.pocrd.core.test.model;

import net.pocrd.entity.ServiceException;

/**
 * @author haomin
 */
public interface HelloService {

    String hello(String name) throws ServiceException;
}
