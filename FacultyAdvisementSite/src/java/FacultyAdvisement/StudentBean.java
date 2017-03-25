package FacultyAdvisement;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.model.SelectItem;
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

    private HashMap<String, StudentPOJO> students = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            students = (HashMap<String, StudentPOJO>) readAll();
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

    public List<SelectItem> getAvailableMajors() throws SQLException {

        List<SelectItem> list = new ArrayList<>();

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "select * from AVAILABLEMAJOR"
            );

            // retrieve customer data from database
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                String s = result.getString("MAJORCODE");
                list.add(new SelectItem(s));
            }

        } finally {
            conn.close();
        }

        return list;

    }

    public HashMap<String, StudentPOJO> getStudents() {
        return students;
    }

    public void edit(String key) {
        StudentPOJO s = students.get(key);
        this.sid = s.getSid();
        this.email = s.getEmail();
        this.major = s.getMajor();
        this.phone = s.getPhone();
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

        HashMap<String, StudentPOJO> list = new HashMap<>();

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "select * from STUDENT"
            );

            // retrieve customer data from database
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                StudentPOJO s = new StudentPOJO();
                s.setSid(result.getString("STUID"));
                s.setEmail(result.getString("EMAIL"));
                s.setMajor(result.getString("MAJORCODE"));
                s.setPhone(result.getString("PHONE"));
                list.put(s.getSid(), s);
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
            StudentPOJO s = students.get(key);
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
            ps.setString(2, s.getEmail());
            ps.executeUpdate();
            ps = conn.prepareStatement(
                    "Update GROUPTABLE set USERNAME=? where USERNAME=?"
            );
            ps.setString(1, this.email);
            ps.setString(2, s.getEmail());
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

        students = (HashMap<String, StudentPOJO>) readAll(); // reload the updated info
    }

    public void delete(String key) throws SQLException {
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {
            StudentPOJO s = students.get(key);
            PreparedStatement ps;
            ps = conn.prepareStatement(
                    "Delete from STUDENT where EMAIL=?"
            );
            ps.setString(1, s.getEmail());
            ps.executeUpdate();
            ps = conn.prepareStatement(
                    "Delete from USERTABLE where USERNAME=?"
            );
            ps.setString(1, s.getEmail());
            ps.executeUpdate();
            ps = conn.prepareStatement(
                    "Delete from GROUPTABLE where USERNAME=?"
            );
            ps.setString(1, s.getEmail());
            ps.executeUpdate();
        } finally {
            conn.close();
        }

        students = (HashMap<String, StudentPOJO>) readAll(); // reload the updated info
    }

}
