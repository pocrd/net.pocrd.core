package net.pocrd.core.test.model;

import net.pocrd.entity.CallerInfo;

public class Evaluator_Left {
    public  String     name;
    public  int        age;
    public  long       expire;
    public  CallerInfo caller1;
    private CallerInfo caller2;
    private boolean    success;
    private boolean    success1;
    public  boolean    success2;
    public  boolean    success3;
    private boolean    isSuccess4;
    private boolean    isSuccess5;
    public  boolean    isSuccess6;
    public  boolean    isSuccess7;
    public boolean isSuccess4() {
        return isSuccess4;
    }
    public void setSuccess4(boolean isSuccess4) {
        this.isSuccess4 = isSuccess4;
    }
    public boolean isSuccess5() {
        return isSuccess5;
    }
    public void setSuccess5(boolean isSuccess5) {
        this.isSuccess5 = isSuccess5;
    }
    public boolean isSuccess1() {
        return success1;
    }
    public void setSuccess1(boolean success1) {
        this.success1 = success1;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public final void setCaller2(CallerInfo info) {
        this.caller2 = info;
    }

    public final CallerInfo getCaller2() {
        return caller2;
    }

    private CallerInfo caller3;

    public final void setCaller3(CallerInfo info) {
        this.caller3 = info;
    }

    public final CallerInfo getCaller3() {
        return caller3;
    }
}
