package net.pocrd.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import net.pocrd.core.PocClassLoader;
import net.pocrd.define.CompileConfig;
import net.pocrd.define.ConstField;
import net.pocrd.define.Serializer;
import net.pocrd.entity.CommonConfig;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.GeneratedMessage;

public class SerializerProvider implements Opcodes {
    private static ConcurrentHashMap<Class<?>, Serializer<?>> cache = new ConcurrentHashMap<Class<?>, Serializer<?>>();

    @SuppressWarnings("unchecked")
    public static <T> Serializer<T> getSerializer(Class<T> clazz) {
        Serializer<T> s = (Serializer<T>)cache.get(clazz);
        if (s == null) {
            synchronized (cache) {
                s = (Serializer<T>)cache.get(clazz);
                if (s == null) {
                    s = build(clazz);
                    cache.put(clazz, s);
                }
            }
        }
        return s;
    }

    @SuppressWarnings("unchecked")
    private static <T> Serializer<T> build(Class<T> clazz) {
        if (clazz.getSuperclass() != GeneratedMessage.class) {
            return null;
        }
        String className = "net.pocrd.autogen.Serializer_" + clazz.getName().replace('.', '_');
        className = className.replace('$', '_');
        String c_name = className.replace('.', '/');
        String c_desc = "L" + c_name + ";";
        String t_className = clazz.getName().replace('.', '/');
        String t_classDesc = Type.getDescriptor(clazz);

        try {
            Method m = clazz.getMethod("getDescriptor");
            Descriptor d = (Descriptor)m.invoke(null);
            List<FieldDescriptor> fds = d.getFields();
            int size = fds.size();
            if (size == 0) return null;

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            MethodVisitor mv;

            cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, c_name, "Ljava/lang/Object;Lnet/pocrd/define/Serializer<" + t_classDesc + ">;",
                    "java/lang/Object", new String[] { Serializer.class.getName().replace('.', '/') });

            if (clazz.getDeclaringClass() != null) {
                BytecodeUtil.createInnerClassVisitor(cw, clazz);
            }

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
                mv = cw.visitMethod(ACC_PUBLIC, "toXml", "(" + t_classDesc + "Ljava/io/OutputStream;Z)V", null, null);
                buildToXml(mv, clazz, fds, c_desc, t_className, t_classDesc);
                mv.visitEnd();
            }

            {
                mv = cw.visitMethod(ACC_PUBLIC, "toJson", "(" + t_classDesc + "Ljava/io/OutputStream;Z)V", null, null);
                buildToJson(mv, clazz, fds, c_desc, t_className, t_classDesc);
                mv.visitEnd();
            }
            {
                mv = cw.visitMethod(ACC_PUBLIC, "toProtobuf", "(Lcom/google/protobuf/GeneratedMessage;Ljava/io/OutputStream;)V", null, null);
                buildToProtobuf(mv);
                mv.visitEnd();
            }
            {
                mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "toJson", "(Ljava/lang/Object;Ljava/io/OutputStream;Z)V", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitTypeInsn(CHECKCAST, t_className);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitVarInsn(ILOAD, 3);
                mv.visitMethodInsn(INVOKEVIRTUAL, c_name, "toJson", "(" + t_classDesc + "Ljava/io/OutputStream;Z)V");
                mv.visitInsn(RETURN);
                mv.visitMaxs(4, 4);
                mv.visitEnd();
            }
            {
                mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "toXml", "(Ljava/lang/Object;Ljava/io/OutputStream;Z)V", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitTypeInsn(CHECKCAST, t_className);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitVarInsn(ILOAD, 3);
                mv.visitMethodInsn(INVOKEVIRTUAL, c_name, "toXml", "(" + t_classDesc + "Ljava/io/OutputStream;Z)V");
                mv.visitInsn(RETURN);
                mv.visitMaxs(4, 4);
                mv.visitEnd();
            }
            cw.visitEnd();

            if (CompileConfig.isDebug) {
                FileOutputStream fos = null;
                try {
                    File folder = new File(CommonConfig.getInstance().autogenPath + File.separator + "Serializer" + File.separator);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    fos = new FileOutputStream(CommonConfig.getInstance().autogenPath + File.separator + "Serializer" + File.separator + className + ".class");
                    fos.write(cw.toByteArray());
                } finally {
                    if (fos != null) {
                        fos.close();
                    }
                }
            }

