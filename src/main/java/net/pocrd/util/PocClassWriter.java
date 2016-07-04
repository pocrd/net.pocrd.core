package net.pocrd.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

/**
 * Created by rendong on 16/5/24.
 */
public class PocClassWriter extends ClassWriter {
    public PocClassWriter(int var1) {
        super(var1);
    }

    public PocClassWriter(ClassReader var1, int var2) {
        super(var1, var2);
    }

    protected String getCommonSuperClass(String var1, String var2) {
        // use the context class loader
        ClassLoader var3 = Thread.currentThread().getContextClassLoader();

        Class var4;
        Class var5;
        try {
            var4 = Class.forName(var1.replace('/', '.'), false, var3);
            var5 = Class.forName(var2.replace('/', '.'), false, var3);
        } catch (Exception var7) {
            throw new RuntimeException(var7.toString());
        }

        if (var4.isAssignableFrom(var5)) {
            return var1;
        } else if (var5.isAssignableFrom(var4)) {
            return var2;
        } else if (!var4.isInterface() && !var5.isInterface()) {
            do {
                var4 = var4.getSuperclass();
            } while (!var4.isAssignableFrom(var5));

            return var4.getName().replace('.', '/');
        } else {
            return "java/lang/Object";
        }
    }
}
