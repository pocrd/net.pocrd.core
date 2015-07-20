package net.pocrd.util;

public class JDBCPoolConfig {
    // 配置节点名称
    private String configName;
    // 数据库连接地址
    private String jdbcUrl;
    // 驱动名
    private String driverClassName;
    private String username;
    private String password;
    // 是否允许JMX
    private boolean jmxEnabled = true;

    // 下面的属性是验证空闲连接使用
    // 空闲时是否进行检测,使用validationQuery
    private boolean testWhileIdle      = false;
    // borrow时进行检测
    private boolean testOnBorrow       = true;
    // return时进行检测
    private boolean testOnReturn       = false;
    // 验证语句
    private String  validationQuery    = "SELECT 1";
    // 验证周期
    private int     validationInterval = 30000;

    // 闲置连接回收周期
    private int     timeBetweenEvictionRunsMillis;
    // 同一时刻连接池中允许的最大活跃连接数
    private int     maxActive;
    // 初始连接池大小 initialSize<=maxActive
    private int     initialSize;
    // 连接最多等待时常
    private int     maxWait;
    // 对象在空闲连接池中最长驻留时间60s
    private int     minEvictableIdleTimeMillis;
    // 周期内允许的最少闲置链接数(用于分配)
    private int     minIdle;
    // minEvictableIdleTimeMillis周期内允许的最大闲置链接数
    private int     maxIdle;
    // 对于废弃连接（未close的链接）是否允许在removeAbandonedTimeout时回收
    private boolean removeAbandoned;
    // 废弃连接回收周期
    private int     removeAbandonedTimeout;
    // 是否跟踪记录废弃连接的堆栈信息
    private boolean logAbandoned;
    // 连接池拦截器
    private String  jdbcInterceptors;

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isJmxEnabled() {
        return jmxEnabled;
    }

    public void setJmxEnabled(boolean jmxEnabled) {
        this.jmxEnabled = jmxEnabled;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    /**
     * (String) The SQL query that will be used to validate connections from this pool before returning them to the caller. If specified, this query
     * does not have to return any data, it just can't throw a SQLException. The default value is null. Example values are SELECT 1(mysql), select 1
     * from dual(oracle), SELECT 1(MS Sql Server)
     *
     * @param validationQuery
     */
    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public int getValidationInterval() {
        return validationInterval;
    }

    /**
     * (long) avoid excess validation, only run validation at most at this frequency - time in milliseconds. If a connection is due for validation,
     * but has been validated previously within this interval, it will not be validated again. The default value is 30000 (30 seconds).
     *
     * @param validationInterval
     */
    public void setValidationInterval(int validationInterval) {
        this.validationInterval = validationInterval;
    }

    public int getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    /**
     * (int) The number of milliseconds to sleep between runs of the idle connection validation/cleaner thread. This value should not be set under 1
     * second. It dictates how often we check for idle, abandoned connections, and how often we validate idle connections. The default value is 5000
     * (5 seconds).
     *
     * @param timeBetweenEvictionRunsMillis
     */
    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getMaxActive() {
        return maxActive;
    }

    /**
     * The maximum number of active connections that can be allocated from this pool at the same time. The default value is 100
     *
     * @param maxActive
     */
    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getInitialSize() {
        return initialSize;
    }

    /**
     * (int)The initial number of connections that are created when the pool is started. Default value is 10
     *
     * @param initialSize
     */
    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    public int getMaxWait() {
        return maxWait;
    }

    /**
     * (int) The maximum number of milliseconds that the pool will wait (when there are no available connections) for a connection to be returned
     * before throwing an exception. Default value is 30000 (30 seconds)
     *
     * @param maxWait
     */
    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    public int getRemoveAbandonedTimeout() {
        return removeAbandonedTimeout;
    }

    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
    }

    public int getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    /**
     * (int) The minimum amount of time an object may sit idle in the pool before it is eligible for eviction. The default value is 60000 (60
     * seconds).
     *
     * @param minEvictableIdleTimeMillis
     */
    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public int getMinIdle() {
        return minIdle;
    }

    /**
     * The minimum number of established connections that should be kept in the pool at all times. The connection pool can shrink below this number if
     * validation queries fail. Default value is derived from initialSize:10 (also see testWhileIdle)
     *
     * @param minIdle
     */
    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    /**
     * The maximum number of connections that should be kept in the pool at all times. Default value is maxActive:100 Idle connections are checked
     * periodically (if enabled) and connections that been idle for longer than minEvictableIdleTimeMillis will be released. (also see testWhileIdle)
     *
     * @param maxIdle
     */
    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public boolean isLogAbandoned() {
        return logAbandoned;
    }

    /**
     * (boolean) Flag to log stack traces for application code which abandoned a Connection. Logging of abandoned Connections adds overhead for every
     * Connection borrow because a stack trace has to be generated. The default value is false.
     *
     * @param logAbandoned
     */
    public void setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }

    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    /**
     * (boolean) Flag to remove abandoned connections if they exceed the removeAbandonedTimeout. If set to true a connection is considered abandoned
     * and eligible for removal if it has been in use longer than the removeAbandonedTimeout Setting this to true can recover db connections from
     * applications that fail to close a connection. See also logAbandoned The default value is false.
     *
     * @param removeAbandoned
     */
    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public String getJdbcInterceptors() {
        return jdbcInterceptors;
    }

    /**
     * flexible and pluggable interceptors to create any customizations around the pool, the query execution and the result set handling. More on this
     * in the advanced section.
     *
     * @param jdbcInterceptors
     */
    public void setJdbcInterceptors(String jdbcInterceptors) {
        this.jdbcInterceptors = jdbcInterceptors;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    /**
     * (boolean) The indication of whether objects will be validated by the idle object evictor (if any). If an object fails to validate, it will be
     * dropped from the pool. NOTE - for a true value to have any effect, the validationQuery parameter must be set to a non-null string. The default
     * value is false and this property has to be set in order for the pool cleaner/test thread is to run (also see timeBetweenEvictionRunsMillis)
     *
     * @param testWhileIdle
     */
    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    /**
     * (boolean) The indication of whether objects will be validated before being borrowed from the pool. If the object fails to validate, it will be
     * dropped from the pool, and we will attempt to borrow another. NOTE - for a true value to have any effect, the validationQuery parameter must be
     * set to a non-null string. In order to have a more efficient validation, see validationInterval. Default value is false
     *
     * @param testOnBorrow
     */
    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    /**
     * (boolean) The indication of whether objects will be validated before being returned to the pool. NOTE - for a true value to have any effect,
     * the validationQuery parameter must be set to a non-null string. The default value is false.
     *
     * @param testOnReturn
     */
    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }
}
