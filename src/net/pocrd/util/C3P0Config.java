package net.pocrd.util;

public class C3P0Config {
    private String driverClass;
    private String jdbcUrl;
    private String user;
    private String password;
    private int acquireIncrement;
    private int initialPoolSize;
    private int minPoolSize;
    private int maxPoolSize;
    private int maxStatements;
    private int maxStatementsPerConnection;
    /**
     * default init
     */
    public C3P0Config(String driverClass,String jdbcUrl,String user,String pwd,int acquireIncrement,
            int initialPoolSize,int minPoolSize,int maxPoolSize,int maxStatements,int maxStatementsPerConnection){
        this.driverClass=driverClass;
        this.jdbcUrl=jdbcUrl;
        this.user=user;
        this.password=pwd;
        this.acquireIncrement=acquireIncrement;
        this.initialPoolSize=initialPoolSize;
        this.minPoolSize=minPoolSize;
        this.maxPoolSize=maxPoolSize;
        this.maxStatements=maxStatements;
        this.maxStatementsPerConnection=maxStatementsPerConnection;
    }
    public C3P0Config(){}
    public String getDriverClass() {
        return driverClass;
    }
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }
    public String getJdbcUrl() {
        return jdbcUrl;
    }
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getAcquireIncrement() {
        return acquireIncrement;
    }
    public void setAcquireIncrement(int acquireIncrement) {
        this.acquireIncrement = acquireIncrement;
    }
    public int getInitialPoolSize() {
        return initialPoolSize;
    }
    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }
    public int getMinPoolSize() {
        return minPoolSize;
    }
    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }
    public int getMaxPoolSize() {
        return maxPoolSize;
    }
    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }
    public int getMaxStatements() {
        return maxStatements;
    }
    public void setMaxStatements(int maxStatements) {
        this.maxStatements = maxStatements;
    }
    public int getMaxStatementsPerConnection() {
        return maxStatementsPerConnection;
    }
    public void setMaxStatementsPerConnection(int maxStatementsPerConnection) {
        this.maxStatementsPerConnection = maxStatementsPerConnection;
    }
}
