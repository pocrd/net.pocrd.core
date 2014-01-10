package net.pocrd.util;

import java.util.Comparator;
import java.util.HashSet;

/**
 * @author guankaiqiang
 */
public class StringUtil {
    public static final Comparator<String> StringComparator = new Comparator<String>() {

                                                                @Override
                                                                public int compare(String s1, String s2) {
                                                                    int n1 = s1 == null ? 0 : s1.length();
                                                                    int n2 = s2 == null ? 0 : s2.length();
                                                                    int mn = n1 < n2 ? n1 : n2;
                                                                    for (int i = 0; i < mn; i++) {
                                                                        int k = s1.charAt(i) - s2.charAt(i);
                                                                        if (k != 0) {
                                                                            return k;
                                                                        }
                                                                    }
                                                                    return n1 - n2;
                                                                }
                                                            };

    /**
     * @param array
     * @return
     */
    public static String toString(int[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i : array) {
            sb.append("[" + i + "]");
        }
        return sb.toString();
    }

    /**
     * 避免Integer[]做unbox
     * 
     * @param array
     * @return
     */
    public static String toString(Integer[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Integer i : array) {
            sb.append("[" + i + "]");
        }
        return sb.toString();
    }

    /**
     * @param array
     * @return
     */
    public static String toString(boolean[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (boolean i : array) {
            sb.append("[" + i + "]");
        }
        return sb.toString();
    }

    /**
     * @param array
     * @return
     */
    public static String toString(Boolean[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Boolean i : array) {
            sb.append("[" + i + "]");
        }
        return sb.toString();
    }

    /**
     * @param array
     * @return
     */
    public static String toString(short[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (short i : array) {
            sb.append("[" + i + "]");
        }
        return sb.toString();
    }

    /**
     * @param array
     * @return
     */
    public static String toString(Short[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Short i : array) {
            sb.append("[" + i + "]");
        }
        return sb.toString();
    }

    /**
     * @param array
     * @return
     */
    public static String toString(byte[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte i : array) {
            sb.append("[" + i + "]");
        }
        return sb.toString();
    }

    /**
     * @param array
     * @return
     */
    public static String toString(Byte[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Byte i : array) {
            sb.append("[" + i + "]");
        }
        return sb.toString();
    }

    /**
     * @param array
     * @return
     */
    public static String toString(float[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (float i : array) {
            sb.append("[" + i + "]");
        }
        return sb.toString();
    }

    /**
     * @param array
     * @return
     */
    public static String toString(Float[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Float i : array) {
            sb.append("[" + i + "]");
        }
        return sb.toString();
    }

    /**
     * @param array
     * @return
     */
    public static String toString(double[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (double i : array) {
            sb.append("[" + i + "]");
        }
        return sb.toString();
    }

    /**
     * @param array
     * @return
     */
    public static String toString(Double[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Double i : array) {
            sb.append("[" + i + "]");
        }
        return sb.toString();
    }

    /**
     * @param array
     * @return
     */
    public static String toString(long[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (long i : array) {
            sb.append("[" + i + "]");
        }
        return sb.toString();
    }

    /**
     * @param array
     * @return
     */
    public static String toString(Long[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Long i : array) {
            sb.append("[" + i + "]");
        }
        return sb.toString();
    }

    /**
     * @param array
     * @return
     */
    public static String toString(String[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String i : array) {
            if (i != null)
                sb.append("[" + i + "]");
            else sb.append("[null]");
        }
        return sb.toString();
    }

    /**
     * array.toString()
     * 
     * @return
     */
    public static String toString(Object[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Object i : array) {
            if (i != null)
                sb.append("[" + i + "]");
            else sb.append("[null]");
        }
        return sb.toString();
    }

    private final static Class<?>    intArrayClass     = int[].class;
    private final static Class<?>    IntegerArrayClass = Integer[].class;
    private final static Class<?>    shortArrayClass   = short[].class;
    private final static Class<?>    ShortArrayClass   = Short[].class;
    private final static Class<?>    byteArrayClass    = byte[].class;
    private final static Class<?>    ByteArrayClass    = Byte[].class;
    private final static Class<?>    floatArrayClass   = float[].class;
    private final static Class<?>    FloatArrayClass   = Float[].class;
    private final static Class<?>    longArrayClass    = long[].class;
    private final static Class<?>    LongArrayClass    = Long[].class;
    private final static Class<?>    doubleArrayClass  = double[].class;
    private final static Class<?>    DoubleArrayClass  = Double[].class;
    private final static Class<?>    booleanArrayClass = boolean[].class;
    private final static Class<?>    BooleanArrayClass = Boolean[].class;
    private final static Class<?>    StringArrayClass  = String[].class;
    private final static Class<?>    ObjectArrayClass  = Object[].class;
    private static HashSet<Class<?>> classSet          = new HashSet<Class<?>>();
    static {
        classSet.add(intArrayClass);
        classSet.add(IntegerArrayClass);
        classSet.add(shortArrayClass);
        classSet.add(ShortArrayClass);
        classSet.add(byteArrayClass);
        classSet.add(ByteArrayClass);
        classSet.add(floatArrayClass);
        classSet.add(FloatArrayClass);
        classSet.add(longArrayClass);
        classSet.add(LongArrayClass);
        classSet.add(doubleArrayClass);
        classSet.add(DoubleArrayClass);
        classSet.add(booleanArrayClass);
        classSet.add(BooleanArrayClass);
        classSet.add(StringArrayClass);
        classSet.add(ObjectArrayClass);
    }

    /**
     * 是否需要类型转换
     * 
     * @param descriptor
     * @return
     */
    static Class<?> getCorrectType(Class<?> clazz) {
        return classSet.contains(clazz) ? clazz : Object[].class;
    }
}
