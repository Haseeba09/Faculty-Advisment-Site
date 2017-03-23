package FacultyAdvisement;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.Principal;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.sql.DataSource;

@Named(value = "userBean")
@SessionScoped
public class UserBean implements Serializable {

    
    private String username;
    private Student student; 

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
        try {
            student.read(ds, username);
        } catch (SQLException ex) {
            Logger.getLogger(UserBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public String getUsername() {
        return username;
    }

}
