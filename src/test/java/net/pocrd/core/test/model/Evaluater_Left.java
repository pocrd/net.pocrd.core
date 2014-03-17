package net.pocrd.core.test.model;

import net.pocrd.entity.CallerInfo;

public class Evaluater_Left {
    public String      name;
    public int         age;
    public long        expire;
    public CallerInfo  caller1;
    private CallerInfo caller2;

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
