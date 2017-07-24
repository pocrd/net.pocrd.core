package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rendong on 2017/6/8.
 */
public final class DateResp implements Serializable {
    private static final long serialVersionUID = 1L;
    @Description("POSIX time的毫秒数")
    public long value;

    public static DateResp convert(Date d) {
        DateResp dr = new DateResp();
        dr.value = d.getTime();
        return dr;
    }
}
