package net.pocrd.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import net.pocrd.demo.DemoDao;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class BytecodeUtil implements Opcodes {
	public static void loadConst(MethodVisitor mv, long l) {
		if (l == 0) {
			mv.visitInsn(LCONST_0);
		} else if (l == 1) {
			mv.visitInsn(LCONST_1);
		} else {
			mv.visitLdcInsn(new Long(l));
		}
	}

	public static void loadConst(MethodVisitor mv, float f) {
		if (f == 0) {
			mv.visitInsn(FCONST_0);
		} else if (f == 1) {
			mv.visitInsn(FCONST_1);
		} else if (f == 2) {
			mv.visitInsn(FCONST_2);
		} else {
			mv.visitLdcInsn(new Float(f));
		}
	}

	public static void loadConst(MethodVisitor mv, double d) {
		if (d == 0) {
			mv.visitInsn(DCONST_0);
		} else if (d == 1) {
			mv.visitInsn(DCONST_1);
		} else {
			mv.visitLdcInsn(new Double(d));
		}
	}

	public static void loadConst(MethodVisitor mv, int i) {
		switch (i) {
		case -1:
			mv.visitInsn(ICONST_M1);
			break;
		case 0:
			mv.visitInsn(ICONST_0);
			break;
		case 1:
			mv.visitInsn(ICONST_1);
			break;
		case 2:
			mv.visitInsn(ICONST_2);
			break;
		case 3:
			mv.visitInsn(ICONST_3);
			break;
		case 4:
			mv.visitInsn(ICONST_4);
			break;
		case 5:
			mv.visitInsn(ICONST_5);
			break;
		default:
			if (i <= Byte.MAX_VALUE && i >= Byte.MIN_VALUE) {
				mv.visitIntInsn(BIPUSH, i);
			} else if (i <= Short.MAX_VALUE && i >= Short.MIN_VALUE) {
				mv.visitIntInsn(SIPUSH, i);
			} else {
				mv.visitLdcInsn(new Integer(i));
			}
			break;
		}
	}

	public static void loadConst(MethodVisitor mv, String s) {
		if (s == null) {
			mv.visitInsn(ACONST_NULL);
		} else {
			mv.visitLdcInsn(s);
		}
	}

	/**
	 * 
	 * 常量 加载
	 */
	public static void loadConst(MethodVisitor mv, String s, Class<?> clazz) {
		if (s == null) {
			mv.visitInsn(ACONST_NULL);
		} else {
			if (clazz.isPrimitive()) {
				String pName = clazz.toString();
				if ("boolean".equals(pName)) {
					loadConst(mv, Boolean.parseBoolean(s) ? 1 : 0);
				} else if ("byte".equals(pName)) {
					loadConst(mv, Integer.parseInt(s));
				} else if ("char".equals(pName)) {
					loadConst(mv, Integer.parseInt(s));
				} else if ("short".equals(pName)) {
					loadConst(mv, Integer.parseInt(s));
				} else if ("int".equals(pName)) {
					loadConst(mv, Integer.parseInt(s));
				} else if ("long".equals(pName)) {
					loadConst(mv, Long.parseLong(s));
				} else if ("float".equals(pName)) {
					loadConst(mv, Float.parseFloat(s));
				} else if ("double".equals(pName)) {
					loadConst(mv, Double.parseDouble(s));
				} else {
					throw new RuntimeException("不支持的参数类型" + pName);
				}
			} else {
				if (clazz == Boolean.class) {
					loadConst(mv, Boolean.parseBoolean(s) ? 1 : 0);
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean",
							"valueOf", "(Z)Ljava/lang/Boolean;");
				} else if (clazz == Byte.class) {
					loadConst(mv, Integer.parseInt(s));
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte",
							"valueOf", "(B)Ljava/lang/Byte;");
				} else if (clazz == Character.class) {
					loadConst(mv, Integer.parseInt(s));
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character",
							"valueOf", "(C)Ljava/lang/Character;");
				} else if (clazz == Short.class) {
					loadConst(mv, Integer.parseInt(s));
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short",
							"valueOf", "(S)Ljava/lang/Short;");
				} else if (clazz == Integer.class) {
					loadConst(mv, Integer.parseInt(s));
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer",
							"valueOf", "(I)Ljava/lang/Integer;");
				} else if (clazz == Long.class) {
					loadConst(mv, Long.parseLong(s));
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long",
							"valueOf", "(J)Ljava/lang/Long;");
				} else if (clazz == Float.class) {
					loadConst(mv, Float.parseFloat(s));
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float",
							"valueOf", "(F)Ljava/lang/Float;");
				} else if (clazz == Double.class) {
					loadConst(mv, Double.parseDouble(s));
					mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double",
							"valueOf", "(D)Ljava/lang/Double;");
				} else if (clazz == String.class) {
					mv.visitLdcInsn(s);
				} else {
					throw new RuntimeException("不支持的参数类型" + clazz.getName());
				}
			}
		}
	}

	public static void createInnerClassVisitor(ClassWriter cw, Class<?> clazz) {
		Class<?> dc = clazz.getDeclaringClass();
		if (dc != null) {
			createInnerClassVisitor(cw, dc);
		} else {
			return;
		}
		int flag = 0;
		int mod = clazz.getModifiers();
		if (Modifier.isPublic(mod)) {
			flag |= ACC_PUBLIC;
		} else if (Modifier.isProtected(mod)) {
			flag |= ACC_PROTECTED;
		} else if (Modifier.isPrivate(mod)) {
			flag |= ACC_PRIVATE;
		}
		if (Modifier.isFinal(mod)) {
			flag |= ACC_FINAL;
		}
		if (Modifier.isStatic(mod)) {
			flag |= ACC_STATIC;
		}
		if (Modifier.isAbstract(mod)) {
			flag |= ACC_ABSTRACT;
		}
		if (Modifier.isInterface(mod)) {
			flag |= ACC_INTERFACE;
		}
		if (Modifier.isStrict(mod)) {
			flag |= ACC_STRICT;
		}
		cw.visitInnerClass(clazz.getName().replace('.', '/'), dc.getName()
				.replace('.', '/'), clazz.getSimpleName(), flag);
	}

	public static void doCast(MethodVisitor mv, Class<?> clazz) {
		if (clazz.isPrimitive()) {
			switch (clazz.getName()) {
			case "int":
				mv.visitTypeInsn(CHECKCAST, "java/lang/Integer");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer",
						"intValue", "()I");
				break;
			case "char":
				mv.visitTypeInsn(CHECKCAST, "java/lang/Character");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character",
						"charValue", "()C");
				break;
			case "short":
				mv.visitTypeInsn(CHECKCAST, "java/lang/Short");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short",
						"shortValue", "()S");
				break;
			case "byte":
				mv.visitTypeInsn(CHECKCAST, "java/lang/Byte");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte",
						"byteValue", "()B");
				break;
			case "float":
				mv.visitTypeInsn(CHECKCAST, "java/lang/Float");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float",
						"floatValue", "()F");
				break;
			case "boolean":
				mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean",
						"booleanValue", "()Z");
				break;
			case "double":
				mv.visitTypeInsn(CHECKCAST, "java/lang/Double");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double",
						"doubleValue", "()D");
				break;
			case "long":
				mv.visitTypeInsn(CHECKCAST, "java/lang/Long");
				mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long",
						"longValue", "()J");
				break;
			default:
				throw new RuntimeException("不支持的类型" + clazz.getName());
			}
		}else{
			mv.visitTypeInsn(CHECKCAST, clazz.getName().replace('.', '/'));
		}
	}

	public static void inbox(MethodVisitor mv, Class<?> clazz) {
		if (clazz.isPrimitive()) {
			switch (clazz.getName()) {
			case "int":
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer",
						"valueOf", "(I)Ljava/lang/Integer;");
				break;
			case "char":
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character",
						"valueOf", "(C)Ljava/lang/Character;");
				break;
			case "short":
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf",
						"(S)Ljava/lang/Short;");
				break;
			case "byte":
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf",
						"(B)Ljava/lang/Byte;");
				break;
			case "float":
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf",
						"(F)Ljava/lang/Float;");
				break;
			case "boolean":
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean",
						"valueOf", "(Z)Ljava/lang/Boolean;");
				break;
			case "double":
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf",
						"(D)Ljava/lang/Double;");
				break;
			case "long":
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf",
						"(J)Ljava/lang/Long;");
				break;
			default:
				throw new RuntimeException("不支持的类型" + clazz.getName());
			}
		}
	}

	/**
	 * 方法返回
	 * 
	 * @param mv
	 * @param clazz
	 */
	public static void doReturn(MethodVisitor mv, Class<?> clazz) {
		if (clazz.isPrimitive()) {
			switch (clazz.getName()) {
			case "int":
			case "char":
			case "short":
			case "byte":
			case "float":
			case "boolean":
				mv.visitInsn(IRETURN);
				break;
			case "double":
				mv.visitInsn(DRETURN);
				break;
			case "long":
				mv.visitInsn(LRETURN);
				break;
			case "void":
				mv.visitInsn(RETURN);
				break;
			default:
				throw new RuntimeException("不支持的类型" + clazz.getName());
			}
		} else {
			mv.visitInsn(ARETURN);
		}
	}

	/**
	 * 
	 * @param mv
	 * @param clazz
	 */
	public static void doInstanceof(MethodVisitor mv, Class<?> clazz) {
		String type = null;
		if (clazz.isPrimitive()) {
			switch (clazz.getName()) {
			case "int":
				type = "java/lang/Integer";
				break;
			case "char":
				type = "java/lang/Char";
				break;
			case "short":
				type = "java/lang/Short";
				break;
			case "byte":
				type = "java/lang/Byte";
				break;
			case "float":
				type = "java/lang/Float";
				break;
			case "boolean":
				type = "java/lang/Boolean";
				break;
			case "double":
				type = "java/lang/Double";
				break;
			case "long":
				type = "java/lang/Long";
				break;
			default:
				throw new RuntimeException("不支持的类型" + clazz.getName());
			}
		} else {
			type=clazz.getName().replace('.', '/');
		}
		mv.visitTypeInsn(INSTANCEOF, type);
	}

	public static void main(String[] args) {
		Method[] ms = DemoDao.class.getMethods();
		for (Method m : ms) {
			System.out.println(m.getName() + ":");
			for (Class<?> ptype : m.getParameterTypes()) {
				System.out.print(ptype.getName() + "  ");
			}
		}
	}
}
