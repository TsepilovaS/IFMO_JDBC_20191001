package com.efimchick.ifmo.web.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SetMapper<T> {
    T mapSet(ResultSet resultSet) throws SQLException;
}
