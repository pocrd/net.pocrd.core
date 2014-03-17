package net.pocrd.core.test;

import static org.junit.Assert.assertEquals;
import net.pocrd.annotation.ApiGroup;
import net.pocrd.annotation.ApiParameter;
import net.pocrd.annotation.HttpApi;
import net.pocrd.core.ApiManager;
import net.pocrd.define.SecurityType;

import org.junit.Test;

public class HttpApiUtilTest {

    @ApiGroup("test")
    public static class Test1 {
        @HttpApi(name = "test.test1", desc = "测试1", security = SecurityType.None)
        public String execute(@ApiParameter(required = true, name = "i1", desc = "参数1") int i1,
                @ApiParameter(required = true, name = "i2", verifyRegex = "^\\d+$", desc = "参数2") int i2) {
            return String.valueOf(i1 + i2);
        }
    }

    @ApiGroup("test")
    public static class Test2 {
        @HttpApi(name = "test.test2", desc = "测试2", security = SecurityType.None)
        public String execute(@ApiParameter(required = true, name = "b1", desc = "参数1") boolean b1,
                @ApiParameter(required = false, name = "b2", desc = "参数2", defaultValue = "true") boolean b2,
                @ApiParameter(required = true, name = "b3", verifyRegex = "^(true)|(false)$", desc = "参数3") boolean b3,
                @ApiParameter(required = false, name = "b4", desc = "参数4", defaultValue = "false") boolean b4,

                @ApiParameter(required = true, name = "by1", desc = "参数5") byte by1,
                @ApiParameter(required = false, name = "by2", desc = "参数6", defaultValue = "127") byte by2,
                @ApiParameter(required = true, name = "by3", verifyRegex = "^-?\\d+$", desc = "参数7") byte by3,
                @ApiParameter(required = false, name = "by4", desc = "参数8", defaultValue = "-128") byte by4,

                @ApiParameter(required = true, name = "c1", desc = "参数9") char c1,
                @ApiParameter(required = false, name = "c2", desc = "参数10", defaultValue = "" + (int)'c') char c2,
                @ApiParameter(required = true, name = "c3", verifyRegex = "^\\+?\\d+$", desc = "参数11") char c3,
                @ApiParameter(required = false, name = "c4", desc = "参数12", defaultValue = "" + (int)'z') char c4,

                @ApiParameter(required = true, name = "s1", desc = "参数13") short s1,
                @ApiParameter(required = false, name = "s2", desc = "参数14", defaultValue = "" + Short.MAX_VALUE) short s2,
                @ApiParameter(required = true, name = "s3", verifyRegex = "^-?\\d+$", desc = "参数15") short s3,
                @ApiParameter(required = false, name = "s4", desc = "参数16", defaultValue = "" + Short.MIN_VALUE) short s4,

                @ApiParameter(required = true, name = "i1", desc = "参数17") int i1,
                @ApiParameter(required = false, name = "i2", desc = "参数18", defaultValue = "0" + Integer.MAX_VALUE) int i2,
                @ApiParameter(required = true, name = "i3", verifyRegex = "^-?\\d+$", desc = "参数19") int i3,
                @ApiParameter(required = false, name = "i4", desc = "参数20", defaultValue = "" + Integer.MIN_VALUE) int i4,

                @ApiParameter(required = true, name = "l1", desc = "参数21") long l1,
                @ApiParameter(required = false, name = "l2", desc = "参数22", defaultValue = "" + Long.MAX_VALUE) long l2,
                @ApiParameter(required = true, name = "l3", verifyRegex = "^-?\\d+$", desc = "参数23") long l3,
                @ApiParameter(required = false, name = "l4", desc = "参数24", defaultValue = "" + Long.MIN_VALUE) long l4,

                @ApiParameter(required = true, name = "f1", desc = "参数25") float f1,
                @ApiParameter(required = false, name = "f2", desc = "参数26", defaultValue = "" + Float.MAX_VALUE) float f2,
                @ApiParameter(required = true, name = "f3", verifyRegex = "^-?\\d+\\.\\d+$", desc = "参数27") float f3,
                @ApiParameter(required = false, name = "f4", desc = "参数28", defaultValue = "" + Float.MIN_VALUE) float f4,

                @ApiParameter(required = true, name = "d1", desc = "参数29") double d1,
                @ApiParameter(required = false, name = "d2", desc = "参数30", defaultValue = "" + Double.MAX_VALUE) double d2,
                @ApiParameter(required = true, name = "d3", verifyRegex = "^-?\\d+\\.\\d+$", desc = "参数31") double d3,
                @ApiParameter(required = false, name = "d4", desc = "参数32", defaultValue = "" + Double.MIN_VALUE) double d4,

                @ApiParameter(required = true, name = "st1", desc = "参数33") String st1,
                @ApiParameter(required = false, name = "st2", desc = "参数34", defaultValue = "xxx") String st2) {
            return "result " + b1 + " " + b2 + " " + b3 + " " + b4 + " " + by1 + " " + by2 + " " + by3 + " " + by4 + " " + c1 + " " + c2 + " " + c3
                    + " " + c4 + " " + s1 + " " + s2 + " " + s3 + " " + s4 + " " + i1 + " " + i2 + " " + i3 + " " + i4 + " " + l1 + " " + l2 + " "
                    + l3 + " " + l4 + " " + f1 + " " + f2 + " " + f3 + " " + f4 + " " + d1 + " " + d2 + " " + d3 + " " + d4 + " " + st1 + " " + st2
                    + " ";
        }
    }

