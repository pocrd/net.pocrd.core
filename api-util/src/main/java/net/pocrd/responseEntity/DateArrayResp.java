package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * Created by rendong on 2017/6/8.
 */
@Description("时间字符串数组返回值")
public final class DateArrayResp implements Serializable {
    private static final long serialVersionUID = 1L;
    @Description("一组POSIX time毫秒数")
    public long[] value;

    public static DateArrayResp convert(Collection<Date> ds) {
        DateArrayResp sa = new DateArrayResp();
        if (ds != null) {
            sa.value = new long[ds.size()];
            int i = 0;
            for (Date d : ds) {
                sa.value[i++] = d.getTime();
            }
        }
        return sa;
    }

    public static DateArrayResp convert(Date[] ds) {
        DateArrayResp sa = new DateArrayResp();
        if (ds != null) {
            sa.value = new long[ds.length];
            int i = 0;
            for (Date d : ds) {
                sa.value[i++] = d.getTime();
            }
        }
        return sa;
    }
}