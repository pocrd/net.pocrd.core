package net.pocrd.util;

import java.util.HashSet;

import org.objectweb.asm.Type;

/**
 * 
 * @author guankaiqiang
 * 
 */
public class StringHelper {
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
	 * 
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
			else
				sb.append("[null]");
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
			else
				sb.append("[null]");
		}
		return sb.toString();
	}

	private final static String intArrayDescriptor=Type.getDescriptor(int[].class);
	private final static String IntegerArrayDescriptor=Type.getDescriptor(Integer[].class);
	private final static String shortArrayDescriptor=Type.getDescriptor(short[].class);
	private final static String ShortArrayDescriptor=Type.getDescriptor(Short[].class);
	private final static String byteArrayDescriptor=Type.getDescriptor(byte[].class);
	private final static String ByteArrayDescriptor=Type.getDescriptor(Byte[].class);
	private final static String floatArrayDescriptor=Type.getDescriptor(float[].class);
	private final static String FloatArrayDescriptor=Type.getDescriptor(Float[].class);
	private final static String longArrayDescriptor=Type.getDescriptor(long[].class);
	private final static String LongArrayDescriptor=Type.getDescriptor(Long[].class);
	private final static String doubleArrayDescriptor=Type.getDescriptor(double[].class);
	private final static String DoubleArrayDescriptor=Type.getDescriptor(Double[].class);
	private final static String booleanArrayDescriptor=Type.getDescriptor(boolean[].class);
	private final static String BooleanArrayDescriptor=Type.getDescriptor(Boolean[].class);
	private final static String StringArrayDescriptor=Type.getDescriptor(String[].class);
	private final static String ObjectArrayDescriptor=Type.getDescriptor(Object[].class);
	private static HashSet<String> descriptorSet=new HashSet<String>();
	static{
		descriptorSet.add(intArrayDescriptor);
		descriptorSet.add(IntegerArrayDescriptor);
		descriptorSet.add(shortArrayDescriptor);
		descriptorSet.add(ShortArrayDescriptor);
		descriptorSet.add(byteArrayDescriptor);
		descriptorSet.add(ByteArrayDescriptor);
		descriptorSet.add(floatArrayDescriptor);
		descriptorSet.add(FloatArrayDescriptor);
		descriptorSet.add(longArrayDescriptor);
		descriptorSet.add(LongArrayDescriptor);
		descriptorSet.add(doubleArrayDescriptor);
		descriptorSet.add(DoubleArrayDescriptor);
		descriptorSet.add(booleanArrayDescriptor);
		descriptorSet.add(BooleanArrayDescriptor);
		descriptorSet.add(StringArrayDescriptor);
		descriptorSet.add(ObjectArrayDescriptor);
	}
	/**
	 * 是否需要类型转换
	 * @param descriptor
	 * @return
	 */
	static boolean checkCast(String descriptor){
	    return descriptorSet.contains(descriptor);
	}
}
