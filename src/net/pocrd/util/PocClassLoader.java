package net.pocrd.util;

class PocClassLoader extends ClassLoader {
    Class<?> defineClass(String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }
}