            return (Serializer<T>)new PocClassLoader(Thread.currentThread().getContextClassLoader()).defineClass(className, cw.toByteArray())
                    .newInstance();
        } catch (Exception e) {
            throw new RuntimeException(c_name, e);
        }
    }

    private static void buildToXml(MethodVisitor mv, Class<?> clazz, List<FieldDescriptor> fds, String classDesc, String t_className,
            String t_classDesc) throws SecurityException, NoSuchMethodException {
        int newLocalIndex = 0;
        @SuppressWarnings("unused")
        final int local_this = newLocalIndex++;
        final int local_instance = newLocalIndex++;
        final int local_out = newLocalIndex++;
        final int local_isRoot = newLocalIndex++;
        final int local_e = newLocalIndex++;
        mv.visitCode();
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitTryCatchBlock(l0, l1, l2, "java/io/IOException");
        Label l_start = new Label();
        mv.visitLabel(l_start);
        mv.visitVarInsn(ALOAD, local_instance);
        mv.visitJumpInsn(IFNONNULL, l0);
        mv.visitInsn(RETURN);
        mv.visitLabel(l0);
        mv.visitVarInsn(ILOAD, local_isRoot);
        Label l4 = new Label();
        mv.visitJumpInsn(IFEQ, l4);
        writeXmlStart(mv, clazz.getSimpleName(), local_out);
        mv.visitLabel(l4);
        for (FieldDescriptor fd : fds) {
            JavaType t = fd.getJavaType();
            String name = fd.getName();
            String getter = "get" + name.substring(0, 1).toUpperCase() + (name.length() > 1 ? name.substring(1) : "");
            String t_sig = null;
            switch (t) {
                case BOOLEAN:
                    if (t_sig == null) t_sig = "Z";
                case INT:
                    if (t_sig == null) t_sig = "I";
                case LONG:
                    if (t_sig == null) t_sig = "J";
                case FLOAT:
                    if (t_sig == null) t_sig = "F";
                case DOUBLE:
                    if (t_sig == null) t_sig = "D";
                    if (fd.isRepeated()) {
                        int local_size = newLocalIndex++;
                        int local_i = newLocalIndex++;
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter + "Count", "()I");
                        mv.visitVarInsn(ISTORE, local_size);
                        mv.visitVarInsn(ILOAD, local_size);
                        Label label_empty = new Label();
                        mv.visitJumpInsn(IFLE, label_empty);
                        writeXmlStart(mv, name + "List", local_out);
                        mv.visitInsn(ICONST_0);
                        mv.visitVarInsn(ISTORE, local_i);
                        Label label_toLoop = new Label();
                        mv.visitJumpInsn(GOTO, label_toLoop);
                        Label label_loop = new Label();
                        mv.visitLabel(label_loop);
                        writeXmlStart(mv, name, local_out);
                        mv.visitVarInsn(ALOAD, local_out);
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitVarInsn(ILOAD, local_i);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter, "(I)" + t_sig);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(" + t_sig + ")Ljava/lang/String;");
                        mv.visitFieldInsn(GETSTATIC, "net/pocrd/define/ConstField", "UTF8", "Ljava/nio/charset/Charset;");
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B");
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "([B)V");
                        writeXmlEnd(mv, name, local_out);
                        mv.visitIincInsn(local_i, 1);
                        mv.visitLabel(label_toLoop);
                        mv.visitVarInsn(ILOAD, local_i);
                        mv.visitVarInsn(ILOAD, local_size);
                        mv.visitJumpInsn(IF_ICMPLT, label_loop);
                        writeXmlEnd(mv, name + "List", local_out);
                        mv.visitLabel(label_empty);
                        newLocalIndex -= 2;
                    } else {
                        writeXmlStart(mv, name, local_out);
                        mv.visitVarInsn(ALOAD, local_out);
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter, "()" + t_sig);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(" + t_sig + ")Ljava/lang/String;");
                        mv.visitFieldInsn(GETSTATIC, "net/pocrd/define/ConstField", "UTF8", "Ljava/nio/charset/Charset;");
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B");
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "([B)V");
                        writeXmlEnd(mv, name, local_out);
                    }
                    break;
                case STRING:
                    if (fd.isRepeated()) {
                        int local_size = newLocalIndex++;
                        int local_i = newLocalIndex++;
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter + "Count", "()I");
                        mv.visitVarInsn(ISTORE, local_size);
                        mv.visitVarInsn(ILOAD, local_size);
                        Label label_empty = new Label();
                        mv.visitJumpInsn(IFLE, label_empty);
                        writeXmlStart(mv, name + "List", local_out);
                        mv.visitInsn(ICONST_0);
                        mv.visitVarInsn(ISTORE, local_i);
                        Label label_toLoop = new Label();
                        mv.visitJumpInsn(GOTO, label_toLoop);
                        Label label_loop = new Label();
                        mv.visitLabel(label_loop);
                        writeCDATAStart(mv, name, local_out);
                        mv.visitVarInsn(ALOAD, local_out);
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitVarInsn(ILOAD, local_i);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter, "(I)Ljava/lang/String;");
                        mv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/SerializerProvider", "writeXmlString",
                                "(Ljava/io/OutputStream;Ljava/lang/String;)V");
                        writeCDATAEnd(mv, name, local_out);
                        mv.visitIincInsn(local_i, 1);
                        mv.visitLabel(label_toLoop);
                        mv.visitVarInsn(ILOAD, local_i);
                        mv.visitVarInsn(ILOAD, local_size);
                        mv.visitJumpInsn(IF_ICMPLT, label_loop);
                        writeXmlEnd(mv, name + "List", local_out);
                        mv.visitLabel(label_empty);
                        newLocalIndex -= 2;
                    } else {
                        int local_obj = newLocalIndex++;
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter, "()Ljava/lang/String;");
                        mv.visitVarInsn(ASTORE, local_obj);
                        mv.visitVarInsn(ALOAD, local_obj);
                        Label label_null = new Label();
                        mv.visitJumpInsn(IFNULL, label_null);
                        writeCDATAStart(mv, name, local_out);
                        mv.visitVarInsn(ALOAD, local_out);
                        mv.visitVarInsn(ALOAD, local_obj);
                        mv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/SerializerProvider", "writeXmlString",
                                "(Ljava/io/OutputStream;Ljava/lang/String;)V");
                        writeCDATAEnd(mv, name, local_out);
                        mv.visitLabel(label_null);
                        newLocalIndex -= 1;
                    }
                    break;
                case MESSAGE:
                    if (fd.isRepeated()) {
                        int local_size = newLocalIndex++;
                        int local_i = newLocalIndex++;
                        int local_serializer = newLocalIndex++;
                        Class<?> type = clazz.getMethod(getter, Integer.TYPE).getReturnType();
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter + "Count", "()I");
                        mv.visitVarInsn(ISTORE, local_size);
                        mv.visitVarInsn(ILOAD, local_size);
                        Label label_empty = new Label();
                        mv.visitJumpInsn(IFLE, label_empty);
                        mv.visitLdcInsn(Type.getType(type));
                        mv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/SerializerProvider", "getSerializer",
                                "(Ljava/lang/Class;)Lnet/pocrd/define/Serializer;");
                        mv.visitVarInsn(ASTORE, local_serializer);
                        writeXmlStart(mv, name + "List", local_out);
                        mv.visitInsn(ICONST_0);
                        mv.visitVarInsn(ISTORE, local_i);
                        Label label_toLoop = new Label();
                        mv.visitJumpInsn(GOTO, label_toLoop);
                        Label label_loop = new Label();
                        mv.visitLabel(label_loop);
                        writeXmlStart(mv, name, local_out);
                        mv.visitVarInsn(ALOAD, local_serializer);
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitVarInsn(ILOAD, local_i);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter, "(I)" + Type.getDescriptor(type));
                        mv.visitVarInsn(ALOAD, local_out);
                        mv.visitInsn(ICONST_0);
                        mv.visitMethodInsn(INVOKEINTERFACE, "net/pocrd/define/Serializer", "toXml", "(Ljava/lang/Object;Ljava/io/OutputStream;Z)V");
                        writeXmlEnd(mv, name, local_out);
                        mv.visitIincInsn(local_i, 1);
                        mv.visitLabel(label_toLoop);
                        mv.visitVarInsn(ILOAD, local_i);
                        mv.visitVarInsn(ILOAD, local_size);
                        mv.visitJumpInsn(IF_ICMPLT, label_loop);
                        writeXmlEnd(mv, name + "List", local_out);
                        mv.visitLabel(label_empty);
                        newLocalIndex -= 3;
                    } else {
                        int local_obj = newLocalIndex++;
                        Class<?> type = clazz.getMethod(getter).getReturnType();
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter, "()" + Type.getDescriptor(type));
                        mv.visitVarInsn(ASTORE, local_obj);
                        mv.visitVarInsn(ALOAD, local_obj);
                        mv.visitMethodInsn(INVOKESTATIC, type.getName().replace('.', '/'), "getDefaultInstance", "()" + Type.getDescriptor(type));
                        Label label_default = new Label();
                        mv.visitJumpInsn(IF_ACMPEQ, label_default);
                        writeXmlStart(mv, name, local_out);
                        mv.visitLdcInsn(Type.getType(type));
                        mv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/SerializerProvider", "getSerializer",
                                "(Ljava/lang/Class;)Lnet/pocrd/define/Serializer;");
                        mv.visitVarInsn(ALOAD, local_obj);
                        mv.visitVarInsn(ALOAD, local_out);
                        mv.visitInsn(ICONST_0);
                        mv.visitMethodInsn(INVOKEINTERFACE, "net/pocrd/define/Serializer", "toXml", "(Ljava/lang/Object;Ljava/io/OutputStream;Z)V");
                        writeXmlEnd(mv, name, local_out);
                        mv.visitLabel(label_default);
                        newLocalIndex -= 1;
                    }
                    break;
                case BYTE_STRING:
                    break;
                case ENUM:
                    break;
                default:
                    break;
            }
        }

        mv.visitVarInsn(ILOAD, local_isRoot);
        Label label_end = new Label();
        mv.visitJumpInsn(IFEQ, label_end);
        writeXmlEnd(mv, clazz.getSimpleName(), local_out);
        mv.visitLabel(l1);
        mv.visitJumpInsn(GOTO, label_end);
        mv.visitLabel(l2);
        mv.visitVarInsn(ASTORE, local_e);
        Label l23 = new Label();
        mv.visitLabel(l23);
        mv.visitTypeInsn(NEW, "net/pocrd/entity/ReturnCodeException");
        mv.visitInsn(DUP);
        mv.visitFieldInsn(GETSTATIC, "net/pocrd/entity/ReturnCode", "UNKNOWN_ERROR", "Lnet/pocrd/entity/ReturnCode;");
        mv.visitVarInsn(ALOAD, local_e);
        mv.visitMethodInsn(INVOKESPECIAL, "net/pocrd/entity/ReturnCodeException", "<init>", "(Lnet/pocrd/entity/ReturnCode;Ljava/lang/Exception;)V");
        mv.visitInsn(ATHROW);
        mv.visitLabel(label_end);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
    }

    private static void buildToJson(MethodVisitor mv, Class<?> clazz, List<FieldDescriptor> fds, String classDesc, String t_className,
            String t_classDesc) throws SecurityException, NoSuchMethodException {
        int newLocalIndex = 0;
        @SuppressWarnings("unused")
        final int local_this = newLocalIndex++;
        final int local_instance = newLocalIndex++;
        final int local_out = newLocalIndex++;
        final int local_isRoot = newLocalIndex++;
        final int local_e = newLocalIndex++;
        mv.visitCode();
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitTryCatchBlock(l0, l1, l2, "java/io/IOException");
        Label l_start = new Label();
        mv.visitLabel(l_start);
        mv.visitVarInsn(ALOAD, local_instance);
        mv.visitJumpInsn(IFNONNULL, l0);
        mv.visitInsn(RETURN);
        mv.visitLabel(l0);
        mv.visitVarInsn(ILOAD, local_isRoot);
        Label l4 = new Label();
        mv.visitJumpInsn(IFEQ, l4);
        writeByteArray(mv, "{".getBytes(ConstField.UTF8), local_out);
        mv.visitLabel(l4);
        int fdsl = fds.size();
        FieldDescriptor[] fda = new FieldDescriptor[fdsl];
        {
            int start = 0;
            int end = fdsl - 1;
            for (FieldDescriptor fd : fds) {
                if (fd.isRepeated()) {
                    fda[start++] = fd;
                } else {
                    fda[end--] = fd;
                }
            }
        }
        for (int n = 0; n < fdsl; n++) {
            FieldDescriptor fd = fda[n];
            JavaType t = fd.getJavaType();
            String name = fd.getName();
            String getter = "get" + name.substring(0, 1).toUpperCase() + (name.length() > 1 ? name.substring(1) : "");
            String t_sig = null;
            switch (t) {
                case BOOLEAN:
                    if (t_sig == null) t_sig = "Z";
                case INT:
                    if (t_sig == null) t_sig = "I";
                case LONG:
                    if (t_sig == null) t_sig = "J";
                case FLOAT:
                    if (t_sig == null) t_sig = "F";
                case DOUBLE:
                    if (t_sig == null) t_sig = "D";
                    if (fd.isRepeated()) {
                        int local_size = newLocalIndex++;
                        int local_i = newLocalIndex++;
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter + "Count", "()I");
                        mv.visitVarInsn(ISTORE, local_size);
                        mv.visitVarInsn(ILOAD, local_size);
                        Label label_empty = new Label();
                        mv.visitJumpInsn(IFLE, label_empty);
                        mv.visitIincInsn(local_size, -1);
                        writeByteArray(mv, ("\"" + name + "List\":[").getBytes(ConstField.UTF8), local_out);
                        mv.visitInsn(ICONST_0);
                        mv.visitVarInsn(ISTORE, local_i);
                        Label label_toLoop = new Label();
                        mv.visitJumpInsn(GOTO, label_toLoop);
                        Label label_loop = new Label();
                        // Repeate size-1 with ','
                        mv.visitLabel(label_loop);
                        mv.visitVarInsn(ALOAD, local_out);
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitVarInsn(ILOAD, local_i);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter, "(I)" + t_sig);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(" + t_sig + ")Ljava/lang/String;");
                        mv.visitFieldInsn(GETSTATIC, "net/pocrd/define/ConstField", "UTF8", "Ljava/nio/charset/Charset;");
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B");
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "([B)V");
                        writeByteArray(mv, ",".getBytes(), local_out);
                        mv.visitIincInsn(local_i, 1);
                        mv.visitLabel(label_toLoop);
                        mv.visitVarInsn(ILOAD, local_i);
                        mv.visitVarInsn(ILOAD, local_size);
                        mv.visitJumpInsn(IF_ICMPLT, label_loop);
                        mv.visitVarInsn(ALOAD, local_out);
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitVarInsn(ILOAD, local_i);
                        // The last without ','
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter, "(I)" + t_sig);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(" + t_sig + ")Ljava/lang/String;");
                        mv.visitFieldInsn(GETSTATIC, "net/pocrd/define/ConstField", "UTF8", "Ljava/nio/charset/Charset;");
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B");
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "([B)V");
                        if (n + 1 == fdsl) {
                            writeByteArray(mv, ("]").getBytes(ConstField.UTF8), local_out);
                        } else {
                            writeByteArray(mv, ("],").getBytes(ConstField.UTF8), local_out);
                        }
                        mv.visitLabel(label_empty);
                        newLocalIndex -= 2;
                    } else {
                        writeByteArray(mv, ("\"" + name + "\":").getBytes(ConstField.UTF8), local_out);
                        mv.visitVarInsn(ALOAD, local_out);
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter, "()" + t_sig);
                        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(" + t_sig + ")Ljava/lang/String;");
                        mv.visitFieldInsn(GETSTATIC, "net/pocrd/define/ConstField", "UTF8", "Ljava/nio/charset/Charset;");
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B");
                        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "([B)V");
                        if (n + 1 == fdsl) {
                            // Do nothing.
                        } else {
                            writeByteArray(mv, (",").getBytes(ConstField.UTF8), local_out);
                        }
                    }
                    break;
                case STRING:
                    if (fd.isRepeated()) {
                        int local_size = newLocalIndex++;
                        int local_i = newLocalIndex++;
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter + "Count", "()I");
                        mv.visitVarInsn(ISTORE, local_size);
                        mv.visitVarInsn(ILOAD, local_size);
                        Label label_empty = new Label();
                        mv.visitJumpInsn(IFLE, label_empty);
                        mv.visitIincInsn(local_size, -1);
                        writeByteArray(mv, ("\"" + name + "List\":[").getBytes(ConstField.UTF8), local_out);
                        mv.visitInsn(ICONST_0);
                        mv.visitVarInsn(ISTORE, local_i);
                        Label label_toLoop = new Label();
                        mv.visitJumpInsn(GOTO, label_toLoop);
                        Label label_loop = new Label();
                        // Repeate size-1 with ','
                        mv.visitLabel(label_loop);
                        writeByteArray(mv, ("\"").getBytes(ConstField.UTF8), local_out);
                        mv.visitVarInsn(ALOAD, local_out);
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitVarInsn(ILOAD, local_i);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter, "(I)Ljava/lang/String;");
                        mv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/SerializerProvider", "writeJsonString",
                                "(Ljava/io/OutputStream;Ljava/lang/String;)V");
                        writeByteArray(mv, ("\",").getBytes(ConstField.UTF8), local_out);
                        mv.visitIincInsn(local_i, 1);
                        mv.visitLabel(label_toLoop);
                        mv.visitVarInsn(ILOAD, local_i);
                        mv.visitVarInsn(ILOAD, local_size);
                        mv.visitJumpInsn(IF_ICMPLT, label_loop);
                        // The last without ','
                        writeByteArray(mv, ("\"").getBytes(ConstField.UTF8), local_out);
                        mv.visitVarInsn(ALOAD, local_out);
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitVarInsn(ILOAD, local_i);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter, "(I)Ljava/lang/String;");
                        mv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/SerializerProvider", "writeJsonString",
                                "(Ljava/io/OutputStream;Ljava/lang/String;)V");
                        writeByteArray(mv, ("\"").getBytes(ConstField.UTF8), local_out);
                        if (n + 1 == fdsl) {
                            writeByteArray(mv, ("]").getBytes(ConstField.UTF8), local_out);
                        } else {
                            writeByteArray(mv, ("],").getBytes(ConstField.UTF8), local_out);
                        }
                        mv.visitLabel(label_empty);
                        newLocalIndex -= 2;
                    } else {
                        int local_obj = newLocalIndex++;
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter, "()Ljava/lang/String;");
                        mv.visitVarInsn(ASTORE, local_obj);
                        mv.visitVarInsn(ALOAD, local_obj);
                        Label label_null = new Label();
                        mv.visitJumpInsn(IFNULL, label_null);
                        writeByteArray(mv, ("\"" + name + "\":\"").getBytes(ConstField.UTF8), local_out);
                        mv.visitVarInsn(ALOAD, local_out);
                        mv.visitVarInsn(ALOAD, local_obj);
                        mv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/SerializerProvider", "writeJsonString",
                                "(Ljava/io/OutputStream;Ljava/lang/String;)V");
                        if (n + 1 == fdsl) {
                            writeByteArray(mv, ("\"").getBytes(ConstField.UTF8), local_out);
                        } else {
                            writeByteArray(mv, ("\",").getBytes(ConstField.UTF8), local_out);
                        }
                        mv.visitLabel(label_null);
                        newLocalIndex -= 1;
                    }
                    break;
                case MESSAGE:
                    if (fd.isRepeated()) {
                        int local_size = newLocalIndex++;
                        int local_i = newLocalIndex++;
                        int local_serializer = newLocalIndex++;
                        Class<?> type = clazz.getMethod(getter, Integer.TYPE).getReturnType();
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter + "Count", "()I");
                        mv.visitVarInsn(ISTORE, local_size);
                        mv.visitVarInsn(ILOAD, local_size);
                        Label label_empty = new Label();
                        mv.visitJumpInsn(IFLE, label_empty);
                        mv.visitIincInsn(local_size, -1);
                        mv.visitLdcInsn(Type.getType(type));
                        mv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/SerializerProvider", "getSerializer",
                                "(Ljava/lang/Class;)Lnet/pocrd/define/Serializer;");
                        mv.visitVarInsn(ASTORE, local_serializer);
                        writeByteArray(mv, ("\"" + name + "List\":[").getBytes(ConstField.UTF8), local_out);
                        mv.visitInsn(ICONST_0);
                        mv.visitVarInsn(ISTORE, local_i);
                        Label label_toLoop = new Label();
                        mv.visitJumpInsn(GOTO, label_toLoop);
                        Label label_loop = new Label();
                        // Repeate size-1 with ','
                        mv.visitLabel(label_loop);
                        writeByteArray(mv, ("{").getBytes(ConstField.UTF8), local_out);
                        mv.visitVarInsn(ALOAD, local_serializer);
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitVarInsn(ILOAD, local_i);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter, "(I)" + Type.getDescriptor(type));
                        mv.visitVarInsn(ALOAD, local_out);
                        mv.visitInsn(ICONST_0);
                        mv.visitMethodInsn(INVOKEINTERFACE, "net/pocrd/define/Serializer", "toJson", "(Ljava/lang/Object;Ljava/io/OutputStream;Z)V");
                        writeByteArray(mv, ("},").getBytes(ConstField.UTF8), local_out);
                        mv.visitIincInsn(local_i, 1);
                        mv.visitLabel(label_toLoop);
                        mv.visitVarInsn(ILOAD, local_i);
                        mv.visitVarInsn(ILOAD, local_size);
                        mv.visitJumpInsn(IF_ICMPLT, label_loop);
                        // The last without ','
                        writeByteArray(mv, ("{").getBytes(ConstField.UTF8), local_out);
                        mv.visitVarInsn(ALOAD, local_serializer);
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitVarInsn(ILOAD, local_i);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter, "(I)" + Type.getDescriptor(type));
                        mv.visitVarInsn(ALOAD, local_out);
                        mv.visitInsn(ICONST_0);
                        mv.visitMethodInsn(INVOKEINTERFACE, "net/pocrd/define/Serializer", "toJson", "(Ljava/lang/Object;Ljava/io/OutputStream;Z)V");
                        writeByteArray(mv, ("}").getBytes(ConstField.UTF8), local_out);
                        if (n + 1 == fdsl) {
                            writeByteArray(mv, ("]").getBytes(ConstField.UTF8), local_out);
                        } else {
                            writeByteArray(mv, ("],").getBytes(ConstField.UTF8), local_out);
                        }
                        mv.visitLabel(label_empty);
                        newLocalIndex -= 3;
                    } else {
                        int local_obj = newLocalIndex++;
                        Class<?> type = clazz.getMethod(getter).getReturnType();
                        mv.visitVarInsn(ALOAD, local_instance);
                        mv.visitMethodInsn(INVOKEVIRTUAL, t_className, getter, "()" + Type.getDescriptor(type));
                        mv.visitVarInsn(ASTORE, local_obj);
                        mv.visitVarInsn(ALOAD, local_obj);
                        mv.visitMethodInsn(INVOKESTATIC, type.getName().replace('.', '/'), "getDefaultInstance", "()" + Type.getDescriptor(type));
                        writeByteArray(mv, ("\"" + name + "\":{").getBytes(ConstField.UTF8), local_out);
                        Label label_skip = new Label();
                        mv.visitJumpInsn(IF_ACMPEQ, label_skip);
                        mv.visitLdcInsn(Type.getType(type));
                        mv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/SerializerProvider", "getSerializer",
                                "(Ljava/lang/Class;)Lnet/pocrd/define/Serializer;");
                        mv.visitVarInsn(ALOAD, local_obj);
                        mv.visitVarInsn(ALOAD, local_out);
                        mv.visitInsn(ICONST_0);
                        mv.visitMethodInsn(INVOKEINTERFACE, "net/pocrd/define/Serializer", "toJson", "(Ljava/lang/Object;Ljava/io/OutputStream;Z)V");
                        mv.visitLabel(label_skip);
                        if (n + 1 == fdsl) {
                            writeByteArray(mv, ("}").getBytes(ConstField.UTF8), local_out);
                        } else {
                            writeByteArray(mv, ("},").getBytes(ConstField.UTF8), local_out);
                        }
                        newLocalIndex -= 1;
                    }
                    break;
                case BYTE_STRING:
                    break;
                case ENUM:
                    break;
                default:
                    break;
            }
        }

        mv.visitVarInsn(ILOAD, local_isRoot);
        Label label_end = new Label();
        mv.visitJumpInsn(IFEQ, label_end);
        writeByteArray(mv, "}".getBytes(ConstField.UTF8), local_out);
        mv.visitLabel(l1);
        mv.visitJumpInsn(GOTO, label_end);
        mv.visitLabel(l2);
        mv.visitVarInsn(ASTORE, local_e);
        Label l23 = new Label();
        mv.visitLabel(l23);
        mv.visitTypeInsn(NEW, "net/pocrd/entity/ReturnCodeException");
        mv.visitInsn(DUP);
        mv.visitFieldInsn(GETSTATIC, "net/pocrd/entity/ReturnCode", "UNKNOWN_ERROR", "Lnet/pocrd/entity/ReturnCode;");
        mv.visitVarInsn(ALOAD, local_e);
        mv.visitMethodInsn(INVOKESPECIAL, "net/pocrd/entity/ReturnCodeException", "<init>", "(Lnet/pocrd/entity/ReturnCode;Ljava/lang/Exception;)V");
        mv.visitInsn(ATHROW);
        mv.visitLabel(label_end);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
    }

    private static void buildToProtobuf(MethodVisitor mv) {
        @SuppressWarnings("unused")
        int local_this = 0;
        int local_instance = 1;
        int local_out = 2;
        int local_e = 3;
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        mv.visitCode();
        mv.visitTryCatchBlock(l0, l1, l2, "java/io/IOException");
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, local_instance);
        mv.visitVarInsn(ALOAD, local_out);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/google/protobuf/GeneratedMessage", "writeTo", "(Ljava/io/OutputStream;)V");
        mv.visitLabel(l1);
        Label l3 = new Label();
        mv.visitJumpInsn(GOTO, l3);
        mv.visitLabel(l2);
        mv.visitVarInsn(ASTORE, local_e);
        mv.visitTypeInsn(NEW, "net/pocrd/entity/ReturnCodeException");
        mv.visitInsn(DUP);
        mv.visitFieldInsn(GETSTATIC, "net/pocrd/entity/ReturnCode", "SERIALIZE_FAILED", "Lnet/pocrd/entity/ReturnCode;");
        mv.visitVarInsn(ALOAD, local_e);
        mv.visitMethodInsn(INVOKESPECIAL, "net/pocrd/entity/ReturnCodeException", "<init>", "(Lnet/pocrd/entity/ReturnCode;Ljava/lang/Exception;)V");
        mv.visitInsn(ATHROW);
        mv.visitLabel(l3);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
    }

    private static void writeByteArray(MethodVisitor mv, byte[] bs, int local_out) {
        for (int i = 0; i < bs.length; i++) {
            mv.visitVarInsn(ALOAD, local_out);
            BytecodeUtil.loadConst(mv, bs[i]);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "(I)V");
        }
    }

    private static void writeXmlStart(MethodVisitor mv, String name, int local_out) {
        // mv.visitVarInsn(ALOAD, local_out);
        // mv.visitLdcInsn("<" + name + ">");
        // mv.visitFieldInsn(GETSTATIC, "net/pocrd/define/ConstField", "UTF8", "Ljava/nio/charset/Charset;");
        // mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B");
        // mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "([B)V");
        writeByteArray(mv, ("<" + name + ">").getBytes(ConstField.UTF8), local_out);
    }

    private static void writeXmlEnd(MethodVisitor mv, String name, int local_out) {
        // mv.visitVarInsn(ALOAD, local_out);
        // mv.visitLdcInsn("</" + name + ">");
        // mv.visitFieldInsn(GETSTATIC, "net/pocrd/define/ConstField", "UTF8", "Ljava/nio/charset/Charset;");
        // mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B");
        // mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "([B)V");
        writeByteArray(mv, ("</" + name + ">").getBytes(ConstField.UTF8), local_out);
    }

    private static void writeCDATAStart(MethodVisitor mv, String name, int local_out) {
        mv.visitVarInsn(ALOAD, local_out);
        mv.visitLdcInsn("<" + name + "><![CDATA[");
        mv.visitFieldInsn(GETSTATIC, "net/pocrd/define/ConstField", "UTF8", "Ljava/nio/charset/Charset;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "([B)V");
    }

    private static void writeCDATAEnd(MethodVisitor mv, String name, int local_out) {
        mv.visitVarInsn(ALOAD, local_out);
        mv.visitLdcInsn("]]></" + name + ">");
        mv.visitFieldInsn(GETSTATIC, "net/pocrd/define/ConstField", "UTF8", "Ljava/nio/charset/Charset;");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "([B)V");
    }

    /**
     * 对xml输出内容中的]]>进行处理
     * 
     * @param out
     * @param str
     * @throws IOException
     */
    public static void writeXmlString(OutputStream out, String str) throws IOException {
        out.write(str.replace("]]>", "]]]]><![CDATA[>").getBytes(ConstField.UTF8));
    }

    /**
     * 对json输出内容进行转义
     * 
     * @param out
     * @param str
     * @throws IOException
     */
    public static void writeJsonString(OutputStream out, String str) throws IOException {
        out.write(str.replace("\\", "\\\\").replace("\"", "\\\"").getBytes(ConstField.UTF8));
    }
}
