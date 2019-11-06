package com.efimchick.ifmo.web.jdbc;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {
//        return res -> {
//            Set<Employee> emp = new HashSet<>();
//            try {
//                while (res.next()) {
//                    emp.add(getEmp(res));
//                }
//            } catch (SQLException ignore){
//
//            return emp;
//            }
//        };
        return new SetMapper<Set<Employee>>() {
            @Override
            public Set<Employee> mapSet(ResultSet res) throws SQLException {
                Set<Employee> emp = new HashSet<>();
                try {
                    while (res.next()) {
                        emp.add(getEmp(res));
                    }
                } catch (SQLException ignore) {

                    return null;
                }
                return emp;
            }
        };
    }

    public Employee getEmp(ResultSet res) {
        try {
            BigInteger id = new BigInteger(res.getString("id"));
            String first = res.getString("firstname");
            String last = res.getString("lastname");
            String middle = res.getString("middlename");
            FullName full = new FullName(first, last, middle);
            Position position = Position.valueOf(res.getString("position"));
            LocalDate hire = LocalDate.parse(res.getString("hiredate"));
            BigDecimal salary = new BigDecimal(res.getString("SALARY"));
            Employee manager = null;
            if (res.getObject("manager") != null) {
                int cursor = res.getRow();
                int idman = res.getInt("manager");
                res.beforeFirst();
                while (res.next()) {
                    if (idman == res.getInt("ID")) {
                        manager = getEmp(res);
                        break;
                    }
                }
                res.absolute(cursor);
            }
            Employee employee = new Employee(id, full, position, hire, salary, manager);
            return employee;
        } catch (SQLException e) {
            return null;
        }
    }
}

