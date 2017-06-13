package net.pocrd.core.test;

import net.pocrd.util.ClassUtil;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ClassUtilTest {

    @Test
    public void testGetAllClassesInPackage() {
        Class<?>[] classes = ClassUtil.getAllClassesInPackage("net.pocrd");
        assertTrue(classes.length > 0);
    }
}
