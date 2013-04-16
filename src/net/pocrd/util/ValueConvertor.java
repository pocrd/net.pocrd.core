package net.pocrd.util;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class ValueConvertor implements Opcodes {

    private static HashMap<String, Evaluater<?, ?>> cache = new HashMap<String, Evaluater<?, ?>>();

    @SuppressWarnings("unchecked")
    public static <TLeft, TRight> void evaluate(TLeft l, TRight r) {
        Evaluater<TLeft, TRight> evaluater = (Evaluater<TLeft, TRight>)getConvertor(l.getClass(), r.getClass());
        evaluater.evaluate(l, r);
    }

    @SuppressWarnings("unchecked")
    private static <TLeft, TRight> Evaluater<TLeft, TRight> getConvertor(Class<TLeft> leftClass, Class<TRight> rightClass) {
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

        try {
            ClassWriter cw = new ClassWriter(0);
            MethodVisitor mv;
            String l_name = leftClass.getName().replace('.', '/');
            String r_name = rightClass.getName().replace('.', '/');
            String className = "net.pocrd.autogen.Evaluator_" + l_name.substring(l_name.lastIndexOf('/') + 1).replace("/", "") + "_"
                    + r_name.substring(r_name.lastIndexOf('/') + 1).replace("/", "");
            className = className.replace('$', '_');
            String c_name = className.replace('.', '/');
            String c_desc = "L" + c_name + ";";
            String e_name = Evaluater.class.getName().replace('.', '/');
            String l_desc = Type.getDescriptor(leftClass);
            String r_desc = Type.getDescriptor(rightClass);
            String e_desc = Type.getDescriptor(Evaluater.class);

            cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, c_name, "Ljava/lang/Object;" + e_desc + "<" + l_desc + r_desc + ">;", "java/lang/Object",
                    new String[] { e_name });

            cw.visitSource(null, null);

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
                Label l2 = new Label();
                mv.visitJumpInsn(IFNONNULL, l2);
                mv.visitLabel(l1);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                mv.visitInsn(RETURN);
                mv.visitLabel(l2);
                mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                HashMap<String, Field> frs = new HashMap<String, Field>();
                for (Field fr : rightClass.getFields()) {
                    int mod = fr.getModifiers();
                    if (Modifier.isPublic(mod) && !Modifier.isFinal(mod) && !Modifier.isStatic(mod)) {
                        frs.put(fr.getName(), fr);
                    }
                }
                for (Field fl : leftClass.getFields()) {
                    int mod = fl.getModifiers();
                    if (Modifier.isPublic(mod) && !Modifier.isFinal(mod) && !Modifier.isStatic(mod)) {
                        String fname = fl.getName();
                        if (frs.containsKey(fname)) {
                            Field fr = frs.get(fname);
                            if (fl.getType() == fr.getType()) {
                                mv.visitVarInsn(ALOAD, 1);
                                mv.visitVarInsn(ALOAD, 2);
                                mv.visitFieldInsn(GETFIELD, r_name, fname, Type.getDescriptor(fl.getType()));
                                mv.visitFieldInsn(PUTFIELD, l_name, fname, Type.getDescriptor(fl.getType()));
                            }
                        }
                    }
                }
                mv.visitInsn(RETURN);
                Label l3 = new Label();
                mv.visitLabel(l3);
                mv.visitLocalVariable("this", c_desc, null, l0, l3, 0);
                mv.visitLocalVariable("left", l_desc, null, l0, l3, 1);
                mv.visitLocalVariable("right", r_desc, null, l0, l3, 2);
                mv.visitMaxs(3, 3);
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
            if (CommonConfig.isDebug) {
                FileOutputStream fos = null;
                try {
                    File folder = new File(CommonConfig.Instance.autogenPath + "\\ValueConvertor\\");
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    fos = new FileOutputStream(CommonConfig.Instance.autogenPath + "\\ValueConvertor\\" + className + ".class");
                    fos.write(cw.toByteArray());
                } finally {
                    if (fos != null) {
                        fos.close();
                    }
                }
            }
            return (Evaluater<TLeft, TRight>)new PocClassLoader().defineClass(className, cw.toByteArray()).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
