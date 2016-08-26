package net.pocrd.define;

public interface HttpApiExecutor {
    void setInstance(Object obj);

    Object execute(String[] parameters);
}
