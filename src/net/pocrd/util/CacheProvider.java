package net.pocrd.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import net.pocrd.annotation.CacheMethod;
import net.pocrd.annotation.CacheParameter;
import net.pocrd.annotation.CacheParameter.CacheKeyType;
import net.pocrd.core.PocClassLoader;
import net.pocrd.util.CommonConfig.CacheDBType;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Generate subclass with cache，then create and cache an single instance for
 * input class.
 * 
 * @author guankaiqiang
 * @param <T>
 */
public class CacheProvider implements Opcodes {

	private static ConcurrentHashMap<Class<?>, Object> cache = new ConcurrentHashMap<Class<?>, Object>();
	private final static String CACHE_SPLITER = "|";

	@SuppressWarnings("unchecked")
	public static <T> T getSingleton(Class<T> clazz) {
		T instance = (T) cache.get(clazz);
		if (instance == null) {
			synchronized (cache) {
				instance = (T) cache.get(clazz);
				if (instance == null) {
					instance = createSingleton(clazz);
					cache.put(clazz, instance);
				}
			}
		}
		return instance;
	}

	private static <T> T createSingleton(Class<T> clazz) {
		try {
			if (hasCacheMethod(clazz)) {
				return newCachedClassInstance(clazz);
			} else {
				return SingletonUtil.getSingleton(clazz);
			}
		} catch (Exception e) {
			throw new RuntimeException("创建单例失败", e);
		}
	}

