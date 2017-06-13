package net.pocrd.util;

import net.pocrd.core.PocClassLoader;
import net.pocrd.define.ConstField;
import net.pocrd.define.Serializer;
import net.pocrd.entity.CommonConfig;
import net.pocrd.entity.CompileConfig;
import org.objectweb.asm.*;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * POJOSerializerProvider只提供了静态的POJOSerializer方式，即不会在运行期根据对象的类型去改变其序列化行为(考虑到ApoDoc的生成所以不支持动态方式)
 * 动态的序列化行为解决方案是自定义Serializer
 */
public class POJOSerializerProvider implements Opcodes {
    private final static ConcurrentHashMap<Class<?>, Serializer<?>> cache = new ConcurrentHashMap<Class<?>, Serializer<?>>();

    /**
     * 返回实体类的序列化类对象
     */
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

    private static <T> Serializer<T> build(Class<T> clazz) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        LinkedList<String> list = new LinkedList<String>();
        String className = "net.pocrd.autogen.Serializer_" + clazz.getName().replace('.', '_');
        className = className.replace('$', '_');
        String c_name = className.replace('.', '/');
        String c_desc = "L" + c_name + ";";
        String t_className = clazz.getName().replace('.', '/');
        String t_classDesc = Type.getDescriptor(clazz);

        try {
            Field[] dfs = clazz.getDeclaredFields();
            List<Field> fds = new ArrayList<Field>(dfs.length);
            for (Field f : dfs) {
                int modifier = f.getModifiers();
                if (!Modifier.isPublic(modifier) || Modifier.isStatic(modifier)) {
                    continue;
                }
                fds.add(f);
            }

            ClassWriter cw = new PocClassWriter(ClassWriter.COMPUTE_FRAMES);
            MethodVisitor mv;

            cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, c_name, "Ljava/lang/Object;Lnet/pocrd/define/Serializer<" + t_classDesc + ">;", "java/lang/Object",
                    new String[] { Serializer.class.getName().replace('.', '/') });

            if (clazz.getDeclaringClass() != null) {
                BytecodeUtil.createInnerClassVisitor(cw, clazz);
            }
            {
                FieldVisitor fv = cw.visitField(ACC_PRIVATE + ACC_STATIC, "bs", "[[B", null, null);
                fv.visitEnd();
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
                PocMethodVisitor pmv = new PocMethodVisitor(cw, ACC_PUBLIC, "toXml", "(" + t_classDesc + "Ljava/io/OutputStream;Z)V", null, null);
                buildToXml(c_name, pmv, clazz, fds, c_desc, t_className, t_classDesc, map, list);
                mv.visitEnd();
            }
            //pojo fastjson
            {
                PocMethodVisitor pmv = new PocMethodVisitor(cw, ACC_PUBLIC, "toJson", "(" + t_classDesc + "Ljava/io/OutputStream;Z)V", null, null);
                buildToJsonWithFastJson(c_name, pmv, clazz, fds, c_desc, t_className, t_classDesc, map, list);
                mv.visitEnd();
            }
            //protobuf
            //            {
            //                mv = cw.visitMethod(ACC_PUBLIC, "toProtobuf", "(Lcom/google/protobuf/GeneratedMessage;Ljava/io/OutputStream;)V", null, null);
            //                mv.visitCode();
            //                mv.visitTypeInsn(NEW, "java/lang/UnsupportedOperationException");
            //                mv.visitInsn(DUP);
            //                mv.visitLdcInsn("\u8be5\u7c7b\u578b\u4e0d\u652f\u6301protobuf\u65b9\u5f0f\u7684\u5e8f\u5217\u5316.");
            //                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/UnsupportedOperationException", "<init>", "(Ljava/lang/String;)V");
            //                mv.visitInsn(ATHROW);
            //                mv.visitMaxs(3, 3);
            //                mv.visitEnd();
            //            }
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
            {
                PocMethodVisitor pmv = new PocMethodVisitor(cw, ACC_STATIC, "<clinit>", "()V", null, null);
                pmv.visitCode();
                pmv.visitIntInsn(BIPUSH, list.size());
                pmv.visitTypeInsn(ANEWARRAY, "[B");
                pmv.visitFieldInsn(PUTSTATIC, c_name, "bs", "[[B");
                for (int i = 0; i < list.size(); i++) {
                    pmv.visitFieldInsn(GETSTATIC, c_name, "bs", "[[B");
                    pmv.loadConst(i);
                    pmv.visitLdcInsn(list.get(i));
                    pmv.visitFieldInsn(GETSTATIC, "net/pocrd/define/ConstField", "UTF8", "Ljava/nio/charset/Charset;");
                    pmv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B");
                    pmv.visitInsn(AASTORE);
                }
                pmv.visitInsn(RETURN);
                pmv.visitMaxs(4, 0);
                pmv.visitEnd();
            }
            cw.visitEnd();

