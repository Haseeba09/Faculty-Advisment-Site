package FacultyAdvisement;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
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
            students = (HashMap<String, Student>) StudentRepository.readAll(ds);
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

    public void update(String key) throws SQLException {
        Student student = new Student();
        student.setId(this.sid);
        student.setUsername(this.email);
        student.setMajorCode(this.major);
        student.setPhoneNumber(this.phone);
        student.setResetPassword(this.resetPassword);
        String oldUsername = students.get(key).getUsername();
        StudentRepository.adminUpdate(ds, student, oldUsername);
        students = (HashMap<String, Student>) StudentRepository.readAll(ds); // reload the updated info
    }

    public void delete(String key) throws SQLException {
        Student s = students.get(key);
        StudentRepository.delete(ds, s);
        students = (HashMap<String, Student>) StudentRepository.readAll(ds); // reload the updated info
    }

}
