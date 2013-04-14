package net.pocrd.util;

public interface HttpApiExecuter {
    void setInstance(Object obj);
    Object execute(String[] parameters);
}
