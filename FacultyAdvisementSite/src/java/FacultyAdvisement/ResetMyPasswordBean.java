package FacultyAdvisement;

import java.io.Serializable;
import javax.inject.Named;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.sql.DataSource;
import javax.validation.constraints.Size;

/**
 *
 * @author violentsushi
 */
@Named(value = "resetMyPasswordBean")
@SessionScoped
public class ResetMyPasswordBean implements Serializable {

    // resource injection
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    private String currentEmail;
    @Size(min = 3, message = " Minimum of 3 characters is required.")
    private String password;
    @Size(min = 3, message = " Minimum of 3 characters is required.")
    private String verifyPassword;
    private boolean resetCompleted;

    public String getCurrentEmail() {
        return currentEmail;
    }

    public void setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerifyPassword() {
        return verifyPassword;
    }

    public void setVerifyPassword(String verifyPassword) {
        this.verifyPassword = verifyPassword;
    }

    public boolean isResetCompleted() {
        return resetCompleted;
    }

    public void setResetCompleted(boolean resetCompleted) {
        this.resetCompleted = resetCompleted;
    }

    public void onPageLoad(String username) throws SQLException {
        resetCompleted = false;
        currentEmail = username;
        password = "";
        verifyPassword = "";
    }
    
    public void reset() throws SQLException {

        boolean error = false;
        if (!password.equals(verifyPassword)) {
            FacesMessage facesMsg = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Not the same password.", null);
            FacesContext.getCurrentInstance().addMessage(
                    null, facesMsg);
            error = true;
        }

        if (!error) {
            StudentRepository.updatePassword(ds, currentEmail, password);
            FacesMessage facesMsg = new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    currentEmail + " password has been reset.", null);
            FacesContext.getCurrentInstance().addMessage(
                    null, facesMsg);
            resetCompleted = true;
        }
    }

}
