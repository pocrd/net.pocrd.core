package net.pocrd.util;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public final class FileConfig {
    private static final String path;
    static {
        File directory = new File(".");
        path = directory.getAbsolutePath();
    }

    /**
     * 保存配置文件到指定目录
     * 
     * @param path
     * @param config
     */
    public static void save(String path, Object config) {
        try {
            JAXBContext context = JAXBContext.newInstance(config.getClass());
            Marshaller m = context.createMarshaller();
            m.setProperty("jaxb.formatted.output", true);
            
            //m.setProperty("com.sun.xml.bind.xmlDeclaration", false);
            m.marshal(config, new File(path));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从当前工作目录读取配置文件，如果找不到则向上级目录寻找
     * 
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T load(String name, Class<T> clazz) {
        try {
            File file = new File(path + "/" + name);
            if (!file.exists()) {
                File parentFolder = new File(path).getParentFile();
                while (!file.exists() && parentFolder != null) {
                    file = new File(parentFolder.getAbsolutePath() + "/" + name);
                    parentFolder = parentFolder.getParentFile();
                }
            }
            if (!file.exists()) {
                throw new RuntimeException("cannot file config file : " + name);
            }

            JAXBContext context = JAXBContext.newInstance(clazz);
            T c = (T)context.createUnmarshaller().unmarshal(file);
            return c;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
