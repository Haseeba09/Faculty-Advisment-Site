/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FacultyAdvisement;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.sql.DataSource;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 *
 * @author Hasee
 */
@Named(value = "imagineBean")
@SessionScoped
public class ImagineBean implements Serializable {

    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;
    private List<Course> currentCourses;
    private String username;
    private Student student = new Student();
    
       
    @PostConstruct
    public void init() {
         FacesContext fc = FacesContext.getCurrentInstance();
        Principal p = fc.getExternalContext().getUserPrincipal();
        username = p.getName();
        
        try {
            student = StudentRepository.read(ds, username);
            currentCourses = CurrentCourseRepository.readCurrentCourses(ds, student.getId());
        } catch (SQLException ex) {
            Logger.getLogger(AppointmentBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void readCurrentCourses() throws SQLException{
        currentCourses = CurrentCourseRepository.readCurrentCourses(ds, student.getId());
    }
    
    public void addCurrentCourse(Course course) throws SQLException{
        CurrentCourseRepository.createCurrentCourse(ds, student.getId(), course.getSubject(), course.getNumber());
        currentCourses = CurrentCourseRepository.readCurrentCourses(ds, student.getId());
    }
    
    public void deleteCurrentCourse(Course course) throws SQLException{
        CurrentCourseRepository.deleteCurrentCourse(ds, student.getId(), course.getSubject(), course.getNumber());
        currentCourses = CurrentCourseRepository.readCurrentCourses(ds, student.getId());
    }
    
    public String submitRequest() {
        
        String emailCourses = "";
        
        for(Course c: currentCourses){
            emailCourses += "<li>" + c.getSubject() + " " + c.getNumber() +"</li>";
        }
    
        try {
                    Email email = new HtmlEmail();
                    email.setHostName("smtp.googlemail.com");
                    email.setSmtpPort(465);
                    email.setAuthenticator(new DefaultAuthenticator("uco.faculty.advisement", "!@#$1234"));
                    email.setSSLOnConnect(true);
                    email.setFrom("uco.faculty.advisement@gmail.com");
                    email.setSubject("Microsoft Imagine Account");
                    email.setMsg(
                            "<font size=\"3\" style=\"font-family:verdana\"> \n"
                                    + "<ul><li>Student Name: "+ student.getFirstName() +" "+ student.getLastName() +"</li><li>Student Major: " + student.getMajorCode() + "<li>Current Courses: <ol>"+emailCourses+"</ol></li></ul> "
                                            + "Student Email if needed for response: " + student.getUsername()
                            + "\n<p align=\"center\">UCO Faculty Advisement</p></font>"
                    );
                    email.addTo("uco.faculty.advisement@gmail.com");
                    email.send();
                } catch (EmailException ex) {
                    Logger.getLogger(VerificationBean.class.getName()).log(Level.SEVERE, null, ex);
                }
        
        return "/customerFolder/imagineConfirm";
    }

    public List<Course> getCurrentCourses() {
        return currentCourses;
    }

    public void setCurrentCourses(List<Course> completedCourses) {
        this.currentCourses = completedCourses;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
    
    
    
    
}
