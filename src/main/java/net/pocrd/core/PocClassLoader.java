package net.pocrd.core;

public class PocClassLoader extends ClassLoader {
    public PocClassLoader(){
        super();
    }
    
    public PocClassLoader(ClassLoader cl){
        super(cl);
    }
    
    public Class<?> defineClass(String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }
}
