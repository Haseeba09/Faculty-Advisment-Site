package FacultyAdvisement;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
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
import javax.faces.context.FacesContext;
import javax.sql.DataSource;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 *
 * @author violentsushi
 */
@Named(value = "verificationBean")
@SessionScoped
public class VerificationBean implements Serializable {

    // resource injection
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    private String currentEmail;
    private String token;
    private boolean verified;
    private boolean verificationCompleted;
    private boolean tokenValid;

    public String getCurrentEmail() {
        return currentEmail;
    }

    public void setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
    
    public boolean isVerificationCompleted() {
        return verificationCompleted;
    }

    public void setVerificationCompleted(boolean verificationCompleted) {
        this.verificationCompleted = verificationCompleted;
    }

    public boolean isTokenValid() {
        return tokenValid;
    }

    public void setTokenValid(boolean tokenValid) {
        this.tokenValid = tokenValid;
    }

    public void send(String recipient) throws SQLException {

        try {
            String newToken = UUID.randomUUID().toString();

            Connection conn = ds.getConnection();
            if (conn == null) {
                throw new SQLException("conn is null; Can't get db connection");
            }
            try {
                PreparedStatement ps;
                ps = conn.prepareStatement(
                        "Insert into TOKENVERIFICATION (TOKEN, EMAIL, EXPIRATION) "
                        + "VALUES(?, ?, ?);"
                );
                ps.setString(1, newToken);
                ps.setString(2, recipient);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis() + 3600000)); // 1 hour expiration date
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
            email.setSubject("UCO Faculty Advisement Verify Email");
            email.setMsg(
                    "<font size=\"3\">Please use the following link to "
                    + "<a href=\"http://localhost:8080/FacultyAdvisementSite/faces/verifyPart2.xhtml?token=" + newToken + "\">verify your email</a>."
                    + "\nIf you did not register for this account, please reply to this email so that we may fix this problem."
                    + "\n<p align=\"center\">UCO Faculty Advisement</p></font>"
            );
            email.addTo(recipient);
            email.send();
            FacesMessage facesMsg = new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    "Email has been sent to " + recipient, null);
            FacesContext.getCurrentInstance().addMessage(
                    null, facesMsg);
        } catch (EmailException ex) {
            Logger.getLogger(VerificationBean.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void onPageLoad() throws SQLException {

        tokenValid = false;
        verificationCompleted = false;
        currentEmail = "";
        Timestamp tokenTimestamp = null;
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "Select * from TOKENVERIFICATION where TOKEN=?"
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
                verify();
            } else {
                conn = ds.getConnection();
                if (conn == null) {
                    throw new SQLException("conn is null; Can't get db connection");
                }
                try {
                    PreparedStatement ps = conn.prepareStatement(
                            "Delete from TOKENVERIFICATION where TOKEN=?"
                    );
                    ps.setString(1, token);
                    ps.executeUpdate();
                } finally {
                    conn.close();
                }
            }
        }
    }

    public void verify() throws SQLException {

        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {
            PreparedStatement ps;
            ps = conn.prepareStatement(
                    "Update USERTABLE set VERIFIED=? where USERNAME=?"
            );
            ps.setString(1, "true");
            ps.setString(2, currentEmail);
            ps.executeUpdate();
            ps = conn.prepareStatement(
                    "Delete from TOKENVERIFICATION where TOKEN=?"
            );
            ps.setString(1, token);
            ps.executeUpdate();
            FacesMessage facesMsg = new FacesMessage(
                    FacesMessage.SEVERITY_INFO,
                    currentEmail + " has been verified.", null);
            FacesContext.getCurrentInstance().addMessage(
                    null, facesMsg);
            verificationCompleted = true;
        } finally {
            conn.close();
        }
        token = "";
    }

    public void checkVerification(String user) throws SQLException {
        String isVerified = "";
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {
            PreparedStatement ps;
            ps = conn.prepareStatement(
                    "Select * from USERTABLE where USERNAME=?"
            );
            ps.setString(1, user);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                isVerified = result.getString("VERIFIED");
            }
        } finally {
            conn.close();
        }
        if (isVerified.equals("false")) {
            verified = false;
        } else {
            verified = true;
        }
    }

    public String checkVerificationRedirect(String user) throws SQLException {
        String isVerified = "";
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {
            PreparedStatement ps;
            ps = conn.prepareStatement(
                    "Select * from USERTABLE where USERNAME=?"
            );
            ps.setString(1, user);
            ResultSet result = ps.executeQuery();
            if (result.next()) {
                isVerified = result.getString("VERIFIED");
            }
        } finally {
            conn.close();
        }
        if (isVerified.equals("false")) {
            return "/customerFolder/verifyPart1";
        } else {
            return null;
        }
    }

}
