package com.efimchick.ifmo.web.jdbc.service;

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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ServiceFactory {

    ResultSet getRS(String s) throws SQLException {
        ConnectionSource connectionSource = ConnectionSource.instance();
        Connection connection = connectionSource.createConnection();
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return statement.executeQuery(s);
    }

    List<Department> departments;

    {
        try {
            departments = getDepartments();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Employee getEmpWithoutChain(ResultSet res, int level) {
        try {
            level++;
            if (level > 2)
                return null;
            BigInteger id = new BigInteger(res.getString("id"));
            String first = res.getString("firstname");
            String last = res.getString("lastname");
            String middle = res.getString("middlename");
            FullName full = new FullName(first, last, middle);
            Position position = Position.valueOf(res.getString("position"));
            LocalDate hire = LocalDate.parse(res.getString("hiredate"));
            BigDecimal salary = new BigDecimal(res.getString("SALARY"));
            Department department = getDepartmentById(res.getString("DEPARTMENT"));
            Employee manager = null;
            if (res.getString("MANAGER") != null){
                ResultSet newRS = getRS("SELECT * FROM EMPLOYEE");
                while (newRS.next()){
                    if (newRS.getString("ID").equals(res.getString("MANAGER"))){
                        manager = getEmpWithoutChain(newRS, level);
                        break;
                    }
                }
            }
            return new Employee(id, full, position, hire, salary, manager, department);
        } catch (SQLException e) {
            return null;
        }
    }

    private Department getDepartmentById(String department) {
        if (department == null)
            return null;
        try {
            for (Department d :getDepartments()){
                if (d.getId().equals(new BigInteger(department))){
                    return d;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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

    List<Employee> page(List<Employee> list, Paging paging){
        List<Employee> res = new LinkedList<>();
        for (int i = (paging.page - 1) * paging.itemPerPage; i<Math.min((paging.page) * paging.itemPerPage, list.size()); ++i){
            res.add(list.get(i));
        }
        return res;
    }


    public EmployeeService employeeService() {
        return new EmployeeService() {
            @Override
            public List<Employee> getAllSortByHireDate(Paging paging) {
                try {
                    ResultSet rs = getRS("SELECT * FROM EMPLOYEE ORDER BY HIREDATE");
                    List<Employee> employees = new ArrayList<>();
                    while (rs.next()){
                        employees.add(getEmpWithoutChain(rs, 0));
                    }
                    return page(employees, paging);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public List<Employee> getAllSortByLastname(Paging paging) {
                try {
                    ResultSet rs = getRS("SELECT * FROM EMPLOYEE ORDER BY LASTNAME");
                    List<Employee> employees = new ArrayList<>();
                    while (rs.next()){
                        employees.add(getEmpWithoutChain(rs, 0));
                    }
                    return page(employees, paging);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public List<Employee> getAllSortBySalary(Paging paging) {
                try {
                    ResultSet rs = getRS("SELECT * FROM EMPLOYEE ORDER BY SALARY");
                    List<Employee> employees = new ArrayList<>();
                    while (rs.next()){
                        employees.add(getEmpWithoutChain(rs, 0));
                    }
                    return page(employees, paging);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public List<Employee> getAllSortByDepartmentNameAndLastname(Paging paging) {
                try {
                    ResultSet rs = getRS("SELECT * FROM EMPLOYEE ORDER BY DEPARTMENT,LASTNAME");
                    List<Employee> employees = new ArrayList<>();
                    while (rs.next()){
                        employees.add(getEmpWithoutChain(rs, 0));
                    }
                    return page(employees, paging);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public List<Employee> getByDepartmentSortByHireDate(Department department, Paging paging) {
                try {
                    ResultSet rs = getRS("SELECT * FROM EMPLOYEE WHERE DEPARTMENT = "+department.getId()+ " ORDER BY HIREDATE");
                    List<Employee> employees = new ArrayList<>();
                    while (rs.next()){
                        employees.add(getEmpWithoutChain(rs, 0));
                    }
                    return page(employees, paging);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public List<Employee> getByDepartmentSortBySalary(Department department, Paging paging) {
                try {
                    ResultSet rs = getRS("SELECT * FROM EMPLOYEE WHERE DEPARTMENT = "+department.getId()+ " ORDER BY SALARY");
                    List<Employee> employees = new ArrayList<>();
                    while (rs.next()){
                        employees.add(getEmpWithoutChain(rs, 0));
                    }
                    return page(employees, paging);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public List<Employee> getByDepartmentSortByLastname(Department department, Paging paging) {
                try {
                    ResultSet rs = getRS("SELECT * FROM EMPLOYEE WHERE DEPARTMENT = "+department.getId()+ " ORDER BY LASTNAME");
                    List<Employee> employees = new ArrayList<>();
                    while (rs.next()){
                        employees.add(getEmpWithoutChain(rs, 0));
                    }
                    return page(employees, paging);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public List<Employee> getByManagerSortByLastname(Employee manager, Paging paging) {
                try {
                    ResultSet rs = getRS("SELECT * FROM EMPLOYEE WHERE MANAGER = "+manager.getId()+ " ORDER BY LASTNAME");
                    List<Employee> employees = new ArrayList<>();
                    while (rs.next()){
                        employees.add(getEmpWithoutChain(rs, 0));
                    }
                    return page(employees, paging);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public List<Employee> getByManagerSortByHireDate(Employee manager, Paging paging) {
                try {
                    ResultSet rs = getRS("SELECT * FROM EMPLOYEE WHERE MANAGER = "+manager.getId()+ " ORDER BY HIREDATE");
                    List<Employee> employees = new ArrayList<>();
                    while (rs.next()){
                        employees.add(getEmpWithoutChain(rs, 0));
                    }
                    return page(employees, paging);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public List<Employee> getByManagerSortBySalary(Employee manager, Paging paging) {
                try {
                    ResultSet rs = getRS("SELECT * FROM EMPLOYEE WHERE MANAGER = "+manager.getId()+ " ORDER BY SALARY");
                    List<Employee> employees = new ArrayList<>();
                    while (rs.next()){
                        employees.add(getEmpWithoutChain(rs, 0));
                    }
                    return page(employees, paging);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public Employee getWithDepartmentAndFullManagerChain(Employee employee) {
                ResultSet rs = null;
                try {
                    rs = getRS("SELECT * FROM EMPLOYEE");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                List<Employee> employees = getEmployeesWithChain(rs);
                for (Employee e : employees){
                    if (e.getId().equals(employee.getId()))
                        return e;
                }
                return null;
            }

            @Override
            public Employee getTopNthBySalaryByDepartment(int salaryRank, Department department) {
                try {
                    ResultSet rs = getRS("SELECT * FROM EMPLOYEE WHERE DEPARTMENT = "+department.getId()+ " ORDER BY SALARY DESC");
                    List<Employee> employees = new ArrayList<>();
                    while (rs.next()){
                        employees.add(getEmpWithoutChain(rs, 0));
                    }
                    return employees.get(salaryRank -1 );
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    List<Employee> getEmployeesWithChain(ResultSet resultSet){
        List<Employee> employees = new ArrayList<>();
        while (true){
            try {
                if (!resultSet.next()) break;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            employees.add(getEmpWithChain(resultSet));
        }
        return employees;
    }

    private Employee getEmpWithChain(ResultSet res) {
        try {
            BigInteger id = new BigInteger(res.getString("id"));
            String first = res.getString("firstname");
            String last = res.getString("lastname");
            String middle = res.getString("middlename");
            FullName full = new FullName(first, last, middle);
            Position position = Position.valueOf(res.getString("position"));
            LocalDate hire = LocalDate.parse(res.getString("hiredate"));
            BigDecimal salary = new BigDecimal(res.getString("SALARY"));
            Department department = getDepartmentById(res.getString("DEPARTMENT"));
            Employee manager = null;
            if (res.getString("MANAGER") != null){
                String manID = res.getString("MANAGER");
                int row = res.getRow();
                res.beforeFirst();
                while (res.next()){
                    if (res.getString("ID").equals(manID)){
                        manager = getEmpWithChain(res);
                        break;
                    }
                }
                res.absolute(row);
            }
            return new Employee(id, full, position, hire, salary, manager, department);
        } catch (SQLException e) {
            return null;
        }
    }

}