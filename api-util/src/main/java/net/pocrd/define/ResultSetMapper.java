package net.pocrd.define;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetMapper<T> {
    T getData(ResultSet rs) throws SQLException;
}
