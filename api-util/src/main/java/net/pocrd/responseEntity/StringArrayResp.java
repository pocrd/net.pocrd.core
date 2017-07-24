package net.pocrd.responseEntity;

import net.pocrd.annotation.Description;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

/**
 * 这个是有必要的否则生成很多没有意义的StringArrayResp
 */
@Description("字符串数组返回值")
public final class StringArrayResp implements Serializable {
    private static final long serialVersionUID = 1L;
    @Description("字符串数组返回值")
    public Collection<String> value;

    public static StringArrayResp convert(Collection<String> ss) {
        StringArrayResp sa = new StringArrayResp();
        sa.value = ss;
        return sa;
    }

    public static StringArrayResp convert(String[] ss) {
        StringArrayResp sa = new StringArrayResp();
        if (ss != null) {
            sa.value = Arrays.asList(ss);
        }
        return sa;
    }
}
