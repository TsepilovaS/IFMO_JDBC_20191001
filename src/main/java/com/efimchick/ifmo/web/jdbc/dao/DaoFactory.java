package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

public class DaoFactory {

    public List<Employee> mapSet(ResultSet res) throws SQLException {
        res.beforeFirst();
        List<Employee> emp = new ArrayList<>();
        try {
            while (res.next()) {
                emp.add(getEmp(res));
            }
        } catch (SQLException ignore) {

            return null;
        }
        return emp;
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
            BigInteger manId = BigInteger.valueOf(res.getString("MANAGER") == null ? 0 : Long.parseLong(res.getString("MANAGER")));
            BigInteger depId = BigInteger.valueOf(res.getString("DEPARTMENT") == null ? 0 : Long.parseLong(res.getString("DEPARTMENT")));
            /*Employee manager = null;
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
            }*/
            Employee employee = new Employee(id, full, position, hire, salary, manId, depId);
            return employee;
        } catch (SQLException e) {
            return null;
        }
    }

    private ResultSet getResultSet(String SQL) throws SQLException {
        ConnectionSource connectionSource = ConnectionSource.instance();
        Connection connection = connectionSource.createConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return statement.executeQuery(SQL);
    }

    private List<Department> getdepartments(ResultSet resultSet) throws SQLException {
        resultSet.beforeFirst();
        List<Department> departments = new ArrayList<>();
        try {
            while (resultSet.next()) {
                departments.add(
                        new Department(
                                new BigInteger(resultSet.getString("ID")),
                                resultSet.getString("NAME"),
                                resultSet.getString("LOCATION")
                        )
                );
            }
        } catch (SQLException e) {
        }
        return departments;
    }

    public EmployeeDao employeeDAO() {
        return new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department) {
                try {
                    ResultSet res = getResultSet("select * from employee where department = " + department.getId());
                    return mapSet(res);
                } catch (SQLException e) {
                }
                return null;
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                try {
                    ResultSet res = getResultSet("select * from employee where manager = " + employee.getId());
                    return mapSet(res);
                } catch (SQLException e) {
                }
                return null;
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                try {
                    ResultSet res = getResultSet("select * from employee where id = " + Id.intValue());
                    if (res.next() == false){
                        return Optional.empty();
                    } else {
                        List<Employee> emp = mapSet(res);
                        return Optional.of(emp.get(0));
                    }
                } catch (SQLException e) {
                    return Optional.empty();
                }
            }

            @Override
            public List<Employee> getAll() {
                try {
                    return mapSet(getResultSet("select * from employee"));
                } catch (SQLException e) {
                }
                return null;
            }

            @Override
            public Employee save(Employee employee) {
                try {
                    ConnectionSource connectionSource = ConnectionSource.instance();
                    Connection connection = connectionSource.createConnection();
                    connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeUpdate("insert into employee values('"
                            + employee.getId() + "', '"
                            + employee.getFullName().getFirstName() + "', '"
                            + employee.getFullName().getLastName() + "', '"
                            + employee.getFullName().getMiddleName() + "', '"
                            + employee.getPosition() + "', '"
                            + employee.getManagerId() + "', '"
                            + employee.getHired() + "', '"
                            + employee.getSalary() + "', '"
                            + employee.getDepartmentId() + "')");
                } catch (SQLException e) {
                }
                return employee;
            }

            @Override
            public void delete(Employee employee) {
                try {
                    ConnectionSource connectionSource = ConnectionSource.instance();
                    Connection connection = connectionSource.createConnection();
                    connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeUpdate("delete from employee where id = " + employee.getId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public DepartmentDao departmentDAO() {
        return new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id) {
                try {
                    ResultSet res = getResultSet("select * from department where id = " + Id);
                    if (res.next() == false) {
                        return Optional.empty();
                    } else {
                        List<Department> dep = getdepartments(res);
                        return Optional.of(dep.get(0));
                    }
                } catch (SQLException e) {
                    return Optional.empty();
                }
            }

            @Override
            public List<Department> getAll() {
                try {
                    return getdepartments(getResultSet("select * from department"));
                } catch (SQLException e) {
                }
                return null;
            }

            @Override
            public Department save(Department department) { ;
                Optional<Department> optDep = getById(department.getId());
                String SQL="update department set name = '"
                        + department.getName() + "',"
                        + " location = '" + department.getLocation()
                        + "' where id = '" + department.getId() + "'";
                if (!optDep.isPresent()) {
                    SQL = "insert into department values('"
                            + department.getId() + "', '"
                            + department.getName() + "', '"
                            + department.getLocation() + "')";
                }
                try {
                    ConnectionSource connectionSource = ConnectionSource.instance();
                    Connection connection = connectionSource.createConnection();
                    connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeUpdate(SQL);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return department;
            }

            @Override
            public void delete(Department department) {
                try {
                    ConnectionSource connectionSource = ConnectionSource.instance();
                    Connection connection = connectionSource.createConnection();
                    connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE).executeUpdate("delete from department where id = " + department.getId());
                }catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}