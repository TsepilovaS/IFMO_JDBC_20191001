package com.efimchick.ifmo.web.jdbc;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigInteger;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RowMapperFactory {

    public RowMapper<Employee> employeeRowMapper() {
        RowMapper<Employee> rm = new RowMapper<Employee>() {
            @Override
            public Employee mapRow(ResultSet res) {
                try {
                    BigInteger id = new BigInteger(res.getString("id"));
                    String first = res.getString("firstname");
                    String last = res.getString("lastname");
                    String middle = res.getString("middlename");
                    FullName full = new FullName(first, last, middle);
                    Position position = Position.valueOf(res.getString("position"));
                    LocalDate hire = LocalDate.parse(res.getString("hiredate"));
                    BigDecimal salary = new BigDecimal(res.getString("SALARY"));
                    Employee employee = new Employee(id, full, position, hire, salary);
                    return employee;
                } catch (SQLException e) {
                    return null;
                }
            }
        };
        return rm;
    }
}