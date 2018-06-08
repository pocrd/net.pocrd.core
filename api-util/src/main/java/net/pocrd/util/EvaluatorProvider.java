package net.pocrd.util;

import net.pocrd.annotation.Description;
import net.pocrd.core.PocClassLoader;
import net.pocrd.define.ConstField;
import net.pocrd.define.Evaluator;
import net.pocrd.entity.CommonConfig;
import net.pocrd.entity.CompileConfig;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 赋值工具, 能够为指定两个类型中的同名成员生成赋值代码
 * 1. 对于没有被 net.pocrd.annotation.Description 标记的成员进行浅层拷贝, 既只有当成员名称和成员类型完全相同的情况下才做赋值
 * 2. 对于成员和成员类型都被 net.pocrd.annotation.Description 标记的成员做递归拷贝, 既生成这两个成员变量类型间的赋值器, 并调用
 * 3. 对于声明为List类型的成员, 比较两者的泛型类型, 如果满足2中的要求, 则进行递归拷贝.
 * 4. 增加了 long 与 java.util.Date 之间的赋值功能, 当把0赋值给Date类型对象时, 会赋值为null, 而不是 new Date(0)
 * 请保证调用时用于赋值的对象中没有实例循环引用的数据成员, 否则会导致 StackOverflowError
 */
public class EvaluatorProvider implements Opcodes {

    private static final ConcurrentHashMap<String, Evaluator<?, ?>> cache = new ConcurrentHashMap<String, Evaluator<?, ?>>();

    public static <TLeft, TRight> Evaluator<TLeft, TRight> getEvaluator(Class<TLeft> leftClass, Class<TRight> rightClass) {
        String key = leftClass.getName() + "___" + rightClass.getName();
        Evaluator<TLeft, TRight> evaluator = (Evaluator<TLeft, TRight>)cache.get(key);
        if (evaluator == null) {
            synchronized (cache) {
                evaluator = (Evaluator<TLeft, TRight>)cache.get(key);
                if (evaluator == null) {
                    evaluator = createEvaluator(leftClass, rightClass);
                    cache.put(key, evaluator);
                }
            }
        }
        return evaluator;
    }

