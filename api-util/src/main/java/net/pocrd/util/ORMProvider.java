package net.pocrd.util;

import net.pocrd.core.PocClassLoader;
import net.pocrd.define.ConstField;
import net.pocrd.define.ResultSetMapper;
import net.pocrd.entity.CommonConfig;
import net.pocrd.entity.CompileConfig;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ORMProvider implements Opcodes {
    private static final ConcurrentHashMap<String, ResultSetMapper<?>> mappers = new ConcurrentHashMap<String, ResultSetMapper<?>>();
    private static final Object                                        locker  = new Object();

    @SuppressWarnings("unchecked")
    private static <T> ResultSetMapper<T> getMapper(String sql, Class<T> clazz) {
        ResultSetMapper<T> mapper = (ResultSetMapper<T>)mappers.get(sql);
        if (mapper == null) {
            synchronized (locker) {
                mapper = (ResultSetMapper<T>)mappers.get(sql);
                if (mapper == null) {
                    if (sql.contains("*")) {
                        throw new RuntimeException("Never use '*' in SQL statement.");
                    }
                    ClassWriter cw = new PocClassWriter(ClassWriter.COMPUTE_FRAMES);
                    String[] ps = sql.substring(sql.indexOf("select ") + 7, sql.indexOf(" from ")).trim().split(",");
                    HashMap<String, Integer> pmap = new HashMap<String, Integer>(ps.length);
                    for (int i = 0; i < ps.length; i++) {
                        if (ps[i].contains(" as ")) {
                            pmap.put(ps[i].substring(ps[i].indexOf(" as ") + 4).trim(), i + 1);
                        } else {
                            pmap.put(ps[i].trim(), i + 1);
                        }
                    }
                    String className = "net.pocrd.autogen.ORM_" + clazz.getName().replace('.', '_') + Md5Util.computeToHex(
                            sql.getBytes(ConstField.UTF8));
                    className = className.replace('$', '_');
                    String c_name = className.replace('.', '/');
                    String c_desc = "L" + c_name + ";";
                    String m_name = Type.getInternalName(ResultSetMapper.class);
                    String p_name = Type.getInternalName(clazz);
                    String p_desc = Type.getDescriptor(clazz);

                    try {
                        cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, c_name, "Ljava/lang/Object;Lnet/pocrd/define/ResultSetMapper<" + p_desc + ">;",
                                "java/lang/Object", new String[] { m_name });
                        MethodVisitor mv;
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
                            mv.visitMaxs(0, 0);
                            mv.visitEnd();
                        }
                        {
                            PocMethodVisitor pmv = new PocMethodVisitor(cw, ACC_PUBLIC, "getData", "(Ljava/sql/ResultSet;)" + p_desc, null,
                                    new String[] { "java/sql/SQLException" });
                            pmv.visitCode();
                            pmv.declareLocal("result", clazz);
                            pmv.visitTypeInsn(NEW, p_name);
                            pmv.visitInsn(DUP);
                            pmv.visitMethodInsn(INVOKESPECIAL, p_name, "<init>", "()V");
                            pmv.setLocal("result");
                            for (Field fl : clazz.getFields()) {
                                int mod = fl.getModifiers();
                                if (Modifier.isPublic(mod) && !Modifier.isFinal(mod) && !Modifier.isStatic(mod)) {
                                    String name = fl.getName();
                                    if (pmap.containsKey(name)) {
                                        pmv.loadLocal("result");
                                        pmv.loadArg(1);
                                        pmv.loadConst(pmap.get(name));
                                        loadData(pmv, fl.getType());
                                        pmv.visitFieldInsn(PUTFIELD, p_name, name, Type.getDescriptor(fl.getType()));
                                    }
                                }
                            }
                            for (Method ml : clazz.getMethods()) {
                                int mod = ml.getModifiers();
                                String name = ml.getName();
                                if (Modifier.isPublic(mod) && name.length() > 3 && name.startsWith("set") && ml.getParameterTypes().length == 1) {
                                    name = name.substring(3);
                                    if (name.length() > 1) {
                                        name = name.substring(0, 1).toLowerCase() + name.substring(1);
                                    } else {
                                        name = name.toLowerCase();
                                    }
                                    if (pmap.containsKey(name)) {
                                        pmv.loadLocal("result");
                                        pmv.loadArg(1);
                                        pmv.loadConst(pmap.get(name));
                                        loadData(pmv, ml.getParameterTypes()[0]);
                                        pmv.visitMethodInsn(INVOKEVIRTUAL, p_name, ml.getName(), Type.getMethodDescriptor(ml));
                                    }
                                }
                            }
                            pmv.loadLocal("result");
                            pmv.visitInsn(ARETURN);
                            pmv.visitMaxs(0, 0);
                            pmv.visitEnd();
                        }
                        {
                            mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "getData", "(Ljava/sql/ResultSet;)Ljava/lang/Object;", null,
                                    new String[] { "java/sql/SQLException" });
                            mv.visitCode();
                            Label l0 = new Label();
                            mv.visitLabel(l0);
                            mv.visitVarInsn(ALOAD, 0);
                            mv.visitVarInsn(ALOAD, 1);
                            mv.visitMethodInsn(INVOKEVIRTUAL, c_name, "getData", "(Ljava/sql/ResultSet;)" + p_desc);
                            mv.visitInsn(ARETURN);
                            mv.visitMaxs(0, 0);
                            mv.visitEnd();
                        }

                        cw.visitEnd();
                        if (CompileConfig.isDebug) {
                            FileOutputStream fos = null;
                            try {
                                File folder = new File(CommonConfig.getInstance().getAutogenPath() + File.separator + "ORM" + File.separator);
                                if (!folder.exists()) {
                                    folder.mkdirs();
                                }
                                fos = new FileOutputStream(
                                        CommonConfig.getInstance().getAutogenPath() + File.separator + "ORM" + File.separator
                                                + className.substring(18) + ".class");
                                fos.write(cw.toByteArray());
                            } finally {
                                if (fos != null) {
                                    fos.close();
                                }
                            }
                        }
                        mapper = (ResultSetMapper<T>)new PocClassLoader(Thread.currentThread().getContextClassLoader())
                                .defineClass(className.replace('/', '.'), cw.toByteArray()).newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(className, e);
                    }
                }
            }
        }
        if (mapper == null) {
            throw new RuntimeException("create mapper failed. " + clazz.getName());
        }

        return mapper;
    }

    private static void loadData(MethodVisitor mv, Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == boolean.class) {
                mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getBoolean", "(I)Z");
            } else if (clazz == byte.class) {
                mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getByte", "(I)B");
            } else if (clazz == char.class) {
                // Do nothing
            } else if (clazz == short.class) {
                mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getShort", "(I)S");
            } else if (clazz == int.class) {
                mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getInt", "(I)I");
            } else if (clazz == long.class) {
                mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(ResultSet.class), "getLong", "(I)J");
            } else if (clazz == float.class) {
                mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getFloat", "(I)F");
            } else if (clazz == double.class) {
                mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getDouble", "(I)D");
            } else {
                throw new RuntimeException("不支持的参数类型" + clazz.getName());
            }
        } else {
            if (clazz == String.class) {
                mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getString", "(I)Ljava/lang/String;");
            } else if (clazz == BigDecimal.class) {
                mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getBigDecimal", "(I)Ljava/math/BigDecimal;");
            } else if (clazz == Blob.class) {
                mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getBlob", "(I)Ljava/sql/Blob;");
            } else if (clazz == Clob.class) {
                mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getClob", "(I)Ljava/sql/Clob;");
            } else if (clazz == NClob.class) {
                mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getNClob", "(I)Ljava/sql/NClob;");
            } else if (clazz == byte[].class) {
                mv.visitMethodInsn(INVOKEINTERFACE, "java/sql/ResultSet", "getBytes", "(I)[B");
            }
        }
    }

    public static <T> T getData(String sql, ResultSet rs, Class<T> clazz) throws SQLException {
        ResultSetMapper<T> mapper = getMapper(sql, clazz);
        if (rs.next()) {
            return mapper.getData(rs);
        }
        return null;
    }

    public static <T> List<T> getList(String sql, ResultSet rs, Class<T> clazz) throws SQLException {
        ResultSetMapper<T> mapper = getMapper(sql, clazz);
        List<T> list = new LinkedList<T>();
        while (rs.next()) {
            list.add(mapper.getData(rs));
        }
        return list;
    }
}
