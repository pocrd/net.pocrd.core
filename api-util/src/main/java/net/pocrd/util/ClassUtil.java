package net.pocrd.util;

import net.pocrd.annotation.HttpDataMixer;
import net.pocrd.define.ConstField;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 获取命名空间下的所有类
 *
 * @author rendong
 */
public class ClassUtil {
    public static Class<?>[] getAllClassesInPackage(String packageName) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new LinkedList<>();
            List<URL> jars = new LinkedList<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                String protocol = resource.getProtocol();
                if ("file".equals(protocol)) {
                    // 本地自己可见的代码
                    dirs.add(new File(resource.getFile()));
                } else if ("jar".equals(protocol)) {
                    // 引用第三方jar的代码
                    jars.add(resource);
                }
            }
            List<Class<?>> classes = new LinkedList<Class<?>>();
            for (File directory : dirs) {
                classes.addAll(findClasses(directory, packageName));
            }
            for (URL url : jars) {
                classes.addAll(findClasses(url, packageName));
            }
            return classes.toArray(new Class[classes.size()]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Class<?>> getAllMixerClasses(JarFile jarFile, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new LinkedList<Class<?>>();
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String jarEntryName = jarEntry.getName(); // 类似：sun/security/internal/interfaces/TlsMasterSecret.class
            String className = jarEntryName.replace("/", ".");
            if (className.startsWith(packageName) && className.endsWith(".class") && !className.contains("$")) {
                Class<?> clazz = loadClass(className.substring(0, className.length() - 6));
                if (clazz.getAnnotation(HttpDataMixer.class) != null) {
                    classes.add(clazz);
                }
            }
        }
        return classes;
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new LinkedList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(loadClass(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                }
            }
        }
        return classes;
    }

    private static List<Class<?>> findClasses(URL url, String packageName) {
        List<Class<?>> classes = new LinkedList<Class<?>>();
        try {
            JarURLConnection jarURLConnection = (JarURLConnection)url.openConnection();
            JarFile jarFile = jarURLConnection.getJarFile();
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry jarEntry = jarEntries.nextElement();
                String jarEntryName = jarEntry.getName(); // 类似：sun/security/internal/interfaces/TlsMasterSecret.class
                String className = jarEntryName.replace("/", ".");
                if (className.startsWith(packageName) && className.endsWith(".class")) {
                    classes.add(loadClass(className.substring(0, className.length() - 6)));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("parse class failed from " + url.toString(), e);
        }
        return classes;
    }

    private static ClassLoader getTCL() {
        ClassLoader cl;
        if (System.getSecurityManager() == null) {
            cl = Thread.currentThread().getContextClassLoader();
        } else {
            cl = java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<ClassLoader>() {
                @Override
                public ClassLoader run() {
                    return Thread.currentThread().getContextClassLoader();
                }
            });
        }

        return cl;
    }

    public static Class<?> loadClass(final String className) throws ClassNotFoundException {
        try {
            return getTCL().loadClass(className);
        } catch (final Throwable e) {
            return Class.forName(className, false, Thread.currentThread().getContextClassLoader());
        }
    }

    public static ConcurrentHashMap<String, String> getAllProtoInPackage(String packageName) {
        try {
            ConcurrentHashMap<String, String> protos = new ConcurrentHashMap<String, String>();
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            List<File> dirs = new LinkedList<File>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }

            for (File directory : dirs) {
                protos.putAll(findProtoFiles(directory, packageName));
            }
            return protos;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static HashMap<String, String> findProtoFiles(File directory, String packageName) {
        HashMap<String, String> protos = new HashMap<String, String>();
        if (!directory.exists()) {
            return protos;
        }
        File[] files = directory.listFiles();
        try {
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        protos.putAll(findProtoFiles(file, packageName + "." + file.getName()));
                    } else if (file.getName().endsWith(".proto")) {
                        protos.put(file.getName().substring(0, file.getName().length() - 6), readAllContent(file));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("find proto file failed.", e);
        }
        return protos;
    }

    private static String readAllContent(File f) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            byte[] bs = new byte[fis.available()];
            int size = fis.read(bs);
            return new String(bs, 0, size, ConstField.UTF8);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
}