    @Test
    public void test() {
        try {
            ApiManager manager = new ApiManager("net.pocrd.core.test", "net.pocrd.core.test");
            {
                Object result1 = manager.processRequest("test.test1", new String[] { "123", "456" });
                System.out.println(result1);
                assertEquals(String.valueOf(123 + 456), result1);
            }
            {
                Object result2 = manager.processRequest(
                        "test.test2",
                        new String[] { "true", null, "true", null, "-128", null, "-128", null, String.valueOf((int)'c'), null,
                                String.valueOf((int)'c'), null, "32767", null, "32767", null, "12345678", null, "12345678", null, "12345678900",
                                null, "12345678900", null, "3.14159", null, "3.14159", null, "3.14159265", null, "3.14159265", null, "end", null });
                System.out.println(result2);
                assertEquals(
                        "result true true true false -128 127 -128 -128 c c c z 32767 32767 32767 -32768 12345678 2147483647 12345678 -2147483648 12345678900 9223372036854775807 12345678900 -9223372036854775808 3.14159 3.4028235E38 3.14159 1.4E-45 3.14159265 1.7976931348623157E308 3.14159265 4.9E-324 end xxx ",
                        result2);
            }
            {
                Object result3 = manager.processRequest("test.test2", new String[] { "true", "false", "true", "false", "-128", "127", "-128", "127",
                        String.valueOf((int)'c'), String.valueOf((int)'z'), String.valueOf((int)'c'), String.valueOf((int)'x'), "32767", "-32768",
                        "32767", "-32768", "12345678", "87654321", "12345678", "87654321", "12345678900", "999876543210", "12345678900",
                        "999876543210", "3.14159", "1.234567", "3.14159", "1.234567", "3.14159265", "1.23456789", "3.14159265", "1.23456789", "end",
                        "xxx" });
                System.out.println(result3);
                assertEquals(
                        "result true false true false -128 127 -128 127 c z c x 32767 -32768 32767 -32768 12345678 87654321 12345678 87654321 12345678900 999876543210 12345678900 999876543210 3.14159 1.234567 3.14159 1.234567 3.14159265 1.23456789 3.14159265 1.23456789 end xxx ",
                        result3);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
}
