package FacultyAdvisement;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.sql.DataSource;

@Named(value = "userBean")
@SessionScoped
public class UserBean implements Serializable {

    private String username;
    private Student student;
    private String oldId;
    private CourseWithRequisites courseWithRequisites;
    private List<Course> completedCourses;
    private List<Course> availableCourses;
    private List<Course> desiredCoureses;

    public List<Course> getDesiredCoureses() {
        return desiredCoureses;
    }

    public void setDesiredCoureses(List<Course> desiredCoureses) {
        this.desiredCoureses = desiredCoureses;
    }

    public CourseWithRequisites getCourseWithRequisites() {
        return courseWithRequisites;
    }

    public void setCourseWithRequisites(CourseWithRequisites courseWithRequisites) {
        this.courseWithRequisites = courseWithRequisites;
    }

    public List<Course> getAvailableCourses() {
        return availableCourses;
    }

    public void setAvailableCourses(List<Course> availableCourses) {
        this.availableCourses = availableCourses;
    }

    public List<Course> getCompletedCourses() {
        return completedCourses;
    }

    public void setCompletedCourses(List<Course> completedCourses) {
        this.completedCourses = completedCourses;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Principal p = fc.getExternalContext().getUserPrincipal();
        username = p.getName();
        student = new Student();
        desiredCoureses = new ArrayList<>();
        try {
            student = StudentRepository.read(ds, username);
        } catch (SQLException ex) {
            Logger.getLogger(UserBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            completedCourses = CourseRepository.readCompletedCourses(ds, student.getId());
        } catch (SQLException ex) {
            Logger.getLogger(UserBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            availableCourses = CourseRepository.readAllCourses(ds);
        } catch (SQLException ex) {
            Logger.getLogger(UserBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        oldId = student.getId();
    }

    public String getUsername() {
        return username;
    }

    public String getPicture(String key) throws SQLException {
        return StudentRepository.getPicture(ds, key);
    }
    
    public String validateForm() throws SQLException {

        boolean flag = true;

        Pattern pattern;
        Matcher matcher;

        pattern = Pattern.compile("[1-9]{1}[0-9]{7}");
        matcher = pattern.matcher(student.getId());
        if (!matcher.matches()) {
            FacesMessage facesMsg = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Enter a valid UCO ID number!", null);
            FacesContext.getCurrentInstance().addMessage(
                    "studentForm:id", facesMsg);
            flag = false;
        } else {
            Connection conn = ds.getConnection();
            if (conn == null) {
                throw new SQLException("conn is null; Can't get db connection");
            }
            try {
                PreparedStatement ps = conn.prepareStatement(
                        "select * from STUDENT"
                );
                ResultSet result = ps.executeQuery();
                while (result.next()) {
                    if (result.getString("STUID").equals(student.getId()) && !oldId.equals(student.getId())) {
                        FacesMessage facesMsg = new FacesMessage(
                                FacesMessage.SEVERITY_ERROR,
                                "UCO ID number is already taken!", null);
                        FacesContext.getCurrentInstance().addMessage(
                                "studentForm:id", facesMsg);
                        flag = false;
                        break;
                    }
                }
            } finally {
                conn.close();
            }
        }

        pattern = Pattern.compile("[0-9]{10}");
        matcher = pattern.matcher(student.getPhoneNumber());
        if (!matcher.matches()) {
            FacesMessage facesMsg = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Phone must be 10 numbers long.", null);
            FacesContext.getCurrentInstance().addMessage(
                    "studentForm:phoneNumber", facesMsg);
            flag = false;
        }

        if (flag == true) {

            try {
                StudentRepository.update(ds, student);
                oldId = student.getId();
                student.setEdit(false);
            } catch (SQLException ex) {
                Logger.getLogger(UserBean.class.getName()).log(Level.SEVERE, null, ex);
            }

            FacesMessage facesMsg = new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    "User Information Updated", null);
            FacesContext.getCurrentInstance().addMessage(
                    "studentForm:save", facesMsg);

            return null;
        }

        FacesMessage facesMsg = new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "Something went wrong!", null);
        FacesContext.getCurrentInstance().addMessage(
                "studentForm:save", facesMsg);
        student.setEdit(true);

        return null;
    }

    public void removeCompletedCourse(Course course) throws SQLException {
        completedCourses.remove(course);
        CourseRepository.deleteCompleted(ds, student.getId(), course.subject, course.number);
        availableCourses = CourseRepository.readAllCourses(ds);
    }

    public void addCompletedCourse(Course course) throws SQLException {
        completedCourses.add(course);
        CourseRepository.createCompletedCourse(ds, student.getId(), course.subject, course.number);
        availableCourses = CourseRepository.readAllCourses(ds);
    }

    public void lookupCourse(Course course) throws SQLException {
        this.setCourseWithRequisites(CourseRepository.readCourseWithRequisites(ds, course));

    }

    public void addToDesired() {
        if (this.courseWithRequisites == null) {
            FacesContext.getCurrentInstance().addMessage("courseinfo:desired",
                    new FacesMessage(FacesMessage.SEVERITY_FATAL,
                            "Please select a course!", null));
            return;
        }

        boolean flag = true;
        for (int i = 0; i < this.courseWithRequisites.getPrerequisites().size(); i++) {
            flag = false;
            for (int j = 0; j < this.completedCourses.size(); j++) {
                if (this.courseWithRequisites.getPrerequisites().get(i).getName() == null ? this.completedCourses.get(j).getName() == null : this.courseWithRequisites.getPrerequisites().get(i).getName().equals(this.completedCourses.get(j).getName())) {
                    flag = true;
                }
            }
        }

        if (flag == false) {
            FacesContext.getCurrentInstance().addMessage("courseinfo:desired",
                    new FacesMessage(FacesMessage.SEVERITY_FATAL,
                            "You are missing a prerequisite course!", null));

            return;
        }

        for (int i = 0; i < this.courseWithRequisites.getPrerequisites().size(); i++) {
            if (!this.desiredCoureses.contains(this.courseWithRequisites.getPrerequisites().get(i))) {
                flag = false;
            }
        }
        if (this.courseWithRequisites.getCorequisite().isEmpty()) {
            flag = true;
        }
        if (flag == false) {
            FacesContext.getCurrentInstance().addMessage("courseinfo:desired",
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "You are missing a corequisite course!", null));

        }

        for (int i = 0; i < this.courseWithRequisites.getPrerequisites().size(); i++) {
            if (!this.desiredCoureses.contains(this.courseWithRequisites.getPrerequisites().get(i)) || this.completedCourses.contains(this.courseWithRequisites.getPrerequisites().get(i))) {
                flag = false;
            }
        }

        if (this.courseWithRequisites.getSuggested().isEmpty()) {
            flag = true;
        }

        if (flag == false) {
            FacesContext.getCurrentInstance().addMessage("courseinfo:desired",
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Consider taking the suggested courses!", null));

        }

        this.desiredCoureses.add(this.courseWithRequisites.getCourse());

    }
}
