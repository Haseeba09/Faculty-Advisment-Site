package FacultyAdvisement;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.sql.DataSource;

@Named(value = "userBean")
@SessionScoped
public class UserBean implements Serializable {

    

    private String username;
    private Student student;
    private String newPassword; 
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
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
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
        newPassword = ""; 
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
        
    }

    public String getUsername() {
        return username;
    }
    public String validateForm() throws NoSuchAlgorithmException {

       boolean flag = true; 

        if (student.getPhoneNumber().isEmpty() || student.getPhoneNumber().matches("\\(?(\\d{3})\\)?-?(\\d{3})-(\\d{4})")) {
            FacesContext.getCurrentInstance().addMessage("studentForm:phoneNumber",
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Please enter a propery phone number, ddd-ddd-dddd.", null));
            flag = false;
        }  

         if (student.getMajorCode().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage("studentForm:majorCode",
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Select a major code!", null));
            flag = false;
        }  

        
            if (student.getId().matches("")) {
            FacesContext.getCurrentInstance().addMessage("studentForm:id",
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Enter a valid UCO ID number!", null));
            flag = false;
        }  
      
        if (newPassword.isEmpty() || newPassword.length() < 3) {
            FacesContext.getCurrentInstance().addMessage("studentForm:password",
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "This password is too weak, it must have at least three (3) characters.", null));
           flag = false;
        }  
        
        
        if(flag==true)
        {
            
           try {
               //this.student.update(ds);
                 student.setPassword(newPassword);
                 
               StudentRepository.update(ds, student);
               student.setEdit(false);
           } catch (SQLException ex) {
               Logger.getLogger(UserBean.class.getName()).log(Level.SEVERE, null, ex);
           }
           
             FacesContext.getCurrentInstance().addMessage("studentForm:save",
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "User Information Updated", null));
          
            
            return null;
        }
        
         FacesContext.getCurrentInstance().addMessage("studentForm:edit",
                    new FacesMessage(FacesMessage.SEVERITY_FATAL,
                            "Something went wrong!", null));
         student.setEdit(true);
         
        return null;
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
}
