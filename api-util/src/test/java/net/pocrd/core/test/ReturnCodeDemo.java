package net.pocrd.core.test;

import net.pocrd.entity.AbstractReturnCode;

/**
 * Created by sunji on 2014/7/24.
 * 帮助Demo类
 * 升级要点：
 * 1 继承关系：ReturnCode -》 AbstractReturnCode
 * 2 构造函数：
 * (String name, String desc, int code) -》 (int code, ReturnCodeSuperDemo display)
 * (String name, int code, ReturnCodeDemo display) -》 (String desc, int code)
 * 3 new ReturnCodeSuperDemo();
 */
public class ReturnCodeDemo extends AbstractReturnCode {
    private ReturnCodeDemo(String desc, int code) {
        super(desc, code);
    }

    private ReturnCodeDemo(int code, ReturnCodeDemo display) {
        super(code, display);
    }

    public static final int            C_DEMO_UNKNOW_ERROR = 1;
    public static final ReturnCodeDemo DEMO_UNKNOW_ERROR   = new ReturnCodeDemo("ReturnCodeSuperDemo错误描述", C_DEMO_UNKNOW_ERROR);
}
