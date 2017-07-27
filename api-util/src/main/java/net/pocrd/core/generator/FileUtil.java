package net.pocrd.core.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;

/**
 * Created by guankaiqiang521 on 2014/9/27.
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private static void listFileInpath(String url, FilenameFilter filenameFilter, List<File> resultList) {
        File file = new File(url);
        if (file.isDirectory()) {
            String[] fileNames = file.list(filenameFilter);
            if (fileNames != null) {
                for (String fileName : fileNames) {
                    listFileInpath(url + File.separator + fileName, filenameFilter, resultList);
                }
            }
        } else {
            resultList.add(file);
        }
    }

    /**
     * 列出目录下所有类型为${fileType}的文件
     *
     * @param path
     * @param fileType
     */
    public static List<File> listFileInpath(String path, String fileType) {
        File file = new File(path);
        List<File> list = new LinkedList<File>();
        if (!file.isDirectory() && path.endsWith("." + fileType)) {
            list.add(file);
        } else {
            FilenameFilter filenameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return dir.getName().endsWith("." + name) || dir.isDirectory();
                }
            };
            listFileInpath(path, filenameFilter, list);
        }
        return list;
    }

    private static void listDirInpath(String url, List<File> resultList) {
        File file = new File(url);
        if (file.isDirectory()) {
            resultList.add(file);
            String[] fileNames = file.list();
            if (fileNames != null) {
                for (String fileName : fileNames) {
                    listDirInpath(url + File.separator + fileName, resultList);
                }
            }
        }
    }

    public static List<File> listDirInpath(String path) {
        List<File> list = null;
        if (path.contains(".jar!")) {
            JarFile jarFile = null;
            //TODO
        } else {
            File file = new File(path);
            if (file.isDirectory()) {
                list = new LinkedList<File>();
                File[] files = file.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isDirectory();
                    }
                });
                if (files != null) {
                    for (File f : files) {
                        listDirInpath(f.getPath(), list);
                    }
                }
            }
        }
        return list;
    }

    public static void deleteDir(String path) {
        File folder = new File(path);
        if (folder.exists()) {
            if (folder.isDirectory()) {
                if (folder.list() == null || folder.list().length == 0) {
                    if (folder.delete()) {
                        logger.info("delete file:{}", folder.getPath());
                        System.out.println("delete file:" + folder.getPath());
                    } else {
                        logger.error("delete file:{} failed!", folder.getPath());
                        throw new RuntimeException(String.format("delete file:%s failed!", path));
                    }
                } else {
                    for (String n : folder.list()) {
                        deleteDir(path + File.separator + n);
                    }
                }
            } else if (folder.isFile()) {
                if (folder.delete()) {
                    logger.info("delete file:{}", folder.getPath());
                    System.out.println("delete file:" + folder.getPath());
                } else {
                    logger.error("delete file:{} failed!", folder.getPath());
                    throw new RuntimeException(String.format("delete file:%s failed!", path));
                }
            }
            folder.delete();
        }
    }

    public static void recreateDir(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                logger.info("creade folder:{}", path);
                System.out.println("creade folder:" + path);
            } else {
                logger.error("create folder:{} failed!", folder.getName());
                throw new RuntimeException(String.format("create folder:%s failed!", path));
            }
        } else {
            if (folder.isDirectory()) {
                if (folder.list() != null) {
                    for (String n : folder.list()) {
                        deleteDir(path + File.separator + n);
                    }
                }
            }
        }
    }

    public static byte[] readAll(String path) throws IOException {
        File f = new File(path);
        if (f.exists() && f.isFile()) {
            try (FileInputStream fis = new FileInputStream(f)) {
                byte[] bs = new byte[fis.available()];
                fis.read(bs);
                return bs;
            }
        } else {
            return null;
        }
    }
}
