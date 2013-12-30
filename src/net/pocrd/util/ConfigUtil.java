package net.pocrd.util;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ConfigUtil {
    private static final Logger logger        = LogManager.getLogger(ConfigUtil.class);
    private static final String path;
    private static final int    SEARCH_PARENT = 3;
    static {
        File directory = new File(".");
        path = directory.getAbsolutePath();
        logger.info("set config base path:" + path);
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
            // m.setProperty("com.sun.xml.bind.xmlDeclaration", false);
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
            File file = new File(name);
            if (!file.exists()) {
                File folder = new File(path);
                int i = 0;
                while (!file.exists() && folder != null) {
                    file = new File(folder.getAbsolutePath() + "/" + name);
                    folder = folder.getParentFile();
                    i++;
                    if (i > SEARCH_PARENT) {
                        break;
                    }
                }
            }
            if (!file.exists()) {
                if (!CommonConfig.isDebug) {
                    throw new RuntimeException("cannot file config file : " + name + " from " + path);
                } else {
                    logger.error("cannot load config file." + name + " from " + path);
                    return null;
                }
            } else {
                logger.info("load config file " + name + " from " + file.getAbsolutePath() + " successful.");
            }

            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller um = context.createUnmarshaller();
            T c = (T)um.unmarshal(file);
            return c;
        } catch (JAXBException e) {
            if (!CommonConfig.isDebug) {
                throw new RuntimeException(e);
            } else {
                logger.error("cannot load config file." + name + " from " + path, e);
                return null;
            }
        }
    }
}
