/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FacultyAdvisement;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 *
 * @author abilb
 */
public class StudentRepository {

    public static void create(DataSource ds, Student student) throws SQLException {
        String studentSQL = "INSERT INTO STUDENT(STUID, EMAIL, FIRSTNAME, LASTNAME, MAJORCODE, PHONE, ADVISED) "
                + "VALUES (?, ?, ?, ?, ?, ?, \'false\')";
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
            sqlStatement.setString(3, student.getFirstName());
            sqlStatement.setString(4, student.getLastName());
            sqlStatement.setString(5, student.getMajorCode());
            sqlStatement.setString(6, student.getPhoneNumber());

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
                s.setFirstName(result.getString("FIRSTNAME"));
                s.setLastName(result.getString("LASTNAME"));
                s.setMajorCode(result.getString("MAJORCODE"));
                s.setPhoneNumber(result.getString("PHONE"));
                if (result.getString("ADVISED").equals("true")) {
                    s.setAdvised(true);
                } else {
                    s.setAdvised(false);
                }
                list.put(s.getId(), s);
            }

        } finally {
            conn.close();
        }

        return list;
    }
    
    public static void adminUpdate(DataSource ds, Student student, String oldUsername) throws SQLException {
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {

            PreparedStatement ps;
            ps = conn.prepareStatement(
                    "Update STUDENT set EMAIL=?, FIRSTNAME=?, LASTNAME=?, MAJORCODE=?, PHONE=?, ADVISED=? where STUID=?"
            );
            ps.setString(1, student.getUsername());
            ps.setString(2, student.getFirstName());
            ps.setString(3, student.getLastName());
            ps.setString(4, student.getMajorCode());
            ps.setString(5, student.getPhoneNumber());
            if (student.isAdvised()) {
                ps.setString(4, "true");
            } else {
                ps.setString(4, "false");
            }
            ps.setString(5, student.getId());
            ps.executeUpdate();

            ps = conn.prepareStatement(
                    "Update USERTABLE set USERNAME=? where USERNAME=?"
            );
            ps.setString(1, student.getUsername());
            ps.setString(2, oldUsername);
            ps.executeUpdate();

            ps = conn.prepareStatement(
                    "Update GROUPTABLE set USERNAME=? where USERNAME=?"
            );
            ps.setString(1, student.getUsername());
            ps.setString(2, oldUsername);
            ps.executeUpdate();

            if (student.isResetPassword()) {
                String newPassword = UUID.randomUUID().toString();
                String encryptedPassword = SHA256Encrypt.encrypt(newPassword);
                ps = conn.prepareStatement(
                        "Update USERTABLE set PASSWORD=? where USERNAME=?"
                );
                ps.setString(1, encryptedPassword);
                ps.setString(2, student.getUsername());
                ps.executeUpdate();

                Email email = new HtmlEmail();
                email.setHostName("smtp.googlemail.com");
                email.setSmtpPort(465);
                email.setAuthenticator(new DefaultAuthenticator("uco.faculty.advisement", "!@#$1234"));
                email.setSSLOnConnect(true);
                email.setFrom("uco.faculty.advisement@gmail.com");
                email.setSubject("UCO Faculty Advisement Password Change");
                email.setMsg(
                        "<font size=\"3\">An admin has resetted your password, your new password is \"" + newPassword + "\"."
                        + "\n<p align=\"center\">UCO Faculty Advisement</p></font>"
                );
                email.addTo(student.getUsername());
                email.send();

            }

        } catch (EmailException ex) {
            Logger.getLogger(StudentRepository.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            conn.close();
        }

        //students = (HashMap<String, StudentPOJO>) readAll(); // reload the updated info
    }

    public static void update(DataSource ds, Student student) throws SQLException {
        String studentSQL = "UPDATE STUDENT SET STUID = ?, FIRSTNAME = ?, LASTNAME = ?, MAJORCODE = ?, PHONE = ? WHERE EMAIL = ?";

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            //Student Information
            PreparedStatement sqlStatement = conn.prepareStatement(studentSQL);
            sqlStatement.setString(1, student.getId());
            sqlStatement.setString(2, student.getFirstName());
            sqlStatement.setString(3, student.getLastName());
            sqlStatement.setString(4, student.getMajorCode());
            sqlStatement.setString(5, student.getPhoneNumber());
            sqlStatement.setString(6, student.getUsername());

            sqlStatement.executeUpdate();

        } finally {
            conn.close();
        }
    }

    public static void updatePassword(DataSource ds, String username, String password) throws SQLException {

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        String newPassword = SHA256Encrypt.encrypt(password);
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "Update USERTABLE set PASSWORD=? where USERNAME=?"
            );
            ps.setString(1, newPassword);
            ps.setString(2, username);

            ps.executeUpdate();

        } finally {
            conn.close();
        }
    }

    public static void delete(DataSource ds, Student student) throws SQLException {
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
                student.setFirstName(result.getString("firstname"));
                student.setLastName(result.getString("lastname"));
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

    public static Student readById(DataSource ds, String key) throws SQLException {
        String studentSQL = "SElECT * FROM STUDENT WHERE STUID = ?";
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
                student.setId(key);
                student.setFirstName(result.getString("firstname"));
                student.setLastName(result.getString("lastname"));
                student.setMajorCode(result.getString("majorcode"));
                student.setPhoneNumber(result.getString("phone"));
                student.setUsername(result.getString("email"));

            }

        } finally {
            conn.close();
        }

        return student;
    }

    public static String getPicture(DataSource ds, String key) throws SQLException {

        Blob image = null;

        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM USERTABLE WHERE USERNAME = ?"
            );
            ps.setString(1, key);
            ResultSet result = ps.executeQuery();
            while (result.next()) {
                image = result.getBlob("IMAGE");
            }
        } finally {
            conn.close();
        }
        
        if (image != null) {
            return "ImageServlet?username=" + key;
        } else {
            return "/resources/default-image.png";
        }
    }
    
    public static void updateAllToNotAdvised(DataSource ds) throws SQLException{
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "update student set advised = \'false\'"
            );
            
            ps.execute();
            
        } finally {
            conn.close();
        }
    }
    
}
