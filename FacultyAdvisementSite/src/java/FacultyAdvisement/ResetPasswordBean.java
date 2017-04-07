package FacultyAdvisement;

import java.io.Serializable;
import javax.inject.Named;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.sql.DataSource;
import javax.validation.constraints.Size;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 *
 * @author violentsushi
 */
@Named(value = "resetPasswordBean")
@SessionScoped
public class ResetPasswordBean implements Serializable {

    // resource injection
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    private String recipient;
    private String currentEmail;
    @Size(min = 3, message = " Minimum of 3 characters is required.")
    private String password;
    @Size(min = 3, message = " Minimum of 3 characters is required.")
    private String verifyPassword;
    private String token;
    private boolean resetCompleted;
    private boolean tokenValid;

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isResetCompleted() {
        return resetCompleted;
    }

    public void setResetCompleted(boolean resetCompleted) {
        this.resetCompleted = resetCompleted;
    }

    public boolean isTokenValid() {
        return tokenValid;
    }

    public void setTokenValid(boolean tokenValid) {
        this.tokenValid = tokenValid;
    }

    public void send() throws SQLException {
        boolean error = false;
        Student s = new Student();
        try {
            s = StudentRepository.read(ds, recipient);
        } catch (SQLException ex) {
            Logger.getLogger(ResetPasswordBean.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (s.getId() == null) {
            FacesMessage facesMsg = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    recipient + " is not in our system.", null);
            FacesContext.getCurrentInstance().addMessage(
                    null, facesMsg);
            error = true;
        }

        if (!error) {
            try {
                String newToken = UUID.randomUUID().toString();

                Connection conn = ds.getConnection();
                if (conn == null) {
                    throw new SQLException("conn is null; Can't get db connection");
                }
                try {
                    PreparedStatement ps;
                    ps = conn.prepareStatement(
                            "Insert into TOKENRESET (TOKEN, EMAIL, EXPIRATION) "
                            + "VALUES(?, ?, ?);"
                    );
                    ps.setString(1, newToken);
                    ps.setString(2, recipient);
                    ps.setTimestamp(3, new Timestamp(System.currentTimeMillis() + 900000)); // 15 minute expiration date
                    ps.executeUpdate();
                } finally {
                    conn.close();
                }

                Email email = new HtmlEmail();
                email.setHostName("smtp.googlemail.com");
                email.setSmtpPort(465);
                email.setAuthenticator(new DefaultAuthenticator("uco.faculty.advisement", "!@#$1234"));
                email.setSSLOnConnect(true);
                email.setFrom("uco.faculty.advisement@gmail.com");
                email.setSubject("UCO Faculty Advisement Password Reset");
                email.setMsg(
                        "<font size=\"3\">Please use the following link to "
                        + "<a href=\"http://localhost:8080/FacultyAdvisementSite/faces/resetPasswordPart2.xhtml?token=" + newToken + "\">reset your password</a>."
                        + "\nIf you did not request this password change please feel free to ignore it."
                        + "\n<p align=\"center\">UCO Faculty Advisement</p></font>"
                );
                email.addTo(recipient);
                email.send();
                FacesMessage facesMsg = new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Email has been sent to " + recipient, null);
                FacesContext.getCurrentInstance().addMessage(
                        null, facesMsg);
                recipient = "";
            } catch (EmailException ex) {
                Logger.getLogger(ResetPasswordBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void onPageLoad() throws SQLException {

        tokenValid = false;
        resetCompleted = false;
        currentEmail = "";
        password = "";
        verifyPassword = "";
        Timestamp tokenTimestamp = null;
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "Select * from TOKENRESET where TOKEN=?"
            );
            ps.setString(1, token);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                currentEmail = result.getString("EMAIL");
                tokenTimestamp = result.getTimestamp("EXPIRATION");
            }
        } finally {
            conn.close();
        }
        if (tokenTimestamp != null) {
            if (currentTimestamp.before(tokenTimestamp)) {
                tokenValid = true;
            } else {
                conn = ds.getConnection();
                if (conn == null) {
                    throw new SQLException("conn is null; Can't get db connection");
                }
                try {
                    PreparedStatement ps = conn.prepareStatement(
                            "Delete from TOKENRESET where TOKEN=?"
                    );
                    ps.setString(1, token);
                    ps.executeUpdate();
                } finally {
                    conn.close();
                }
            }
        }
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
            Connection conn = ds.getConnection();
            if (conn == null) {
                throw new SQLException("conn is null; Can't get db connection");
            }
            try {
                PreparedStatement ps = conn.prepareStatement(
                        "Delete from TOKENRESET where TOKEN=?"
                );
                ps.setString(1, token);
                ps.executeUpdate();
                FacesMessage facesMsg = new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        currentEmail + " password has been reset.", null);
                FacesContext.getCurrentInstance().addMessage(
                        null, facesMsg);
                resetCompleted = true;
            } finally {
                conn.close();
            }
            token = "";
        }
    }

}