            if (CompileConfig.isDebug) {
                FileOutputStream fos = null;
                try {
                    File folder = new File(CommonConfig.getInstance().getAutogenPath() + File.separator + "POJOSerializer" + File.separator);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    fos = new FileOutputStream(
                            CommonConfig.getInstance().getAutogenPath() + File.separator + "POJOSerializer" + File.separator + className + ".class");
                    fos.write(cw.toByteArray());
                } finally {
                    if (fos != null) {
                        fos.close();
                    }
                }
            }

            return (Serializer<T>)new PocClassLoader(Thread.currentThread().getContextClassLoader()).defineClass(className,
                    cw.toByteArray()).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(c_name, e);
        }
    }

    //TODO refactor,支持动态类型的风险是无法在编译期获取到接口信息,暂不打算支持。未来会支持对象数组
    private static void buildToXml(String cn, PocMethodVisitor pmv, Class<?> clazz, List<Field> fds, String classDesc, String t_className,
            String t_classDesc, HashMap<String, Integer> map,
            LinkedList<String> list) throws SecurityException, NoSuchMethodException {
        pmv.visitCode();
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        pmv.visitTryCatchBlock(l0, l1, l2, "java/io/IOException");
        Label l_start = new Label();
        pmv.visitLabel(l_start);
        pmv.loadArg(1); // load instance
        pmv.visitJumpInsn(IFNONNULL, l0);
        pmv.visitInsn(RETURN);
        pmv.visitLabel(l0);
        pmv.loadArg(3); // load isRoot
        Label l4 = new Label();
        pmv.visitJumpInsn(IFEQ, l4);
        writeXmlStart(cn, pmv, clazz.getSimpleName(), 2, map, list); // load out
        pmv.visitLabel(l4);
        for (Field fd : fds) {
            Class<?> t = fd.getType();
            boolean isCollection = false;
            boolean isArray = false;
            if (Collection.class.isAssignableFrom(t)) {
                java.lang.reflect.Type genericType;
                try {
                    //TODO 如果要支持泛型的动态序列化，t应该动态获取obj.getClass();
                    genericType = ((ParameterizedTypeImpl)fd.getGenericType()).getActualTypeArguments()[0];//必须明确的指定泛型为何类型
                } catch (Throwable throwable) {
                    throw new RuntimeException("can not get generic type of list in " + clazz.getName(), throwable);
                }
                try {
                    t = Class.forName(((Class)genericType).getName(), true, Thread.currentThread().getContextClassLoader());
                } catch (Exception e) {
                    throw new RuntimeException("generic type unsupported:" + genericType + " in " + clazz.getName(), e);
                }
                isCollection = true;
            } else if (t.isArray()) {
                if (t == boolean[].class) {
                    t = boolean.class;
                } else if (t == byte[].class) {
                    t = byte.class;
                } else if (t == short[].class) {
                    t = short.class;
                } else if (t == char[].class) {
                    t = char.class;
                } else if (t == int[].class) {
                    t = int.class;
                } else if (t == long[].class) {
                    t = long.class;
                } else if (t == float[].class) {
                    t = float.class;
                } else if (t == double[].class) {
                    t = double.class;
                } else {
                    //                    t = t.getComponentType();//TODO 对象数组的支持
                    throw new RuntimeException("array type unsupported:" + t.getName() + " in " + clazz.getName());
                }
                isArray = true;
            }
            String name = fd.getName();
            String t_sig = null;
            if (t == boolean.class || t == byte.class || t == short.class || t == char.class || t == int.class || t == long.class || t == float.class
                    || t == double.class) {
                if (t == boolean.class) {
                    t_sig = "Z";
                } else if (t == byte.class) {
                    t_sig = "B";
                } else if (t == char.class) {
                    t_sig = "C";
                } else if (t == short.class) {
                    t_sig = "S";
                } else if (t == int.class) {
                    t_sig = "I";
                } else if (t == long.class) {
                    t_sig = "J";
                } else if (t == float.class) {
                    t_sig = "F";
                } else if (t == double.class) {
                    t_sig = "D";
                }
                if (isArray) {
                    pmv.loadArg(1); // load instance
                    pmv.visitFieldInsn(GETFIELD, t_className, name, "[" + t_sig);
                    Label label_empty = new Label();
                    pmv.visitJumpInsn(IFNULL, label_empty);
                    writeXmlStart(cn, pmv, name, 2, map, list); // load out
                    pmv.loadArg(1); // load instance
                    pmv.visitFieldInsn(GETFIELD, t_className, name, "[" + t_sig);
                    pmv.declareRefLocal("array");
                    pmv.setLocal("array");
                    pmv.loadLocal("array");
                    pmv.visitInsn(ARRAYLENGTH);
                    pmv.declareLocal("size", int.class);
                    pmv.setLocal("size");
                    pmv.loadConst(0);
                    pmv.declareLocal("i", int.class);
                    pmv.setLocal("i");
                    Label label_loop = new Label();
                    pmv.visitLabel(label_loop);
                    pmv.loadLocal("i");
                    pmv.loadLocal("size");
                    Label label_finish = new Label();
                    pmv.visitJumpInsn(IF_ICMPGE, label_finish);
                    writeXmlStart(cn, pmv, name.endsWith("List") ? name.substring(0, name.length() - 4) : "item", 2, map, list);
                    pmv.loadLocal("array");
                    pmv.loadLocal("i");
                    if (t == boolean.class) {
                        pmv.visitInsn(BALOAD);
                    } else if (t == byte.class) {
                        pmv.visitInsn(BALOAD);
                    } else if (t == short.class) {
                        pmv.visitInsn(SALOAD);
                    } else if (t == char.class) {
                        pmv.visitInsn(CALOAD);
                    } else if (t == int.class) {
                        pmv.visitInsn(IALOAD);
                    } else if (t == long.class) {
                        pmv.visitInsn(LALOAD);
                    } else if (t == float.class) {
                        pmv.visitInsn(FALOAD);
                    } else if (t == double.class) {
                        pmv.visitInsn(DALOAD);
                    }
                    pmv.declareLocal("item", t);
                    pmv.setLocal("item");
                    pmv.loadArg(2); // load out
                    pmv.loadLocal("item");
                    //隐式转换
                    if (t == byte.class) {
                        pmv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;");
                    } else if (t == char.class) {
                        pmv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;");
                    } else if (t == short.class) {
                        pmv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;");
                    } else {
                        pmv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(" + t_sig + ")Ljava/lang/String;");
                    }
                    pmv.visitFieldInsn(GETSTATIC, "net/pocrd/define/ConstField", "UTF8", "Ljava/nio/charset/Charset;");
                    pmv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B");
                    pmv.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "([B)V");
                    writeXmlEnd(cn, pmv, name.endsWith("List") ? name.substring(0, name.length() - 4) : "item", 2, map, list);
                    pmv.incrementLocal("i", 1);
                    pmv.visitJumpInsn(GOTO, label_loop);
                    pmv.visitLabel(label_finish);
                    writeXmlEnd(cn, pmv, name, 2, map, list);
                    pmv.visitLabel(label_empty);
                    pmv.deleteLocal("array");
                    pmv.deleteLocal("size");
                    pmv.deleteLocal("i");
                } else {
                    writeXmlStart(cn, pmv, name, 2, map, list);
                    pmv.loadArg(2);
                    pmv.loadArg(1);
                    pmv.visitFieldInsn(GETFIELD, t_className, name, t_sig);
                    //隐式转换
                    if (t == byte.class) {
                        pmv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;");
                    } else if (t == char.class) {
                        pmv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;");
                    } else if (t == short.class) {
                        pmv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(I)Ljava/lang/String;");
                    } else {
                        pmv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(" + t_sig + ")Ljava/lang/String;");
                    }
                    pmv.visitFieldInsn(GETSTATIC, "net/pocrd/define/ConstField", "UTF8", "Ljava/nio/charset/Charset;");
                    pmv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B");
                    pmv.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "([B)V");
                    writeXmlEnd(cn, pmv, name, 2, map, list);
                }
            } else if (t == String.class || t.isEnum()) {
                if (isCollection) {
                    pmv.loadArg(1); // load instance
                    pmv.visitFieldInsn(GETFIELD, t_className, name, Type.getDescriptor(fd.getType()));//attention:t是actually generic types
                    Label label_empty = new Label();
                    pmv.visitJumpInsn(IFNULL, label_empty);
                    writeXmlStart(cn, pmv, name, 2, map, list); // load out
                    pmv.loadArg(1); // load instance
                    pmv.visitFieldInsn(GETFIELD, t_className, name, Type.getDescriptor(fd.getType()));//attention:t是actually generic types
                    pmv.declareLocal("iter", Iterator.class);
                    pmv.visitMethodInsn(INVOKEINTERFACE, "java/util/Collection", "iterator", "()Ljava/util/Iterator;");
                    pmv.setLocal("iter");
                    Label label_loop = new Label();
                    pmv.visitLabel(label_loop);
                    pmv.loadLocal("iter");
                    pmv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
                    Label label_finish = new Label();
                    pmv.visitJumpInsn(IFEQ, label_finish);
                    writeCDATAStart(cn, pmv, name.endsWith("List") ? name.substring(0, name.length() - 4) : "item", 2, map, list);
                    pmv.loadArg(2); // load out
                    pmv.loadLocal("iter");
                    pmv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
                    pmv.doCast(t);
                    if (t.isEnum()) {
                        pmv.visitMethodInsn(INVOKEVIRTUAL, t.getName().replace('.', '/'), "name", "()Ljava/lang/String;");
                    }
                    pmv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/POJOSerializerProvider", "writeXmlString",
                            "(Ljava/io/OutputStream;Ljava/lang/String;)V");
                    writeCDATAEnd(cn, pmv, name.endsWith("List") ? name.substring(0, name.length() - 4) : "item", 2, map, list);
                    pmv.visitJumpInsn(GOTO, label_loop);
                    pmv.visitLabel(label_finish);
                    writeXmlEnd(cn, pmv, name, 2, map, list);
                    pmv.visitLabel(label_empty);
                    pmv.deleteLocal("iter");
                } else if (isArray) {//trick,其实没有支持String[]而是通过StringArrayResp的convert来规避了这个支持
                    //TODO 支持String/enum数组的序列化
                    //attention:t是actually generic types
                    throw new RuntimeException("unsupport array type,type:" + t.getName());
                } else {
                    pmv.declareLocal("obj", Object.class);
                    pmv.loadArg(1);
                    if (t.isEnum()) {
                        pmv.visitFieldInsn(GETFIELD, t_className, name, Type.getDescriptor(t));
                    } else {
                        pmv.visitFieldInsn(GETFIELD, t_className, name, "Ljava/lang/String;");
                    }
                    pmv.setLocal("obj");
                    pmv.loadLocal("obj");
                    Label label_null = new Label();
                    pmv.visitJumpInsn(IFNULL, label_null);
                    writeCDATAStart(cn, pmv, name, 2, map, list);
                    pmv.loadArg(2);
                    pmv.loadLocal("obj");
                    if (t.isEnum()) {
                        pmv.visitMethodInsn(INVOKEVIRTUAL, t.getName().replace('.', '/'), "name", "()Ljava/lang/String;");
                    }
                    pmv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/POJOSerializerProvider", "writeXmlString",
                            "(Ljava/io/OutputStream;Ljava/lang/String;)V");
                    writeCDATAEnd(cn, pmv, name, 2, map, list);
                    pmv.visitLabel(label_null);
                    pmv.deleteLocal("obj");
                }
            } else {//非String/enum以及基础类型的序列化
                if (t.getName().startsWith("java.lang.")) {
                    throw new RuntimeException("unsupport complex type,type:" + t.getName());
                }
                if (isCollection) {
                    pmv.loadArg(1); // load instance
                    pmv.visitFieldInsn(GETFIELD, t_className, name, Type.getDescriptor(fd.getType()));//attention:t是actually generic types
                    Label label_empty = new Label();
                    pmv.visitJumpInsn(IFNULL, label_empty);
                    writeXmlStart(cn, pmv, name, 2, map, list); // load out
                    pmv.declareLocal("serializer", Serializer.class);
                    //TODO 如果要支持泛型的动态序列化，这部分逻辑需要重构
                    pmv.visitLdcInsn(Type.getType(t));
                    pmv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/POJOSerializerProvider", "getSerializer",
                            "(Ljava/lang/Class;)Lnet/pocrd/define/Serializer;");
                    pmv.setLocal("serializer");
                    pmv.loadArg(1); // load instance
                    pmv.visitFieldInsn(GETFIELD, t_className, name, Type.getDescriptor(fd.getType()));//attention:t是actually generic types
                    pmv.declareLocal("iter", Iterator.class);
                    pmv.visitMethodInsn(INVOKEINTERFACE, "java/util/Collection", "iterator", "()Ljava/util/Iterator;");
                    pmv.setLocal("iter");
                    Label label_loop = new Label();
                    pmv.visitLabel(label_loop);
                    pmv.loadLocal("iter");
                    pmv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");
                    Label label_finish = new Label();
                    pmv.visitJumpInsn(IFEQ, label_finish);
                    writeXmlStart(cn, pmv, name.endsWith("List") ? name.substring(0, name.length() - 4) : "item", 2, map, list);
                    pmv.loadLocal("serializer");
                    pmv.loadLocal("iter");
                    pmv.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
                    pmv.loadArg(2); // load out
                    pmv.loadConst(0);
                    pmv.visitMethodInsn(INVOKEINTERFACE, "net/pocrd/define/Serializer", "toXml", "(Ljava/lang/Object;Ljava/io/OutputStream;Z)V");
                    writeXmlEnd(cn, pmv, name.endsWith("List") ? name.substring(0, name.length() - 4) : "item", 2, map, list);
                    pmv.visitJumpInsn(GOTO, label_loop);
                    pmv.visitLabel(label_finish);
                    writeXmlEnd(cn, pmv, name, 2, map, list);
                    pmv.visitLabel(label_empty);
                    pmv.deleteLocal("iter");
                    pmv.deleteLocal("serializer");
                } else if (isArray) {
                    //TODO 支持非String/enum以及基础类型的数组的序列化
                    //attention:t是actually generic types
                    throw new RuntimeException("unsupport array type,type:" + t.getName());
                } else {
                    pmv.declareLocal("obj", t);
                    pmv.loadArg(1);
                    pmv.visitFieldInsn(GETFIELD, t_className, name, Type.getDescriptor(t));
                    pmv.setLocal("obj");
                    pmv.loadLocal("obj");
                    Label label_null = new Label();
                    pmv.visitJumpInsn(IFNULL, label_null);
                    writeXmlStart(cn, pmv, name, 2, map, list);
                    //TODO 动态类型的序列化使用下面obj.getClass()而非Object.class
                    //                    if (Object.class == t) {
                    //                        pmv.visitVarInsn(ALOAD, 1);
                    //                        pmv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
                    //                    } else {
                    pmv.visitLdcInsn(Type.getType(t));
                    //                    }
                    pmv.visitMethodInsn(INVOKESTATIC, "net/pocrd/util/POJOSerializerProvider", "getSerializer",
                            "(Ljava/lang/Class;)Lnet/pocrd/define/Serializer;");
                    pmv.loadLocal("obj");
                    pmv.loadArg(2);
                    pmv.loadConst(0);
                    pmv.visitMethodInsn(INVOKEINTERFACE, "net/pocrd/define/Serializer", "toXml", "(Ljava/lang/Object;Ljava/io/OutputStream;Z)V");
                    writeXmlEnd(cn, pmv, name, 2, map, list);
                    pmv.visitLabel(label_null);
                    pmv.deleteLocal("obj");
                }
            }
        }

        pmv.loadArg(3);
        Label label_end = new Label();
        pmv.visitJumpInsn(IFEQ, label_end);
        writeXmlEnd(cn, pmv, clazz.getSimpleName(), 2, map, list);
        pmv.visitLabel(l1);
        pmv.visitJumpInsn(GOTO, label_end);
        pmv.visitLabel(l2);
        pmv.declareLocal("e", Exception.class);
        pmv.setLocal("e");
        pmv.visitTypeInsn(NEW, "net/pocrd/entity/ReturnCodeException");
        pmv.visitInsn(DUP);
        pmv.visitFieldInsn(GETSTATIC, "net/pocrd/entity/ApiReturnCode", "UNKNOWN_ERROR", "Lnet/pocrd/entity/AbstractReturnCode;");
        pmv.loadLocal("e");
        pmv.visitMethodInsn(INVOKESPECIAL, "net/pocrd/entity/ReturnCodeException", "<init>",
                "(Lnet/pocrd/entity/AbstractReturnCode;Ljava/lang/Exception;)V");
        pmv.visitInsn(ATHROW);
        pmv.visitLabel(label_end);
        pmv.visitInsn(RETURN);
        pmv.visitMaxs(0, 0);
    }

    private static void buildToJsonWithFastJson(String cn, PocMethodVisitor pmv, Class<?> clazz, List<Field> fds, String classDesc,
            String t_className, String t_classDesc, HashMap<String, Integer> map, LinkedList<String> list) {
        pmv.visitCode();
        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();
        pmv.visitTryCatchBlock(l0, l1, l2, "java/io/IOException");
        pmv.visitLabel(l0);
        pmv.loadArg(2);
        pmv.loadArg(1);
        pmv.visitFieldInsn(GETSTATIC, "net/pocrd/define/Serializer", "EMPTY_FEATURES",
                "[Lcom/alibaba/fastjson/serializer/SerializerFeature;");
        pmv.visitMethodInsn(INVOKESTATIC, "com/alibaba/fastjson/JSON", "toJSONBytes",
                "(Ljava/lang/Object;[Lcom/alibaba/fastjson/serializer/SerializerFeature;)[B");
        pmv.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "([B)V");
        pmv.visitLabel(l1);
        Label label_end = new Label();
        pmv.visitJumpInsn(GOTO, label_end);
        pmv.visitLabel(l2);
        pmv.declareLocal("e", Exception.class);
        pmv.setLocal("e");
        pmv.visitTypeInsn(NEW, "net/pocrd/entity/ReturnCodeException");
        pmv.visitInsn(DUP);
        pmv.visitFieldInsn(GETSTATIC, "net/pocrd/entity/ApiReturnCode", "UNKNOWN_ERROR", "Lnet/pocrd/entity/AbstractReturnCode;");
        pmv.loadLocal("e");
        pmv.visitMethodInsn(INVOKESPECIAL, "net/pocrd/entity/ReturnCodeException", "<init>",
                "(Lnet/pocrd/entity/AbstractReturnCode;Ljava/lang/Exception;)V");
        pmv.visitInsn(ATHROW);
        pmv.visitLabel(label_end);
        pmv.visitInsn(RETURN);
        pmv.visitMaxs(0, 0);
    }

    private static void writeString(String cn, PocMethodVisitor mv, String str, int local_out, HashMap<String, Integer> map,
            LinkedList<String> list) {
        int index = 0;
        if (map.containsKey(str)) {
            index = map.get(str);
        } else {
            list.addLast(str);
            map.put(str, list.size() - 1);
            index = list.size() - 1;
        }
        mv.visitVarInsn(ALOAD, local_out);
        mv.visitFieldInsn(GETSTATIC, cn, "bs", "[[B");
        mv.loadConst(index);
        mv.visitInsn(AALOAD);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/OutputStream", "write", "([B)V");
    }

    private static void writeXmlStart(String cn, PocMethodVisitor mv, String name, int local_out, HashMap<String, Integer> map,
            LinkedList<String> list) {
        writeString(cn, mv, "<" + name + ">", local_out, map, list);
    }

    private static void writeXmlEnd(String cn, PocMethodVisitor mv, String name, int local_out, HashMap<String, Integer> map,
            LinkedList<String> list) {
        writeString(cn, mv, "</" + name + ">", local_out, map, list);
    }

    private static void writeCDATAStart(String cn, PocMethodVisitor mv, String name, int local_out, HashMap<String, Integer> map,
            LinkedList<String> list) {
        writeString(cn, mv, "<" + name + "><![CDATA[", local_out, map, list);
    }

    private static void writeCDATAEnd(String cn, PocMethodVisitor mv, String name, int local_out, HashMap<String, Integer> map,
            LinkedList<String> list) {
        writeString(cn, mv, "]]></" + name + ">", local_out, map, list);
    }

    /**
     * 对json输出内容进行转义
     */
    public static void writeJsonString(OutputStream out, String str) throws IOException {
        out.write(str.replace("\\", "\\\\").replace("\"", "\\\"").getBytes(ConstField.UTF8));
    }

    /**
     * 对xml输出内容中的]]>进行处理
     */
    public static void writeXmlString(OutputStream out, String str) throws IOException {
        if (str != null) {
            out.write(str.replace("]]>", "]]]]><![CDATA[>").getBytes(ConstField.UTF8));
        }
    }
}
