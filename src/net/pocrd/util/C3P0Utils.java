package net.pocrd.util;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author guankaiqiang
 */
public class C3P0Utils {
    private final static Logger logger     = LogManager.getLogger(C3P0Utils.class);
    private static ComboPooledDataSource mainCpds;
    static {
        mainCpds = new ComboPooledDataSource();
        C3P0Config c3p0config = CommonConfig.Instance.c3p0config;
        try {
            mainCpds.setDriverClass(c3p0config.getDriverClass());
        } catch (PropertyVetoException e) {
            logger.error(e);
        }
        mainCpds.setJdbcUrl(c3p0config.getJdbcUrl());
        mainCpds.setUser(c3p0config.getUser());
        mainCpds.setPassword(c3p0config.getPassword());
        mainCpds.setMaxStatementsPerConnection(c3p0config.getMaxStatementsPerConnection());
        mainCpds.setMaxStatements(c3p0config.getMaxStatements());
        mainCpds.setMaxPoolSize(c3p0config.getMaxPoolSize());
        mainCpds.setAcquireIncrement(c3p0config.getAcquireIncrement());
        mainCpds.setInitialPoolSize(c3p0config.getInitialPoolSize());
        mainCpds.setMinPoolSize(c3p0config.getMinPoolSize());
    }

    public static DataSource getDataSource() {
        return mainCpds;
    }

    public static Connection getConnection() throws Exception {
        return mainCpds.getConnection();
    }
}
