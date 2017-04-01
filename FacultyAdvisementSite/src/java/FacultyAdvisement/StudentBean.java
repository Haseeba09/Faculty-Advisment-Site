package FacultyAdvisement;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.validation.constraints.Pattern;

/**
 *
 * @author violentsushi
 */
@Named(value = "studentBean")
@SessionScoped
public class StudentBean implements Serializable {

    // resource injection
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    private String sid;
    @Pattern(regexp = "[a-zA-Z0-9]{3,20}@uco.edu", message = "Local-part of email must be 3 to 20 alphanumeric characters long and end with @uco.edu")
    private String email;
    private String major;
    @Pattern(regexp = "[0-9]{10}", message = "Phone must be 10 numbers long.")
    private String phone;
    private boolean resetPassword;

    private HashMap<String, Student> students = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            students = (HashMap<String, Student>) readAll();
        } catch (SQLException ex) {
            Logger.getLogger(StudentBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isResetPassword() {
        return resetPassword;
    }

    public void setResetPassword(boolean resetPassword) {
        this.resetPassword = resetPassword;
    }

    public HashMap<String, Student> getStudents() {
        return students;
    }

    public void edit(String key) {
        Student s = students.get(key);
        this.sid = s.getId();
        this.email = s.getUsername();
        this.major = s.getMajorCode();
        this.phone = s.getPhoneNumber();
        this.resetPassword = s.isResetPassword();
    }

    public Map readAll() throws SQLException {
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

    public void update(String key) throws SQLException {
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {
            Student s = students.get(key);
            PreparedStatement ps;
            ps = conn.prepareStatement(
                    "Update STUDENT set EMAIL=?, MAJORCODE=?, PHONE=? where STUID=?"
            );
            ps.setString(1, this.email);
            ps.setString(2, this.major);
            ps.setString(3, this.phone);
            ps.setString(4, this.sid);
            ps.executeUpdate();
            ps = conn.prepareStatement(
                    "Update USERTABLE set USERNAME=? where USERNAME=?"
            );
            ps.setString(1, this.email);
            ps.setString(2, s.getUsername());
            ps.executeUpdate();
            ps = conn.prepareStatement(
                    "Update GROUPTABLE set USERNAME=? where USERNAME=?"
            );
            ps.setString(1, this.email);
            ps.setString(2, s.getUsername());
            ps.executeUpdate();
            if (this.resetPassword) {
                String defaultPassword = "password";
                defaultPassword = SHA256Encrypt.encrypt(defaultPassword);
                ps = conn.prepareStatement(
                        "Update USERTABLE set PASSWORD=? where USERNAME=?"
                );
                ps.setString(1, defaultPassword);
                ps.setString(2, this.email);
                ps.executeUpdate();
            }
        } finally {
            conn.close();
        }

        students = (HashMap<String, Student>) readAll(); // reload the updated info
    }

    public void delete(String key) throws SQLException {
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {
            Student s = students.get(key);
            PreparedStatement ps;
            ps = conn.prepareStatement(
                    "Delete from STUDENT where EMAIL=?"
            );
            ps.setString(1, s.getUsername());
            ps.executeUpdate();
            ps = conn.prepareStatement(
                    "Delete from USERTABLE where USERNAME=?"
            );
            ps.setString(1, s.getUsername());
            ps.executeUpdate();
            ps = conn.prepareStatement(
                    "Delete from GROUPTABLE where USERNAME=?"
            );
            ps.setString(1, s.getUsername());
            ps.executeUpdate();
        } finally {
            conn.close();
        }

        students = (HashMap<String, Student>) readAll(); // reload the updated info
    }

}
