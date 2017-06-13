package net.pocrd.util;

import java.util.Date;

/**
 * Created by rendong on 2017/6/12.
 */
public class DateUtil {
    public static Date parseDateFromPOSIXTime(long millisecondsSince1970) {
        return new Date(millisecondsSince1970);
    }

    public static Date parseDateFromPOSIXTimeString(String millisecondsStringSince1970) {
        return parseDateFromPOSIXTime(Long.valueOf(millisecondsStringSince1970));
    }
}
