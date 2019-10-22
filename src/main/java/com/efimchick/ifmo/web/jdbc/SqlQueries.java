package com.efimchick.ifmo.web.jdbc;

/**
 * Implement sql queries like described
 */
public class SqlQueries {
    //Select all employees sorted by last name in ascending order
    //language=HSQLDB
    public String select01 = "SELECT * FROM EMPLOYEE ORDER BY LASTNAME ASC";

    //Select employees having no more than 5 characters in last name sorted by last name in ascending order
    //language=HSQLDB
    public String select02 = "SELECT * FROM EMPLOYEE WHERE LENGTH(LASTNAME) <=5 ORDER BY LASTNAME ASC";

    //Select employees having salary no less than 2000 and no more than 3000
    //language=HSQLDB
    public String select03 = "SELECT * FROM EMPLOYEE WHERE SALARY BETWEEN 2000 AND 3000";

    //Select employees having salary no more than 2000 or no less than 3000
    //language=HSQLDB
    public String select04 = "SELECT * FROM EMPLOYEE WHERE SALARY <= 2000 OR SALARY >= 3000";

    //Select employees assigned to a department and corresponding department name
    //language=HSQLDB
    public String select05 = "SELECT * FROM EMPLOYEE emp INNER JOIN DEPARTMENT D on emp.DEPARTMENT = D.ID";

    //Select all employees and corresponding department name if there is one.
    //Name column containing name of the department "depname".
    //language=HSQLDB
    public String select06 = "SELECT emp.*, D.NAME as depname "+
            "FROM EMPLOYEE AS emp LEFT OUTER JOIN DEPARTMENT AS D ON emp.DEPARTMENT = D.ID";
    //Select total salary pf all employees. Name it "total".
    //language=HSQLDB
    public String select07 = "SELECT SUM(SALARY) AS total FROM EMPLOYEE";

    //Select all departments and amount of employees assigned per department
    //Name column containing name of the department "depname".
    //Name column containing employee amount "staff_size".
    //language=HSQLDB
    public String select08 = "SELECT D.NAME AS depname, COUNT(emp.ID) AS staff_size "+
            "FROM DEPARTMENT D INNER JOIN EMPLOYEE emp on emp.DEPARTMENT = D.id "+
            "GROUP BY D.NAME";

    //Select all departments and values of total and average salary per department
    //Name column containing name of the department "depname".
    //language=HSQLDB
    public String select09 = "SELECT D.NAME AS depname, SUM(emp.SALARY) AS total, AVG(emp.SALARY) AS average "+
            "FROM DEPARTMENT D INNER JOIN EMPLOYEE emp on emp.DEPARTMENT = D.id "+
            "GROUP BY D.NAME";

    //Select all employees and their managers if there is one.
    //Name column containing employee lastname "employee".
    //Name column containing manager lastname "manager".
    //language=HSQLDB
    public String select10 = "SELECT EMP.LASTNAME AS employee, MANAGER.LASTNAME AS manager "+
            "FROM EMPLOYEE EMP LEFT JOIN EMPLOYEE MANAGER ON EMP.manager=MANAGER.id";


}
