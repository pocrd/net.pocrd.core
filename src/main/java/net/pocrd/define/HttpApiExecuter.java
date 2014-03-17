package net.pocrd.define;

public interface HttpApiExecuter {
    void setInstance(Object obj);
    Object execute(String[] parameters);
}
