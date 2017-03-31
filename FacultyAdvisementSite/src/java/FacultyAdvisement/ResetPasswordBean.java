package FacultyAdvisement;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.constraints.Pattern;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 *
 * @author violentsushi
 */
@Named(value = "resetPasswordBean")
@SessionScoped
public class ResetPasswordBean implements Serializable {

    //@Pattern(regexp = "[a-zA-Z0-9]{3,20}@uco.edu", message = "Local-part of email must be 3 to 20 alphanumeric characters long and end with @uco.edu")
    private String recipient;

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void send() {
        try {
            Email email = new SimpleEmail();
            email.setHostName("smtp.googlemail.com");
            email.setSmtpPort(465);
            email.setAuthenticator(new DefaultAuthenticator("uco.faculty.advisement", "!@#$1234"));
            email.setSSLOnConnect(true);
            email.setFrom("uco.faculty.advisement@gmail.com");
            email.setSubject("TestMail");
            email.setMsg("This is a test mail ... :-)");
            email.addTo(recipient);
            email.send();
            System.out.println("email sent successfully");
        } catch (EmailException ex) {
            Logger.getLogger(ResetPasswordBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