    /**
     * 由于jdk 1.8 改用 Metaspace 后重复调用 defineClass 可能导致内存泄漏, 要求所有直接产生字节码的工具类进行本地缓存。
     */
    private static <TLeft, TRight> Evaluator<TLeft, TRight> createEvaluator(Class<TLeft> leftClass, Class<TRight> rightClass) {
        ClassWriter cw = new PocClassWriter(ClassWriter.COMPUTE_FRAMES);
        String l_name = Type.getInternalName(leftClass);
        String r_name = Type.getInternalName(rightClass);
        String className = "net.pocrd.autogen.Evaluator_" + l_name.substring(l_name.lastIndexOf('/') + 1)
                .replace("/", "") + "_" + r_name.substring(r_name.lastIndexOf('/') + 1).replace("/", "");
        className = className.replace('$', '_');
        String c_name = className.replace('.', '/');
        String c_desc = "L" + c_name + ";";
        String e_name = Type.getInternalName(Evaluator.class);
        String l_desc = Type.getDescriptor(leftClass);
        String r_desc = Type.getDescriptor(rightClass);
        HashSet<String> getters = new HashSet<>();
        HashSet<Class> innerClasses = new HashSet<>();
        createInnerClassVisitor(cw, innerClasses, leftClass);
        createInnerClassVisitor(cw, innerClasses, rightClass);
        try {
            cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, c_name,
                    "Ljava/lang/Object;Lnet/pocrd/define/Evaluator<" + l_desc + r_desc + ">;",
                    "java/lang/Object", new String[] { e_name });
            {
                MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
                mv.visitInsn(RETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", c_desc, null, l0, l1, 0);
                mv.visitMaxs(1, 1);
                mv.visitEnd();
            }
            {
                PocMethodVisitor pmv = new PocMethodVisitor(cw, ACC_PUBLIC, "evaluate", "(" + l_desc + r_desc + ")V", null, null);
                pmv.visitCode();
                Label l0 = new Label();
                pmv.visitLabel(l0);
                pmv.visitVarInsn(ALOAD, 1);
                Label l1 = new Label();
                pmv.visitJumpInsn(IFNULL, l1);
                pmv.visitVarInsn(ALOAD, 2);
                pmv.visitJumpInsn(IFNULL, l1);
                HashMap<String, Field> frs = new HashMap<String, Field>();
                HashMap<String, Method> mrs = new HashMap<String, Method>();
                for (Field fr : rightClass.getFields()) {
                    int mod = fr.getModifiers();
                    if (Modifier.isPublic(mod) && !Modifier.isStatic(mod)) {
                        frs.put(fr.getName(), fr);
                    }
                }
                for (Method mr : rightClass.getMethods()) {
                    int mod = mr.getModifiers();
                    String name = mr.getName();
                    if (Modifier.isPublic(mod) && !Modifier.isStatic(mod) && name.length() > 3 && (name.startsWith("get")
                            || name.startsWith("is") && mr.getReturnType() == boolean.class) && mr.getParameterTypes().length == 0) {
                        name = name.startsWith("get") ? name.substring(3) : name.substring(2);
                        if (name.length() > 1) {
                            name = name.substring(0, 1).toLowerCase() + name.substring(1);
                        } else {
                            name = name.toLowerCase();
                        }
                        mrs.put(name, mr);
                    }
                }

                for (Field fl : leftClass.getFields()) {
                    int mod = fl.getModifiers();
                    if (Modifier.isPublic(mod) && !Modifier.isFinal(mod) && !Modifier.isStatic(mod)) {
                        String name = fl.getName();
                        if (frs.containsKey(name)) {
                            Field fr = frs.get(name);
                            if (fl.getType() == fr.getType()) {
                                if (fl.getGenericType().getTypeName().equals(fr.getGenericType().getTypeName())) {
                                    pmv.visitVarInsn(ALOAD, 1);
                                    pmv.visitVarInsn(ALOAD, 2);
                                    pmv.visitFieldInsn(GETFIELD, r_name, name, Type.getDescriptor(fr.getType()));
                                    pmv.visitFieldInsn(PUTFIELD, l_name, name, Type.getDescriptor(fl.getType()));
                                } else {
                                    if (fl.getType() == List.class && fl.getAnnotation(Description.class) != null
                                            && fr.getAnnotation(Description.class) != null) {
                                        Class lgc = TypeCheckUtil.getSupportedGenericClass(fl.getGenericType(), leftClass.getName()
                                                + " " + fl.getName());
                                        Class rgc = TypeCheckUtil.getSupportedGenericClass(fr.getGenericType(), rightClass.getName()
                                                + " " + fr.getName());
                                        if (lgc.getAnnotation(Description.class) != null && rgc.getAnnotation(Description.class) != null) {
                                            createInnerClassVisitor(cw, innerClasses, lgc);
                                            createInnerClassVisitor(cw, innerClasses, rgc);
                                            String getter = createEvaluatorGetter(cw, c_name, getters, lgc, rgc);

                                            pmv.loadArg(2);
                                            pmv.visitFieldInsn(GETFIELD, r_name, name, Type.getDescriptor(fr.getType()));
                                            Label rNullCheck = new Label();
                                            pmv.visitJumpInsn(IFNULL, rNullCheck);
                                            pmv.loadArg(1);
                                            pmv.visitFieldInsn(GETFIELD, l_name, name, Type.getDescriptor(fl.getType()));
                                            Label lNullCheck = new Label();
                                            pmv.visitJumpInsn(IFNONNULL, lNullCheck);
                                            pmv.loadArg(1);
                                            pmv.visitTypeInsn(NEW, "java/util/ArrayList");
                                            pmv.visitInsn(DUP);
                                            pmv.loadArg(2);
                                            pmv.visitFieldInsn(GETFIELD, r_name, name, Type.getDescriptor(fr.getType()));
                                            pmv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I");
                                            pmv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "(I)V");
                                            pmv.visitFieldInsn(PUTFIELD, l_name, name, Type.getDescriptor(fl.getType()));
                                            pmv.visitLabel(lNullCheck);
                                            pmv.visitInsn(ICONST_0);
                                            pmv.declareLocal("i", int.class);
                                            pmv.setLocal("i");
                                            pmv.loadArg(2);
                                            pmv.visitFieldInsn(GETFIELD, r_name, name, Type.getDescriptor(fr.getType()));
                                            pmv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;");
                                            pmv.declareRefLocal("rIterator");
                                            pmv.setLocal("rIterator");
                                            Label loop = new Label();
                                            pmv.visitLabel(loop);
                                            pmv.loadLocal("rIterator");
                                            pmv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
                                            pmv.visitJumpInsn(IFEQ, rNullCheck);
                                            pmv.loadLocal("rIterator");
                                            pmv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
                                            pmv.visitTypeInsn(CHECKCAST, Type.getInternalName(rgc));
                                            pmv.declareRefLocal("rItem");
                                            pmv.setLocal("rItem");
                                            pmv.visitInsn(ACONST_NULL);
                                            pmv.declareRefLocal("lItem");
                                            pmv.setLocal("lItem");
                                            pmv.loadArg(1);
                                            pmv.visitFieldInsn(GETFIELD, l_name, name, Type.getDescriptor(fl.getType()));
                                            pmv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I");
                                            pmv.loadLocal("i");
                                            Label checkLItemElse = new Label();
                                            pmv.visitJumpInsn(IF_ICMPLE, checkLItemElse);
                                            pmv.loadArg(1);
                                            pmv.visitFieldInsn(GETFIELD, l_name, name, Type.getDescriptor(fl.getType()));
                                            pmv.loadLocal("i");
                                            pmv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;");
                                            pmv.visitTypeInsn(CHECKCAST, Type.getInternalName(fl.getType()));
                                            pmv.setLocal("lItem");
                                            Label checkLItemEnd = new Label();
                                            pmv.visitJumpInsn(GOTO, checkLItemEnd);
                                            pmv.visitLabel(checkLItemElse);
                                            pmv.visitTypeInsn(NEW, Type.getInternalName(lgc));
                                            pmv.visitInsn(DUP);
                                            pmv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(lgc), "<init>", "()V");
                                            pmv.setLocal("lItem");
                                            pmv.loadArg(1);
                                            pmv.visitFieldInsn(GETFIELD, l_name, name, Type.getDescriptor(fl.getType()));
                                            pmv.loadLocal("lItem");
                                            pmv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z");
                                            pmv.visitInsn(POP);
                                            pmv.visitLabel(checkLItemEnd);
                                            pmv.loadArg(0);
                                            pmv.visitMethodInsn(INVOKEVIRTUAL, c_name, "get_" + getter, "()Lnet/pocrd/define/Evaluator;");
                                            pmv.loadLocal("lItem");
                                            pmv.loadLocal("rItem");
                                            pmv.visitMethodInsn(INVOKEINTERFACE, "net/pocrd/define/Evaluator", "evaluate",
                                                    "(Ljava/lang/Object;Ljava/lang/Object;)V");

                                            pmv.incrementLocal("i", 1);
                                            pmv.visitJumpInsn(GOTO, loop);
                                            pmv.visitLabel(rNullCheck);
                                            pmv.deleteLocal("lItem");
                                            pmv.deleteLocal("rItem");
                                            pmv.deleteLocal("rIterator");
                                            pmv.deleteLocal("i");
                                        }
                                    }
                                }
                            } else if (fl.getType() == Date.class && fr.getType() == long.class) {
                                //允许long到date的拷贝 0不会导致Date的初始化
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitFieldInsn(GETFIELD, r_name, name, "J");
                                pmv.loadConst(0L);
                                pmv.visitInsn(LCMP);
                                Label isZeroUTC = new Label();
                                pmv.visitJumpInsn(IFEQ, isZeroUTC);
                                pmv.visitVarInsn(ALOAD, 1);
                                pmv.visitTypeInsn(NEW, "java/util/Date");
                                pmv.visitInsn(DUP);
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitFieldInsn(GETFIELD, r_name, name, "J");
                                pmv.visitMethodInsn(INVOKESPECIAL, "java/util/Date", "<init>", "(J)V");
                                pmv.visitFieldInsn(PUTFIELD, l_name, name, "Ljava/util/Date;");
                                pmv.visitLabel(isZeroUTC);
                            } else if (fl.getType() == long.class && fr.getType() == Date.class) {
                                //允许dateTime到long的拷贝
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitFieldInsn(GETFIELD, r_name, name, "Ljava/util/Date;");
                                Label isNullDate = new Label();
                                pmv.visitJumpInsn(IFNULL, isNullDate);
                                pmv.visitVarInsn(ALOAD, 1);
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitFieldInsn(GETFIELD, r_name, name, "Ljava/util/Date;");
                                pmv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Date", "getTime", "()J");
                                pmv.visitFieldInsn(PUTFIELD, l_name, name, "J");
                                pmv.visitLabel(isNullDate);
                            } else if (fl.getAnnotation(Description.class) != null
                                    && fr.getAnnotation(Description.class) != null
                                    && fl.getType().getAnnotation(Description.class) != null
                                    && fr.getType().getAnnotation(Description.class) != null) {

                                createInnerClassVisitor(cw, innerClasses, fl.getType());
                                createInnerClassVisitor(cw, innerClasses, fr.getType());
                                String getter = createEvaluatorGetter(cw, c_name, getters, fl.getType(), fr.getType());

                                pmv.loadArg(2);
                                pmv.visitFieldInsn(GETFIELD, r_name, name, Type.getDescriptor(fr.getType()));
                                Label rNullCheck = new Label();
                                pmv.visitJumpInsn(IFNULL, rNullCheck);
                                pmv.loadArg(1);
                                pmv.visitFieldInsn(GETFIELD, l_name, name, Type.getDescriptor(fl.getType()));
                                Label lNullCheck = new Label();
                                pmv.visitJumpInsn(IFNONNULL, lNullCheck);
                                pmv.loadArg(1);
                                pmv.visitTypeInsn(NEW, Type.getInternalName(fl.getType()));
                                pmv.visitInsn(DUP);
                                pmv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(fl.getType()), "<init>", "()V");
                                pmv.visitFieldInsn(PUTFIELD, l_name, name, Type.getDescriptor(fl.getType()));
                                pmv.visitLabel(lNullCheck);
                                pmv.loadArg(0);
                                pmv.visitMethodInsn(INVOKEVIRTUAL, c_name, "get_" + getter, "()Lnet/pocrd/define/Evaluator;");
                                pmv.visitVarInsn(ALOAD, 1);
                                pmv.visitFieldInsn(GETFIELD, l_name, name, Type.getDescriptor(fl.getType()));
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitFieldInsn(GETFIELD, r_name, name, Type.getDescriptor(fr.getType()));

                                pmv.visitMethodInsn(INVOKEINTERFACE, "net/pocrd/define/Evaluator", "evaluate",
                                        "(Ljava/lang/Object;Ljava/lang/Object;)V");
                                pmv.visitLabel(rNullCheck);
                            }
                        } else if (mrs.containsKey(name)) {
                            Method mr = mrs.get(name);
                            if (fl.getType() == mr.getReturnType()) {
                                if (fl.getGenericType().getTypeName().equals(mr.getGenericReturnType().getTypeName())) {
                                    pmv.visitVarInsn(ALOAD, 1);
                                    pmv.visitVarInsn(ALOAD, 2);
                                    pmv.visitMethodInsn(INVOKEVIRTUAL, r_name, mr.getName(), Type.getMethodDescriptor(mr));
                                    pmv.visitFieldInsn(PUTFIELD, l_name, name, Type.getDescriptor(fl.getType()));
                                }
                            } else if (fl.getType() == Date.class && mr.getReturnType() == long.class) {
                                //允许long到date的双向拷贝
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitMethodInsn(INVOKEVIRTUAL, r_name, mr.getName(), "()J");
                                pmv.visitInsn(LCONST_0);
                                pmv.visitInsn(LCMP);
                                Label isZeroUTC = new Label();
                                pmv.visitJumpInsn(IFEQ, isZeroUTC);
                                pmv.visitVarInsn(ALOAD, 1);
                                pmv.visitTypeInsn(NEW, "java/util/Date");
                                pmv.visitInsn(DUP);
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitMethodInsn(INVOKEVIRTUAL, r_name, mr.getName(), "()J");
                                pmv.visitMethodInsn(INVOKESPECIAL, "java/util/Date", "<init>", "(J)V");
                                pmv.visitFieldInsn(PUTFIELD, l_name, name, "Ljava/util/Date;");
                                pmv.visitLabel(isZeroUTC);
                            } else if (fl.getType() == long.class && mr.getReturnType() == Date.class) {
                                //允许long到dateTime的双向拷贝
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitMethodInsn(INVOKEVIRTUAL, r_name, mr.getName(), "()Ljava/util/Date;");
                                Label isNullDate = new Label();
                                pmv.visitJumpInsn(IFNULL, isNullDate);
                                pmv.visitVarInsn(ALOAD, 1);
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitMethodInsn(INVOKEVIRTUAL, r_name, mr.getName(), "()Ljava/util/Date;");
                                pmv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Date", "getTime", "()J");
                                pmv.visitFieldInsn(PUTFIELD, l_name, name, "J");
                                pmv.visitLabel(isNullDate);
                            }
                        }
                    }
                }
                for (Method ml : leftClass.getMethods()) {
                    int mod = ml.getModifiers();
                    String name = ml.getName();
                    if (Modifier.isPublic(mod) && name.length() > 3 && name.startsWith("set")
                            && ml.getParameterTypes().length == 1) {
                        name = name.substring(3);
                        if (name.length() > 1) {
                            name = name.substring(0, 1).toLowerCase() + name.substring(1);
                        } else {
                            name = name.toLowerCase();
                        }
                        if (frs.containsKey(name)) {
                            Field fr = frs.get(name);
                            if (ml.getParameterTypes()[0] == fr.getType()) {
                                if (ml.getGenericParameterTypes()[0].getTypeName().equals(fr.getGenericType().getTypeName())) {
                                    pmv.visitVarInsn(ALOAD, 1);
                                    pmv.visitVarInsn(ALOAD, 2);
                                    pmv.visitFieldInsn(GETFIELD, r_name, name, Type.getDescriptor(fr.getType()));
                                    pmv.visitMethodInsn(INVOKEVIRTUAL, l_name, ml.getName(), Type.getMethodDescriptor(ml));
                                }
                            } else if (ml.getParameterTypes()[0] == Date.class && fr.getType() == long.class) {
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitFieldInsn(GETFIELD, r_name, name, "J");
                                pmv.visitInsn(LCONST_0);
                                pmv.visitInsn(LCMP);
                                Label isZeroUTC = new Label();
                                pmv.visitJumpInsn(IFEQ, isZeroUTC);
                                pmv.visitVarInsn(ALOAD, 1);
                                pmv.visitTypeInsn(NEW, "java/util/Date");
                                pmv.visitInsn(DUP);
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitFieldInsn(GETFIELD, r_name, name, "J");
                                pmv.visitMethodInsn(INVOKESPECIAL, "java/util/Date", "<init>", "(J)V");
                                pmv.visitMethodInsn(INVOKEVIRTUAL, l_name, ml.getName(), "(Ljava/util/Date;)V");
                                pmv.visitLabel(isZeroUTC);
                            } else if (ml.getParameterTypes()[0] == long.class && fr.getType() == Date.class) {
                                //允许long到dateTime的双向拷贝
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitFieldInsn(GETFIELD, r_name, name, "Ljava/util/Date;");
                                Label isNullDate = new Label();
                                pmv.visitJumpInsn(IFNULL, isNullDate);
                                pmv.visitVarInsn(ALOAD, 1);
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitFieldInsn(GETFIELD, r_name, name, "Ljava/util/Date;");
                                pmv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Date", "getTime", "()J");
                                pmv.visitMethodInsn(INVOKEVIRTUAL, l_name, ml.getName(), "(J)V");
                                pmv.visitLabel(isNullDate);
                            }
                        } else if (mrs.containsKey(name)) {
                            Method mr = mrs.get(name);
                            if (ml.getParameterTypes()[0] == mr.getReturnType()) {
                                if (ml.getGenericParameterTypes()[0].getTypeName().equals(mr.getGenericReturnType().getTypeName())) {
                                    pmv.visitVarInsn(ALOAD, 1);
                                    pmv.visitVarInsn(ALOAD, 2);
                                    pmv.visitMethodInsn(INVOKEVIRTUAL, r_name, mr.getName(), Type.getMethodDescriptor(mr));
                                    pmv.visitMethodInsn(INVOKEVIRTUAL, l_name, ml.getName(), Type.getMethodDescriptor(ml));
                                }
                            } else if (ml.getParameterTypes()[0] == Date.class && mr.getReturnType() == long.class) {
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitMethodInsn(INVOKEVIRTUAL, r_name, mr.getName(), "()J");
                                pmv.visitInsn(LCONST_0);
                                pmv.visitInsn(LCMP);
                                Label isZeroUTC = new Label();
                                pmv.visitJumpInsn(IFEQ, isZeroUTC);
                                pmv.visitVarInsn(ALOAD, 1);
                                pmv.visitTypeInsn(NEW, "java/util/Date");
                                pmv.visitInsn(DUP);
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitMethodInsn(INVOKEVIRTUAL, r_name, mr.getName(), "()J");
                                pmv.visitMethodInsn(INVOKESPECIAL, "java/util/Date", "<init>", "(J)V");
                                pmv.visitMethodInsn(INVOKEVIRTUAL, l_name, ml.getName(), "(Ljava/util/Date;)V");
                                pmv.visitLabel(isZeroUTC);
                            } else if (ml.getParameterTypes()[0] == long.class && mr.getReturnType() == Date.class) {
                                //允许long到dateTime的双向拷贝
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitMethodInsn(INVOKEVIRTUAL, r_name, mr.getName(), "()Ljava/util/Date;");
                                Label isNullDate = new Label();
                                pmv.visitJumpInsn(IFNULL, isNullDate);
                                pmv.visitVarInsn(ALOAD, 1);
                                pmv.visitVarInsn(ALOAD, 2);
                                pmv.visitMethodInsn(INVOKEVIRTUAL, r_name, mr.getName(), "()Ljava/util/Date;");
                                pmv.visitMethodInsn(INVOKEVIRTUAL, "java/util/Date", "getTime", "()J");
                                pmv.visitMethodInsn(INVOKEVIRTUAL, l_name, ml.getName(), "(J)V");
                                pmv.visitLabel(isNullDate);
                            }
                        }
                    }
                }
                pmv.visitLabel(l1);
                pmv.visitInsn(RETURN);
                pmv.visitMaxs(0, 0);
                pmv.visitEnd();
            }
            {
                MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "evaluate",
                        "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitTypeInsn(CHECKCAST, l_name);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitTypeInsn(CHECKCAST, r_name);
                mv.visitMethodInsn(INVOKEVIRTUAL, c_name, "evaluate", "(" + l_desc + r_desc + ")V");
                mv.visitInsn(RETURN);
                mv.visitMaxs(3, 3);
                mv.visitEnd();
            }
            cw.visitEnd();
            if (CompileConfig.isDebug) {
                FileOutputStream fos = null;
                try {
                    File folder = new File(
                            CommonConfig.getInstance().getAutogenPath() + File.separator + "Evaluator" + File.separator);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    fos = new FileOutputStream(
                            CommonConfig.getInstance().getAutogenPath() + File.separator + "Evaluator" + File.separator
                                    + className + ".class");
                    fos.write(cw.toByteArray());
                } finally {
                    if (fos != null) {
                        fos.close();
                    }
                }
            }
            return (Evaluator<TLeft, TRight>)new PocClassLoader(Thread.currentThread().getContextClassLoader())
                    .defineClass(className, cw.toByteArray()).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(className, e);
        }
    }

    private static <TLeft, TRight> String createEvaluatorGetter(ClassWriter cw, String className,
            HashSet<String> getters, Class<TLeft> leftClass, Class<TRight> rightClass) {
        String key = HexStringUtil.toHexString(Md5Util.compute((leftClass.getName() + "___" + rightClass.getName()).getBytes(ConstField.UTF8)));
        if (!getters.contains(key)) {
            String filedName = "f_" + key;
            FieldVisitor fv = cw.visitField(ACC_PRIVATE, filedName, "Lnet/pocrd/define/Evaluator;", null, null);
            fv.visitEnd();
            PocMethodVisitor pmv = new PocMethodVisitor(cw, ACC_PRIVATE, "get_" + key, "()Lnet/pocrd/define/Evaluator;", null, null);
            pmv.visitCode();
            pmv.loadArg(0);
            pmv.visitFieldInsn(GETFIELD, className, filedName, "Lnet/pocrd/define/Evaluator;");
            Label l1 = new Label();
            pmv.visitJumpInsn(IFNONNULL, l1);
            pmv.loadArg(0);
            pmv.visitLdcInsn(Type.getType(leftClass));
            pmv.visitLdcInsn(Type.getType(rightClass));
            pmv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/EvaluatorProvider", "getEvaluator",
                    "(Ljava/lang/Class;Ljava/lang/Class;)Lnet/pocrd/define/Evaluator;");
            pmv.visitFieldInsn(PUTFIELD, className, filedName, "Lnet/pocrd/define/Evaluator;");
            pmv.visitLabel(l1);
            pmv.loadArg(0);
            pmv.visitFieldInsn(GETFIELD, className, filedName, "Lnet/pocrd/define/Evaluator;");
            pmv.visitInsn(ARETURN);
            pmv.visitMaxs(0, 0);
            pmv.visitEnd();
            getters.add(key);
        }
        return key;
    }

    private static void createInnerClassVisitor(ClassWriter cw, HashSet<Class> innerClasses, Class clazz) {
        if (clazz.getEnclosingClass() != null && !innerClasses.contains(clazz)) {
            do {
                cw.visitInnerClass(Type.getInternalName(clazz), Type.getInternalName(clazz.getEnclosingClass()),
                        clazz.getSimpleName(), ACC_PUBLIC + ACC_STATIC);
                innerClasses.add(clazz);
            } while ((clazz = clazz.getEnclosingClass()).getEnclosingClass() != null);
        }
    }
}
