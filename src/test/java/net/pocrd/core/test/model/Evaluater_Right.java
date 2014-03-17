package net.pocrd.core.test.model;

import net.pocrd.entity.CallerInfo;

public class Evaluater_Right {
    public String     name;
    public int        age;
    public long       expire;
    private CallerInfo caller1;
    public final void setCaller1(CallerInfo info){
        caller1 = info;
    }
    public final CallerInfo getCaller1(){
        return caller1;
    }
    public CallerInfo caller2;
    private CallerInfo caller3;
    public final CallerInfo getCaller3(){
        return caller3;
    }
    public final void setCaller3(CallerInfo info){
        caller3 = info;
    }
}
