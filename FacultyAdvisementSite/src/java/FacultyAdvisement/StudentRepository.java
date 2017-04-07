/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FacultyAdvisement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author abilb
 */
public class StudentRepository {

    public static void create(DataSource ds, Student student) throws SQLException {
        String studentSQL = "INSERT INTO STUDENT(STUID, EMAIL, MAJORCODE, PHONE, ADVISED) "
                + "VALUES (?, ?, ?, ?, \'false\')";
        String userSQL = "INSERT INTO USERTABLE(PASSWORD, USERNAME, VERIFIED) VALUES (?, ?, ?)"; //haseeb was here

        String groupSQL = "INSERT INTO GROUPTABLE(GROUPNAME, USERNAME) VALUES (\'customergroup\', ?)";

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {

            //Here we execute three SQL statements
            //Student Information
            PreparedStatement sqlStatement = conn.prepareStatement(studentSQL);

            sqlStatement.setString(1, student.getId());
            sqlStatement.setString(2, student.getUsername());
            sqlStatement.setString(3, student.getMajorCode());
            sqlStatement.setString(4, student.getPhoneNumber());

            sqlStatement.executeUpdate();

            //user credentials
            sqlStatement = conn.prepareStatement(userSQL);

            //Encrypt the pssword into SHA-256
            sqlStatement.setString(1, SHA256Encrypt.encrypt(student.getPassword()));

            sqlStatement.setString(2, student.getUsername());
            sqlStatement.setString(3, "false");
            sqlStatement.execute();

            //Group Table
            sqlStatement = conn.prepareStatement(groupSQL);
            sqlStatement.setString(1, student.getUsername());
            sqlStatement.execute();
        } finally {
            conn.close();
        }

    }

    public static Map readAll(DataSource ds) throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        HashMap<String, Student> list = new HashMap<>();

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "select * from STUDENT"
            );

            // retrieve customer data from database
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                Student s = new Student();
                s.setId(result.getString("STUID"));
                s.setUsername(result.getString("EMAIL"));
                s.setMajorCode(result.getString("MAJORCODE"));
                s.setPhoneNumber(result.getString("PHONE"));
                list.put(s.getId(), s);
            }

        } finally {
            conn.close();
        }

        return list;
    }

    public static void adminUpdate(String key, DataSource ds, Student student, boolean resetPassword) throws SQLException {
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {

            PreparedStatement ps;
            ps = conn.prepareStatement(
                    "Update STUDENT set EMAIL=?, MAJORCODE=?, PHONE=? where STUID=?"
            );
            ps.setString(1, student.getUsername());
            ps.setString(2, student.getMajorCode());
            ps.setString(3, student.getPhoneNumber());
            ps.setString(4, student.getId());
            ps.executeUpdate();
            ps = conn.prepareStatement(
                    "Update USERTABLE set USERNAME=? where USERNAME=?"
            );
            ps.setString(1, student.getUsername());
            ps.setString(2, student.getUsername());
            ps.executeUpdate();
            ps = conn.prepareStatement(
                    "Update GROUPTABLE set USERNAME=? where USERNAME=?"
            );
            ps.setString(1, student.getUsername());
            ps.setString(2, student.getUsername());
            ps.executeUpdate();
            if (resetPassword) {
                String defaultPassword = "password";
                defaultPassword = SHA256Encrypt.encrypt(defaultPassword);
                ps = conn.prepareStatement(
                        "Update USERTABLE set PASSWORD=? where USERNAME=?"
                );
                ps.setString(1, defaultPassword);
                ps.setString(2, student.getUsername());
                ps.executeUpdate();
            }
        } finally {
            conn.close();
        }

        //students = (HashMap<String, StudentPOJO>) readAll(); // reload the updated info
    }

    public static void update(DataSource ds, Student student) throws SQLException {
        String studentSQL = "UPDATE STUDENT SET STUID= ?, MAJORCODE = ?, PHONE = ? WHERE EMAIL = ?";
        String userSQL = "UPDATE USERTABLE SET PASSWORD = ? WHERE USERNAME = ?";

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {

            //Here we execute three SQL statements
            //Student Information
            PreparedStatement sqlStatement = conn.prepareStatement(studentSQL);
            //sqlStatement.setString(1, Integer.toString(books.size() + 1)); //Integer.toString(this.getBooks().size() + 1));
            sqlStatement.setString(1, student.getId());
            sqlStatement.setString(2, student.getMajorCode());
            sqlStatement.setString(3, student.getPhoneNumber());
            sqlStatement.setString(4, student.getUsername());

            sqlStatement.executeUpdate();

            //user credentials
            sqlStatement = conn.prepareStatement(userSQL);

            //Encrypt the pssword into SHA-256
            sqlStatement.setString(1, SHA256Encrypt.encrypt(student.getPassword()));

            sqlStatement.setString(2, student.getUsername());
            sqlStatement.execute();

        } finally {
            conn.close();
        }
    }

    public static void delete(String key, DataSource ds, Student student) throws SQLException {
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {

            PreparedStatement ps;
            ps = conn.prepareStatement(
                    "Delete from STUDENT where EMAIL=?"
            );
            ps.setString(1, student.getUsername());
            ps.executeUpdate();
            ps = conn.prepareStatement(
                    "Delete from USERTABLE where USERNAME=?"
            );
            ps.setString(1, student.getUsername());
            ps.executeUpdate();
            ps = conn.prepareStatement(
                    "Delete from GROUPTABLE where USERNAME=?"
            );
            ps.setString(1, student.getUsername());
            ps.executeUpdate();
        } finally {
            conn.close();
        }

        //students = (HashMap<String, StudentPOJO>) readAll(); // reload the updated info
    }

    public static Student read(DataSource ds, String key) throws SQLException {
        String studentSQL = "SElECT * FROM STUDENT JOIN USERTABLE on EMAIL = USERNAME WHERE EMAIL = ?";
        Student student = new Student();

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {

            PreparedStatement sqlStatement = conn.prepareStatement(studentSQL);

            sqlStatement.setString(1, key);

            ResultSet result = sqlStatement.executeQuery();
            while (result.next()) {
                student.setId(result.getString("STUID"));
                student.setMajorCode(result.getString("majorcode"));
                student.setPhoneNumber(result.getString("phone"));
                student.setUsername(key);
                student.setPassword(result.getString("password"));
            }

        } finally {
            conn.close();
        }

        return student;
    }

}
