package net.pocrd.entity;

import net.pocrd.core.ApiManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by sunji on 2014/7/23.
 */
public class ReturnCodeContainer {
    private static HashMap<Integer, AbstractReturnCode> map = new HashMap<Integer, AbstractReturnCode>();

    static {
        Class returnCodeClass = ApiReturnCode.class;
        Class abstractReturnCodeClass = AbstractReturnCode.class;
        try {
            for (Field f : returnCodeClass.getDeclaredFields()) {
                if (ApiManager.isConstField(f) && AbstractReturnCode.class.isAssignableFrom(f.getType())) {
                    AbstractReturnCode code = (AbstractReturnCode)f.get(null);
                    code.setName(f.getName());
                    ReturnCodeContainer.putReturnCodeSuper2Map(code);
                }
            }
            for (Field f : abstractReturnCodeClass.getDeclaredFields()) {
                if (ApiManager.isConstField(f) && AbstractReturnCode.class.isAssignableFrom(f.getType())) {
                    AbstractReturnCode code = (AbstractReturnCode)f.get(null);
                    code.setName(f.getName());
                    ReturnCodeContainer.putReturnCodeSuper2Map(code);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("parse code failed. " + returnCodeClass.getName(), e);
        }
    }

    public static AbstractReturnCode findCode(int c) {
        AbstractReturnCode code = map.get(c);
        if (code == null) {
            throw new RuntimeException("cannot find errorCode:" + c + " in registed ReturnCode class.");
        }
        return code;
    }

    public static AbstractReturnCode[] getOpenCodes() {
        int size = map.size();
        ArrayList<AbstractReturnCode> cis = new ArrayList<AbstractReturnCode>(size);
        for (AbstractReturnCode c : map.values()) {
            cis.add(c);
        }
        Collections.sort(cis, new Comparator<AbstractReturnCode>() {

            @Override
            public int compare(AbstractReturnCode o1, AbstractReturnCode o2) {
                return o1.getCode() > o2.getCode() ? 1 : o1.getCode() < o2.getCode() ? -1 : 0;
            }
        });
        return cis.toArray(new AbstractReturnCode[cis.size()]);
    }

    public static void putReturnCodeSuper2Map(AbstractReturnCode abstractReturnCode) {
        AbstractReturnCode rc = map.get(abstractReturnCode.getCode());
        if (rc == null) {
            map.put(abstractReturnCode.getCode(), abstractReturnCode);
        }
    }
}
