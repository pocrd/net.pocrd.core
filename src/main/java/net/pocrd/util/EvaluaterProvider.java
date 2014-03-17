package net.pocrd.util;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import net.pocrd.core.PocClassLoader;
import net.pocrd.define.CompileConfig;
import net.pocrd.define.Evaluater;
import net.pocrd.entity.CommonConfig;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class EvaluaterProvider implements Opcodes {

    private static ConcurrentHashMap<String, Evaluater<?, ?>> cache = new ConcurrentHashMap<String, Evaluater<?, ?>>();

    @SuppressWarnings("unchecked")
    public static <TLeft, TRight> Evaluater<TLeft, TRight> getEvaluater(Class<TLeft> leftClass, Class<TRight> rightClass) {
        String key = leftClass.getName() + "_" + rightClass.getName();
        Evaluater<TLeft, TRight> evaluater = (Evaluater<TLeft, TRight>)cache.get(key);
        if (evaluater == null) {
            synchronized (cache) {
                evaluater = (Evaluater<TLeft, TRight>)cache.get(key);
                if (evaluater == null) {
                    evaluater = createEvaluater(leftClass, rightClass);
                    cache.put(key, evaluater);
                }
            }
        }
        return evaluater;
    }

    @SuppressWarnings("unchecked")
    private static <TLeft, TRight> Evaluater<TLeft, TRight> createEvaluater(Class<TLeft> leftClass, Class<TRight> rightClass) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        MethodVisitor mv;
        String l_name = Type.getInternalName(leftClass);
        String r_name = Type.getInternalName(rightClass);
        String className = "net.pocrd.autogen.Evaluator_" + l_name.substring(l_name.lastIndexOf('/') + 1).replace("/", "") + "_"
                + r_name.substring(r_name.lastIndexOf('/') + 1).replace("/", "");
        className = className.replace('$', '_');
        String c_name = className.replace('.', '/');
        String c_desc = "L" + c_name + ";";
        String e_name = Type.getInternalName(Evaluater.class);
        String l_desc = Type.getDescriptor(leftClass);
        String r_desc = Type.getDescriptor(rightClass);
        try {
            cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, c_name, "Ljava/lang/Object;Lnet/pocrd/define/Evaluater<" + l_desc + r_desc + ">;",
                    "java/lang/Object", new String[] { e_name });
            {
                mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
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
                mv = cw.visitMethod(ACC_PUBLIC, "evaluate", "(" + l_desc + r_desc + ")V", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitVarInsn(ALOAD, 1);
                Label l1 = new Label();
                mv.visitJumpInsn(IFNULL, l1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitJumpInsn(IFNULL, l1);
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
                    if (Modifier.isPublic(mod) && !Modifier.isStatic(mod) && name.length() > 3 && name.startsWith("get")
                            && mr.getParameterTypes().length == 0) {
                        name = name.substring(3);
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
                                mv.visitVarInsn(ALOAD, 1);
                                mv.visitVarInsn(ALOAD, 2);
                                mv.visitFieldInsn(GETFIELD, r_name, name, Type.getDescriptor(fl.getType()));
                                mv.visitFieldInsn(PUTFIELD, l_name, name, Type.getDescriptor(fl.getType()));
                            }
                        } else if (mrs.containsKey(name)) {
                            Method mr = mrs.get(name);
                            if (fl.getType() == mr.getReturnType()) {
                                mv.visitVarInsn(ALOAD, 1);
                                mv.visitVarInsn(ALOAD, 2);
                                mv.visitMethodInsn(INVOKEVIRTUAL, r_name, mr.getName(), Type.getMethodDescriptor(mr));
                                mv.visitFieldInsn(PUTFIELD, l_name, name, Type.getDescriptor(fl.getType()));
                            }
                        }
                    }
                }
                for (Method ml : leftClass.getMethods()) {
                    int mod = ml.getModifiers();
                    String name = ml.getName();
                    if (Modifier.isPublic(mod) && name.length() > 3 && name.startsWith("set") && ml.getParameterTypes().length == 1) {
                        name = name.substring(3);
                        if (name.length() > 1) {
                            name = name.substring(0, 1).toLowerCase() + name.substring(1);
                        } else {
                            name = name.toLowerCase();
                        }
                        if (frs.containsKey(name)) {
                            Field fr = frs.get(name);
                            if (ml.getParameterTypes()[0] == fr.getType()) {
                                mv.visitVarInsn(ALOAD, 1);
                                mv.visitVarInsn(ALOAD, 2);
                                mv.visitFieldInsn(GETFIELD, r_name, name, Type.getDescriptor(fr.getType()));
                                mv.visitMethodInsn(INVOKEVIRTUAL, l_name, ml.getName(), Type.getMethodDescriptor(ml));
                            }
                        } else if (mrs.containsKey(name)) {
                            Method mr = mrs.get(name);
                            if (ml.getParameterTypes()[0] == mr.getReturnType()) {
                                mv.visitVarInsn(ALOAD, 1);
                                mv.visitVarInsn(ALOAD, 2);
                                mv.visitMethodInsn(INVOKEVIRTUAL, r_name, mr.getName(), Type.getMethodDescriptor(mr));
                                mv.visitMethodInsn(INVOKEVIRTUAL, l_name, ml.getName(), Type.getMethodDescriptor(ml));
                            }
                        }
                    }
                }
                mv.visitLabel(l1);
                mv.visitInsn(RETURN);
                Label l2 = new Label();
                mv.visitLabel(l2);
                mv.visitLocalVariable("this", c_desc, null, l0, l2, 0);
                mv.visitLocalVariable("left", l_desc, null, l0, l2, 1);
                mv.visitLocalVariable("right", r_desc, null, l0, l2, 2);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }
            {
                mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "evaluate", "(Ljava/lang/Object;Ljava/lang/Object;)V", null, null);
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
                    File folder = new File(CommonConfig.getInstance().autogenPath + File.separator + "Evaluater" + File.separator);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    fos = new FileOutputStream(CommonConfig.getInstance().autogenPath + File.separator + "Evaluater" + File.separator + className
                            + ".class");
                    fos.write(cw.toByteArray());
                } finally {
                    if (fos != null) {
                        fos.close();
                    }
                }
            }
            return (Evaluater<TLeft, TRight>)new PocClassLoader(Thread.currentThread().getContextClassLoader()).defineClass(className,
                    cw.toByteArray()).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(className, e);
        }
    }
}