	/**
	 * 检测代理类型是否包含需要缓存的方法
	 * 
	 * @author guankaiqiang
	 * @param clazz
	 * @return
	 */
	public static <T> boolean hasCacheMethod(Class<T> clazz) {
		Method[] methods = clazz.getMethods();
		for (Method m : methods) {
			CacheMethod cacheAnnotation = m.getAnnotation(CacheMethod.class);
			if (cacheAnnotation != null
					&& !"void".equals(m.getReturnType().getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 织入缓存代码
	 * 
	 * @author guankaiqiang
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newCachedClassInstance(Class<T> clazz) {
		try {
			String className = "net/pocrd/autogen/Cache_"
					+ clazz.getSimpleName();
			String superClassName = clazz.getName().replace('.', '/');
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
			MethodVisitor mv;
			cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, className, null,
					superClassName, null);
			cw.visitSource("Cache_" + clazz.getSimpleName() + ".java", null);
			{
				// init
				mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
				mv.visitCode();
				Label l0 = new Label();
				mv.visitLabel(l0);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKESPECIAL, superClassName, "<init>",
						"()V");
				mv.visitInsn(RETURN);
				Label l1 = new Label();
				mv.visitLabel(l1);
				mv.visitLocalVariable("this", Type.getDescriptor(clazz), null,
						l0, l1, 0);
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
			Method[] methods = clazz.getMethods();
			for (Method m : methods) {
				CacheMethod cacheAnnotation = m
						.getAnnotation(CacheMethod.class);
				if (cacheAnnotation != null && cacheAnnotation.enable()) {
					// 对于返回void的函数不做处理
					if ("void".equals(m.getReturnType().getName())) {
						continue;
					}
					// 引入ReturnType参与key构造，从一定程度上避免复用cacheKey导致转型问题。
					// 允许不同函数复用同样的缓存，不过这个复用是不安全的，不推荐使用
					String keyName = CommonConfig.Instance.cacheVersion
							+ CACHE_SPLITER + cacheAnnotation.key()
							+ CACHE_SPLITER
							+ m.getReturnType().getCanonicalName()// 使用全限定名
							+ CACHE_SPLITER;
					int expire = cacheAnnotation.expire();
					Label ljump0 = new Label();
					Label ljump1 = new Label();
					Label ljump2 = new Label();
					LocalVarTable varTb = new LocalVarTable(m);// 临时变量表信息
					{
						mv = cw.visitMethod(ACC_PUBLIC, m.getName(),
								Type.getMethodDescriptor(m), null, null);
						mv.visitCode();
						// 1.构造cachekey
						{
							mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
							mv.visitInsn(DUP);
							mv.visitLdcInsn(keyName);
							mv.visitMethodInsn(INVOKESPECIAL,
									"java/lang/StringBuilder", "<init>",
									"(Ljava/lang/String;)V");
							Annotation[][] paramAnnotations = m
									.getParameterAnnotations();
							Class<?>[] paramTypes = m.getParameterTypes();
							if (paramAnnotations != null
									&& paramAnnotations.length != 0) {
								if (CommonConfig.isDebug) {
									if (paramTypes.length != paramAnnotations.length)
										throw new RuntimeException(
												"存在未被CacheParameter标记的函数入参"
														+ m.getName());
								}
								int indexOfParam = 0;
								for (Annotation[] annotations : paramAnnotations) {
									if (annotations != null
											&& annotations.length != 0) {
										for (Annotation annotation : annotations) {
											if (annotation.annotationType() == CacheParameter.class) {
												CacheParameter paramAnnotation = (CacheParameter) annotation;
												if (paramAnnotation.type() == CacheKeyType.Normal) {
													Class<?> paramType = paramTypes[indexOfParam];
													String paramDes = "";
													varTb.loadLocalVar(mv,
															indexOfParam + 1);
													if (paramType.isArray()) {
														paramDes = StringHelper.descriptorSet
																.contains(Type
																		.getDescriptor(paramType)) ? Type
																.getDescriptor(paramType)
																: Type.getDescriptor(Object[].class);// 隐式的类型转换
														mv.visitMethodInsn(
																INVOKESTATIC,
																"net/pocrd/util/StringHelper",
																"toString",
																"("
																		+ paramDes
																		+ ")"
																		+ Type.getDescriptor(String.class));
														mv.visitMethodInsn(
																INVOKEVIRTUAL,
																"java/lang/StringBuilder",
																"append",
																"(Ljava/lang/String;)Ljava/lang/StringBuilder;");
													} else {
														if (paramType
																.isPrimitive()) {
															switch (paramType
																	.getName()) {
															case "int":
															case "short":
															case "byte":
																paramDes = "I";// 隐式的类型转换
																break;
															default:
																paramDes = Type
																		.getDescriptor(paramType);
																break;
															}
														} else {
															paramDes = Type
																	.getDescriptor(Object.class);// 隐式的类型转换
														}
														mv.visitMethodInsn(
																INVOKEVIRTUAL,
																"java/lang/StringBuilder",
																"append",
																"("
																		+ paramDes
																		+ ")"
																		+ Type.getDescriptor(StringBuilder.class));
													}
													mv.visitLdcInsn(CACHE_SPLITER);
													mv.visitMethodInsn(
															INVOKEVIRTUAL,
															"java/lang/StringBuilder",
															"append",
															"(Ljava/lang/String;)Ljava/lang/StringBuilder;");
												} else {
													// TODO:Support
													// autopaging/filter
													throw new RuntimeException(
															"不识别的CacheKeyType");
												}
											}
										}
										indexOfParam++;
									}
								}
							}
							mv.visitMethodInsn(INVOKEVIRTUAL,
									"java/lang/StringBuilder", "toString",
									"()Ljava/lang/String;");
							varTb.storeLocalVar(mv, String.class);
						}
						// if (CommonConfig.isDebug) {
						// // 输出cachekey
						// mv.visitFieldInsn(GETSTATIC, "java/lang/System",
						// "out", "Ljava/io/PrintStream;");
						// varTb.loadLocalVar(mv, varTb.nextFreeSlotPos() - 1);
						// mv.visitMethodInsn(INVOKEVIRTUAL,
						// "java/io/PrintStream", "println",
						// "(Ljava/lang/String;)V");
						// }
						{
							// 2.ICacheManager localCacheManager4Redis =
							// CacheManager4Redis.getSingleton();
							if (CommonConfig.Instance.cacheType
									.equals(CacheDBType.Redis)) {
								mv.visitMethodInsn(INVOKESTATIC,
										"net/pocrd/util/CacheManager4Redis",
										"getSingleton",
										"()Lnet/pocrd/util/CacheManager4Redis;");
								varTb.storeLocalVar(mv,
										CacheManager4Redis.class);
							} else {
								// TODO:Support memcache
								throw new RuntimeException("不识别的CacheDBType");
							}
						}
						{
							// 3.Object obj = cacheManager.get(cachekey);
							varTb.loadLocalVar(mv,
									varTb.nextLocalVarIndex() - 1);
							varTb.loadLocalVar(mv,
									varTb.nextLocalVarIndex() - 2);
							mv.visitMethodInsn(INVOKEINTERFACE,
									"net/pocrd/util/ICacheManager", "get",
									"(Ljava/lang/String;)Ljava/lang/Object;");
							varTb.storeLocalVar(mv, ICacheManager.class);
						}
						{
							// 4.if(obj==null)
							varTb.loadLocalVar(mv,
									varTb.nextLocalVarIndex() - 1);
							mv.visitJumpInsn(IFNONNULL, ljump0);
						}
						{
							// 5.DemoEntity demo=super.getDemoEntity();
							for (int i = 0; i <= m.getParameterTypes().length; i++) {
								// start from this
								varTb.loadLocalVar(mv, i);
							}
							mv.visitMethodInsn(INVOKESPECIAL, superClassName,
									m.getName(), Type.getMethodDescriptor(m));
							varTb.storeLocalVar(mv, m.getReturnType());
						}
						{
							// 6.if(demo!=null)
							if (!m.getReturnType().isPrimitive()) {
								varTb.loadLocalVar(mv,
										varTb.nextLocalVarIndex() - 1);
								mv.visitJumpInsn(IFNULL, ljump1);
							}
						}
						{
							// 7.localCacheManager4Redis.set(cachekey,demoEntity,expire);
							varTb.loadLocalVar(mv,
									varTb.nextLocalVarIndex() - 3);
							varTb.loadLocalVar(mv,
									varTb.nextLocalVarIndex() - 4);
							varTb.loadLocalVar(mv,
									varTb.nextLocalVarIndex() - 1);
							// inbox所有值类型都缓存
							if (m.getReturnType().isPrimitive())
								BytecodeUtil.inbox(mv, m.getReturnType());
							mv.visitIntInsn(BIPUSH, expire);
							mv.visitMethodInsn(INVOKEINTERFACE,
									"net/pocrd/util/ICacheManager", "set",
									"(Ljava/lang/String;Ljava/lang/Object;I)Z");
							mv.visitInsn(POP);
							varTb.loadLocalVar(mv,
									varTb.nextLocalVarIndex() - 1);
							BytecodeUtil.doReturn(mv, m.getReturnType());
						}
						{
							// 8.return null;
							if (!m.getReturnType().isPrimitive()) {
								mv.visitLabel(ljump1);
								mv.visitInsn(ACONST_NULL);
								BytecodeUtil.doReturn(mv, m.getReturnType());
							}
						}
						{
							// if (obj instanceof Integer)
							// return ((Integer) obj).intValue();
							{
								 mv.visitLabel(ljump0);
								 varTb.loadLocalVar(mv,
								 varTb.nextLocalVarIndex() - 2);
								 BytecodeUtil
								 .doInstanceof(mv, m.getReturnType());
								 mv.visitJumpInsn(IFEQ, ljump2);
								 varTb.loadLocalVar(mv,
								 varTb.nextLocalVarIndex() - 2);
								 BytecodeUtil.doCast(mv, m.getReturnType());
								 BytecodeUtil.doReturn(mv, m.getReturnType());
							}
							{
								// else throw new RuntimeException(...);
								 mv.visitLabel(ljump2);
								 mv.visitTypeInsn(NEW,
								 "java/lang/RuntimeException");
								 mv.visitInsn(DUP);
								 mv.visitTypeInsn(NEW,
								 "java/lang/StringBuilder");
								 mv.visitInsn(DUP);
								 mv.visitLdcInsn("Cache object conflict,key:");
								 mv.visitMethodInsn(INVOKESPECIAL,
								 "java/lang/StringBuilder", "<init>",
								 "(Ljava/lang/String;)V");
								 varTb.loadLocalVar(mv,
								 varTb.nextLocalVarIndex() - 4);
								 mv.visitMethodInsn(INVOKEVIRTUAL,
								 "java/lang/StringBuilder", "append",
								 "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
								 mv.visitMethodInsn(INVOKEVIRTUAL,
								 "java/lang/StringBuilder", "toString",
								 "()Ljava/lang/String;");
								 mv.visitMethodInsn(INVOKESPECIAL,
								 "java/lang/RuntimeException", "<init>",
								 "(Ljava/lang/String;)V");
								 mv.visitInsn(ATHROW);
							}
						}
						mv.visitMaxs(0, 0);
					}
					mv.visitEnd();
				}
			}
			cw.visitEnd();
			if (CommonConfig.isDebug) {
				outPutClassFile("Cache_" + clazz.getSimpleName(),
						cw.toByteArray());
			}
			T e = (T) new PocClassLoader(Thread.currentThread()
					.getContextClassLoader()).defineClass(
					className.replace('/', '.'), cw.toByteArray())
					.newInstance();
			return e;
		} catch (Exception e) {
			throw new RuntimeException("generate failed. " + clazz.getName(), e);
		}
	}

	private static void outPutClassFile(String fileName, byte[] byteArray) {
		FileOutputStream fos = null;
		try {
			File folder = new File(CommonConfig.Instance.autogenPath
					+ "\\CachedClass\\");
			if (!folder.exists())
				folder.mkdirs();
			fos = new FileOutputStream(CommonConfig.Instance.autogenPath
					+ "\\CachedClass\\" + fileName + ".class");
			fos.write(byteArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
