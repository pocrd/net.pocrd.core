package net.pocrd.core.test;

import static org.junit.Assert.assertTrue;
import net.pocrd.util.ClassUtil;

import org.junit.Test;

public class ClassUtilTest {

    @Test
    public void testGetAllClassesInPackage() {
        Class<?>[] classes = ClassUtil.getAllClassesInPackage("net.pocrd");
        assertTrue(classes.length > 0);
    }
}
