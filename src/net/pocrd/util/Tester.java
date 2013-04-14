package net.pocrd.util;

import net.pocrd.entity.ReturnCode;
import net.pocrd.entity.ReturnCodeException;

public class Tester implements HttpApiExecuter {
    public static class A {
        public Object test(int i, int i2) {
            return null;
        }
    }

    private Object instance;

    @Override
    public void setInstance(Object obj) {
        instance = obj;
    }

    @Override
    public Object execute(String[] parameters) {
        try {
            return ((A)instance).test(Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1]));
        } catch (Exception e) {
            throw new ReturnCodeException(ReturnCode.PARAMETER_ERROR, e);
        }
    }
}
