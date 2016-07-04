package net.pocrd.core.test;

import net.pocrd.core.PocClassLoader;
import net.pocrd.util.MethodVisitorWrapper;
import net.pocrd.util.PocClassWriter;
import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;

public class MethodVisitorWrapperTest implements Opcodes {
    @Test
    public void TestGen() throws Exception {
        ClassWriter cw = new PocClassWriter(ClassWriter.COMPUTE_FRAMES);
        MethodVisitorWrapper mv;
        cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, "net/pocrd/test/core/util/MethodVisitorWrapperTest", null, "java/lang/Object", null);
        cw.visitSource("MethodVisitorWrapperTest.java", null);
        {
            mv = new MethodVisitorWrapper(cw, ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            // mv.declareArgs(false, null);
            mv.visitLabel(l0);
            mv.loadArg(0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
            mv.visitInsn(RETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "Lnet/pocrd/test/core/util/MethodVisitorWrapperTest;", null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = new MethodVisitorWrapper(cw, ACC_PUBLIC + ACC_STATIC, "staticMain", "([Ljava/lang/String;)V", null, null);
            mv.visitCode();
            // mv.declareArgs(true, new Class<?>[] { String[].class });
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("Hello world!");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        {
            mv = new MethodVisitorWrapper(cw, ACC_PUBLIC, "main", "(Ljava/lang/String;)V", null, null);
            mv.visitCode();
            // mv.declareArgs(false, new Class<?>[] { String.class });
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitLdcInsn("Hello world,");
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "(Ljava/lang/String;)V");
            mv.loadArg(1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
            mv.visitLdcInsn("!");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        cw.visitEnd();
        // FileOutputStream output = new FileOutputStream("./bin/MethodVisitorTest.class");
        // byte[] content = cw.toByteArray();
        // output.write(content, 0, content.length);
        // output.close();
        PocClassLoader pocClassLoader = new PocClassLoader(Thread.currentThread().getContextClassLoader());
        Class<?> testClass = pocClassLoader.defineClass("net.pocrd.test.core.util.MethodVisitorWrapperTest", cw.toByteArray());
        Method main = testClass.getMethod("main", String.class);// invoke method of instance
        main.invoke(testClass.newInstance(), "Tim.Guan");
        Method staticMain = testClass.getMethod("staticMain", String[].class);// invoke static
        staticMain.invoke(null, new Object[]{new String[]{}});
    }

}
