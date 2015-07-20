package net.pocrd.core.test;

import net.pocrd.util.XmlFileConfigUtil;
import org.junit.Test;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.File;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class XmlFileConfigTest {
    @XmlRootElement
    public static class Config {
        public static Config Instance;

        public Config() {}

        static {
            try {
                Instance = XmlFileConfigUtil.load("test.config", Config.class);
            } catch (Exception e) {

            }
        }

        public String f1;
        public String f2;
        public String f3;

        @XmlTransient
        public String f4;
    }

    @Test
    public void testFileConfig() {
        Config c = new Config();
        c.f1 = "1";
        c.f2 = "2";
        c.f3 = "123";
        c.f4 = "1111";

        String path = new File(".").getAbsolutePath() + "/test.config";
        XmlFileConfigUtil.save(path, c);
        assertTrue(new File(path).exists());

        Config c2 = XmlFileConfigUtil.load("test.config", Config.class);

        assertTrue(c2 != null);
        assertTrue(c.f1.equals(c2.f1));
        assertTrue(c.f2.equals(c2.f2));
        assertTrue(c.f3.equals(c2.f3));
        assertNull(c2.f4);
    }

}
