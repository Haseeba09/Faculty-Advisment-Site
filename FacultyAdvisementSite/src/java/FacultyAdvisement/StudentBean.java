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
    @Pattern(regexp = "[a-zA-Z]{2,60}", message = "Name must be 2-60 characters.")
    private String firstName;
    @Pattern(regexp = "[a-zA-Z]{2,60}", message = "Name must be 2-60 characters.")
    private String lastName;
    private String major;
    @Pattern(regexp = "[0-9]{10}", message = "Phone must be 10 numbers long.")
    private String phone;
    private boolean advised;
    private boolean resetPassword;
    private boolean editing;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public boolean isAdvised() {
        return advised;
    }

    public void setAdvised(boolean advised) {
        this.advised = advised;
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

    public String isAdvisedOutput(String key) {
        Student s = students.get(key);
        if (s.isAdvised()) {
            return "Yes";
        } else {
            return "No";
        }
    }

    public void edit(String key) {
        Student s = students.get(key);
        this.sid = s.getId();
        this.email = s.getUsername();
        this.firstName = s.getFirstName();
        this.lastName = s.getLastName();
        this.major = s.getMajorCode();
        this.phone = s.getPhoneNumber();
        this.advised = s.isAdvised();
        this.resetPassword = s.isResetPassword();
        setEditing(true);
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        this.editing = editing;
    }

    public String getPicture(String key) throws SQLException {
        return StudentRepository.getPicture(ds, key);
    }

    public void update(String key) throws SQLException {
        Student student = new Student();
        student.setId(this.sid);
        student.setUsername(this.email);
        student.setFirstName(this.firstName);
        student.setLastName(this.lastName);
        student.setMajorCode(this.major);
        student.setPhoneNumber(this.phone);
        student.setAdvised(this.advised);
        student.setResetPassword(this.resetPassword);
        String oldUsername = students.get(key).getUsername();
        StudentRepository.adminUpdate(ds, student, oldUsername);
        students = (HashMap<String, Student>) StudentRepository.readAll(ds); // reload the updated info
        setEditing(false);
    }

    public void delete(String key) throws SQLException {
        Student s = students.get(key);
        StudentRepository.delete(ds, s);
        students = (HashMap<String, Student>) StudentRepository.readAll(ds); // reload the updated info
    }

    public void setAllStudentsToNotAdvised() throws SQLException {
        /*
        students = (HashMap<String, Student>) StudentRepository.readAll(ds);
        for(int i = 0; i < students.size(); i++){
            students.get(i).setAdvised(false);
            StudentRepository.adminUpdate(ds, students.get(i), students.get(i).getUsername());
        }
         */
        StudentRepository.updateAllToNotAdvised(ds);
        students = (HashMap<String, Student>) StudentRepository.readAll(ds);

    }

    public void markStudentAsAdvised(Student student) throws SQLException {
        student.setAdvised(true);
        StudentRepository.adminUpdate(ds, student, student.getUsername());

        students = (HashMap<String, Student>) StudentRepository.readAll(ds);

    }

}
