package net.pocrd.util;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;

public class BytecodeUtil implements Opcodes {

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
        cw.visitInnerClass(clazz.getName().replace('.', '/'), dc.getName().replace('.', '/'), clazz.getSimpleName(), flag);
    }
}
