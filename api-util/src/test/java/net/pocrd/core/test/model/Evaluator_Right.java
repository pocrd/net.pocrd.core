package net.pocrd.core.test.model;

import net.pocrd.entity.CallerInfo;

public class Evaluator_Right {
    public  String     name;
    public  int        age;
    public  long       expire;
    private CallerInfo caller1;
    private boolean    success;
    public  boolean    success1;
    private boolean    success2;
    public  boolean    success3;
    private boolean    isSuccess4;
    public  boolean    isSuccess5;
    private boolean    isSuccess6;
    public  boolean    isSuccess7;
    public boolean isSuccess4() {
        return isSuccess4;
    }
    public void setSuccess4(boolean isSuccess4) {
        this.isSuccess4 = isSuccess4;
    }
    public boolean isSuccess6() {
        return isSuccess6;
    }
    public void setSuccess6(boolean isSuccess6) {
        this.isSuccess6 = isSuccess6;
    }
    public boolean isSuccess2() {
        return success2;
    }
    public void setSuccess2(boolean success2) {
        this.success2 = success2;
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public final void setCaller1(CallerInfo info) {
        caller1 = info;
    }
    public final CallerInfo getCaller1() {
        return caller1;
    }
    public  CallerInfo caller2;
    private CallerInfo caller3;
    public final CallerInfo getCaller3() {
        return caller3;
    }
    public final void setCaller3(CallerInfo info) {
        caller3 = info;
    }
}
