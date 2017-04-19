/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FacultyAdvisement;

import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 *
 * @author abilb
 */
@Named(value = "signupBean")
@ViewScoped
public class SignupBean implements Serializable{
    
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    @Inject AppointmentBean appointmentBean;
    
    
    private String username;
    private Student student;
    private CourseWithRequisites courseWithRequisites;  
    private List<Course> completedCourses; 
    private List<Course> availableCourses;
    private List<Course> desiredCoureses; 
    private Appointment appointment;
    private boolean edit; 

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }
   
    
    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Principal p = fc.getExternalContext().getUserPrincipal();
        username = p.getName();
        student = new Student();
        this.appointment = appointmentBean.getAppointment();
        desiredCoureses = new ArrayList<>();
        
            
        try
        {
            desiredCoureses = DesiredCourseRepository.readDesiredCourses(ds, Long.toString(appointment.aID));
        } catch (SQLException ex) {
            Logger.getLogger(SignupBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
       
        
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
   
    public void lookupCourse(Course course) throws SQLException
    {
        this.setCourseWithRequisites(CourseRepository.readCourseWithRequisites(ds, course));
    
    }
    
    public void addToDesired()
    {
        if(this.desiredCoureses != null && courseWithRequisites != null)
        {
             
             this.desiredCoureses.add(this.courseWithRequisites.getCourse());
       
        }
        
    }
    
    public void removeFromDesired(Course course)
    {
    
        this.desiredCoureses.remove(course);
    }
    
    public boolean checkRequsites(CourseWithRequisites course)
    {
    
         try {
           completedCourses = CourseRepository.readCompletedCourses(ds, student.getId());
        } catch (SQLException ex) {
            Logger.getLogger(UserBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         
        
         
        //Check for Prerquisites
        
        //Flag
        boolean flag = true;
        
        //Iterate through prerequisites. 
        if(course.getPrerequisites() != null)
        {
             for(int i = 0; i < course.getPrerequisites().size(); i++)
            {
                flag = false;
                if(completedCourses != null)
                    {
                    for(int j = 0; j < this.completedCourses.size(); j++)
                        {
                            if(this.completedCourses.get(j).compare(course.getPrerequisites().get(i)))
                            {
                                flag = true;
                            }
                        }
                    }
            
            
            }
        }
       
        
        if(flag == false)
        {
             FacesContext.getCurrentInstance().addMessage("desiredCourses:Submit",
                    new FacesMessage(FacesMessage.SEVERITY_FATAL,
                            "You are missing a prerequisite course!", null));
             
             return false;
        }
        
        if(course.getCorequisite().isEmpty())
        {
            flag = true;
        }
        else
        {
            for(int i = 0; i < course.getCorequisite().size(); i++)
            {
                flag = false; 
                for(int j = 0; j < this.completedCourses.size(); j++)
                {
                    if(course.getCorequisite().get(i).compare(this.completedCourses.get(j)))
                    {
                        flag = true; 
                    }
                }
                
                for(int j = 0; j < this.desiredCoureses.size(); j++)
                {
                    if(course.getCorequisite().get(i).compare(this.desiredCoureses.get(j)))
                    {
                        flag = true; 
                    }
                }
            }
        
        }
        
        if(flag == false)
        {
             FacesContext.getCurrentInstance().addMessage("desiredCourses:Submit",
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "You are missing a corequisite course!", null));
             
             return false; 
        }
        
         
         return true; 
    }
    
    public String validateSignUp(ArrayList<Course> list, Appointment appointment) throws SQLException, IOException, EmailException
    {
        

        
        if(this.desiredCoureses == null || this.desiredCoureses.size() < 1)
        {
           FacesContext.getCurrentInstance().addMessage("desiredCourses:Submit",
                    new FacesMessage(FacesMessage.SEVERITY_FATAL,
                            "Please select some courses before confirming an appointment.", null));
    
        }
        for (int i = 0; i < list.size(); i++)
        {
            if(!this.checkRequsites(CourseRepository.readCourseWithRequisites(ds,list.get(i))))
            {
                return null; 
            
            }
        }
        
         try
        {
             DesiredCourseRepository.deleteFromAppointment(ds, this.appointment.aID);
        }
       catch(SQLException ex)
       {
           Logger.getLogger(UserBean.class.getName()).log(Level.SEVERE, null, ex);
       }
        
        DesiredCourseRepository.createDesiredCourses(ds, desiredCoureses,Long.toString(appointment.aID));
           
        if(!this.edit){
            try{ 
                Email email = new HtmlEmail();
                email.setHostName("smtp.googlemail.com");
                email.setSmtpPort(465);
                email.setAuthenticator(new DefaultAuthenticator("uco.faculty.advisement", "!@#$1234"));
                email.setSSLOnConnect(true);
                email.setFrom("uco.faculty.advisement@gmail.com");
                email.setSubject("UCO Faculty Advisement Appointmen Confirmation");
            
            
                StringBuilder table = new StringBuilder(); 
                table.append("<style>" +
                   "td" +
                    "{border-left:1px solid black;" +
                    "border-top:1px solid black;}" +
                    "table" +
                    "{border-right:1px solid black;" +
                    "border-bottom:1px solid black;}" +
                    "</style>");
                table.append("<table><tr><td  width=\"350\">Course Name</td><td  width=\"350\">Course Subject</td><td  width=\"350\">Course Number</td><td  width=\"350\">Course Credits</td></tr> </table>");
                for(int i =0; i < this.desiredCoureses.size(); i++)
                {
                    table.append(
                        "<tr><td   width=\"350\">" + this.desiredCoureses.get(i).getName() + "</td>"
                        + "<td   width=\"350\">" + this.desiredCoureses.get(i).getSubject() + "</td>"
                        + "<td   width=\"350\">" + this.desiredCoureses.get(i).getNumber() + "</td>"
                        + "<td   width=\"350\">" + this.desiredCoureses.get(i).getCredits() + "</td></tr>"
                
                    );
            
                }
            
                email.setMsg(
                    "<p>Your appointment with your faculty advisor is at "
                     + appointment.datetime
                     + " on " + appointment.date + " . </p>" 
                            + "<p align=\"center\">Desired Courses</p>"
                            + table.toString() 
                     +"<p align=\"center\">UCO Faculty Advisement</p></font>"
                    
                    
                );
                email.addTo(username);
                email.send();
            }   catch (EmailException ex) {
            Logger.getLogger(VerificationBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return "/customerFolder/profile.xhtml"; 
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
