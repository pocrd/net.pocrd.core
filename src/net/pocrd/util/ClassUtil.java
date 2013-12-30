package net.pocrd.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.pocrd.define.ConstField;

import org.apache.logging.log4j.core.helpers.Loader;

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
            List<File> dirs = new LinkedList<File>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }
            List<Class<?>> classes = new LinkedList<Class<?>>();
            for (File directory : dirs) {
                classes.addAll(findClasses(directory, packageName));
            }
            return classes.toArray(new Class[classes.size()]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new LinkedList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Loader.loadClass(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public static HashMap<String, String> getAllProtoInPackage(String packageName) {
        try {
            HashMap<String, String> protos = new HashMap<String, String>();
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
            for (File file : files) {
                if (file.isDirectory()) {
                    protos.putAll(findProtoFiles(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".proto")) {
                    protos.put(file.getName().substring(0, file.getName().length() - 6), readAllContent(file));
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
            fis.read(bs);
            return new String(bs, ConstField.UTF8);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
}
