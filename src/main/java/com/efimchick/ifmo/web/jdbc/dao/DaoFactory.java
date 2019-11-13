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

    List<Employee> employees;

    {
        try {
            employees = getEmployees();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    List<Department> departments;

    {
        try {
            departments = getDepartments();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Employee> mapSet(ResultSet res) throws SQLException {
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

    private List<Employee> getEmployees() throws SQLException {
        ConnectionSource connectionSource = ConnectionSource.instance();
        Connection connection = connectionSource.createConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM EMPLOYEE");
        return mapSet(resultSet);
    }

    private List<Department> getDepartments() throws SQLException {
        ConnectionSource connectionSource = ConnectionSource.instance();
        Connection connection = connectionSource.createConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM DEPARTMENT");
        return getdepartments(resultSet);

    }

    private List<Department> getdepartments(ResultSet resultSet) {
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
                List<Employee> list = new ArrayList<>();
                for (int i = 0; i < employees.size(); ++i) {
                    if (employees.get(i).getDepartmentId().equals(department.getId())) {
                        list.add(employees.get(i));
                    }
                }
                return list;
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                List<Employee> list = new ArrayList<>();
                for (int i = 0; i < employees.size(); i++) {
                    if (employees.get(i).getManagerId().equals(employee.getId())) {
                        list.add(employees.get(i));
                    }
                }
                return list;
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                for (int i = 0; i < employees.size(); i++) {
                    if (employees.get(i).getId().equals(Id)) {
                        return Optional.of(employees.get(i));
                    }
                }
                return Optional.empty();
            }

            @Override
            public List<Employee> getAll() {
                return employees;
            }

            @Override
            public Employee save(Employee employee) {
                employees.add(employee);
                return employee;
            }

            @Override
            public void delete(Employee employee) {
                employees.remove(employee);
            }
        };
    }

    public DepartmentDao departmentDAO() {
        return new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id) {
                for (int i = 0; i < departments.size(); i++) {
                    if (departments.get(i).getId().equals(Id)) {
                        return Optional.of(departments.get(i));
                    }
                }
                return Optional.empty();
            }

            @Override
            public List<Department> getAll() {
                return departments;
            }

            @Override
            public Department save(Department department) {
                for (int i = 0; i < departments.size(); i++) {
                    if (departments.get(i).getId().equals(department.getId())) {
                        departments.remove(departments.get(i));
                    }
                }
                departments.add(department);
                return department;
            }

            @Override
            public void delete(Department department) {
                departments.remove(department);
            }
        };
    }
}