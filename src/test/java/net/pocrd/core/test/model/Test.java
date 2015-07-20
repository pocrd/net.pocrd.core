package net.pocrd.core.test.model;

import net.pocrd.define.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Test implements ResultSetMapper<ORMObject> {
    public ORMObject getData(ResultSet paramResultSet) throws SQLException {
        ORMObject localORMObject = new ORMObject();
        return localORMObject;
    }
}
