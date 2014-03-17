package net.pocrd.core.test.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.pocrd.define.ResultSetMapper;

public class Test implements ResultSetMapper<ORMObject> {
    public ORMObject getData(ResultSet paramResultSet) throws SQLException {
        ORMObject localORMObject = new ORMObject();
        return localORMObject;
    }
}
