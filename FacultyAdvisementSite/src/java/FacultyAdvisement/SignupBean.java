/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FacultyAdvisement;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.sql.DataSource;

/**
 *
 * @author abilb
 */
@Named(value = "signupBean")
@SessionScoped
public class SignupBean implements Serializable{
    
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    
    private String username;
    private Student student;
    private CourseWithRequisites courseWithRequisites;  
    private List<Course> completedCourses; 
    private List<Course> availableCourses;
    private List<Course> desiredCoureses; 
    private Appointment appointment;

   
    
    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Principal p = fc.getExternalContext().getUserPrincipal();
        username = p.getName();
        student = new Student();
        // appointment = p.getAppointment(); 
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
           //TODO: Filter Completed Courses out of Available Courses
            availableCourses = CourseRepository.readAllCourses(ds);
        } catch (SQLException ex) {
            Logger.getLogger(UserBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
     
    public void removeCompletedCourse(Course course) throws SQLException
    {
        completedCourses.remove(course);
        CourseRepository.deleteCompleted(ds, student.getId(), course.subject, course.number);
         availableCourses = CourseRepository.readAllCourses(ds);
    }
    
    public void addCompletedCourse(Course course) throws SQLException
    {
        completedCourses.add(course);
        CourseRepository.createCompletedCourse(ds, student.getId(), course.subject, course.number);
       availableCourses = CourseRepository.readAllCourses(ds);
    }
    
    public void lookupCourse(Course course) throws SQLException
    {
        this.setCourseWithRequisites(CourseRepository.readCourseWithRequisites(ds, course));
    
    }
    
    public void addToDesired()
    {
        if(this.courseWithRequisites == null)
        {
         FacesContext.getCurrentInstance().addMessage("courseinfo:desired",
                    new FacesMessage(FacesMessage.SEVERITY_FATAL,
                            "Please select a course!", null));
                                return;
        }
        
        boolean flag = true;
        for(int i = 0; i < this.courseWithRequisites.getPrerequisites().size(); i++)
        {
            flag = false;
            for(int j = 0; j < this.completedCourses.size(); j++)
            {
                if(this.courseWithRequisites.getPrerequisites().get(i).getName() == null ? this.completedCourses.get(j).getName() == null : this.courseWithRequisites.getPrerequisites().get(i).getName().equals(this.completedCourses.get(j).getName()))
                {
                    flag = true;
                }
            }
        }
        
        if(flag == false)
        {
             FacesContext.getCurrentInstance().addMessage("courseinfo:desired",
                    new FacesMessage(FacesMessage.SEVERITY_FATAL,
                            "You are missing a prerequisite course!", null));
             
             return;
        }
        
        for(int i = 0; i < this.courseWithRequisites.getPrerequisites().size(); i++)
        {
            if(!this.desiredCoureses.contains(this.courseWithRequisites.getPrerequisites().get(i)))
            {
                flag = false;
            }
        }
        if(this.courseWithRequisites.getCorequisite().isEmpty())
        {
            flag = true;
        }
        if(flag == false)
        {
             FacesContext.getCurrentInstance().addMessage("courseinfo:desired",
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "You are missing a corequisite course!", null));
             
              
        }
        
        for(int i = 0; i < this.courseWithRequisites.getPrerequisites().size(); i++)
        {
            if(!this.desiredCoureses.contains(this.courseWithRequisites.getPrerequisites().get(i)) || this.completedCourses.contains(this.courseWithRequisites.getPrerequisites().get(i)))
            {
                flag = false;
            }
        }
        
        if(this.courseWithRequisites.getSuggested().isEmpty())
        {
            flag = true; 
        }
        
         if(flag == false)
        {
             FacesContext.getCurrentInstance().addMessage("courseinfo:desired",
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Consider taking the suggested courses!", null));
             
             
        }
         
        this.desiredCoureses.add(this.courseWithRequisites.getCourse());
        
    }
    
    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    } 
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public CourseWithRequisites getCourseWithRequisites() {
        return courseWithRequisites;
    }

    public void setCourseWithRequisites(CourseWithRequisites courseWithRequisites) {
        this.courseWithRequisites = courseWithRequisites;
    }

    public List<Course> getCompletedCourses() {
        return completedCourses;
    }

    public void setCompletedCourses(List<Course> completedCourses) {
        this.completedCourses = completedCourses;
    }

    public List<Course> getAvailableCourses() {
        return availableCourses;
    }

    public void setAvailableCourses(List<Course> availableCourses) {
        this.availableCourses = availableCourses;
    }

    public List<Course> getDesiredCoureses() {
        return desiredCoureses;
    }

    public void setDesiredCoureses(List<Course> desiredCoureses) {
        this.desiredCoureses = desiredCoureses;
    }

    
}
