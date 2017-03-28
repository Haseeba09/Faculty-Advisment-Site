package FacultyAdvisement;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.sql.SQLException;
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
        try {
            student = StudentRepository.read(ds, username);
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

}
