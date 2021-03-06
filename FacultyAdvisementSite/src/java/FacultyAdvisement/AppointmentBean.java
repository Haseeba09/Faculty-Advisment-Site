package FacultyAdvisement;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.security.Principal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

/**
 *
 * @author Hasee
 */
@Named(value = "appointmentBean")
@SessionScoped
public class AppointmentBean implements Serializable {

    @Inject
    SignupBean signupBean;

    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;
    private String username;
    private List<Appointment> appointments;
    private List<Appointment> scheduledAppointments;
    private List<Course> desiredCourses;
    private Appointment appointment = new Appointment();
    private Student student = new Student();

    @PostConstruct
    public void init() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Principal p = fc.getExternalContext().getUserPrincipal();
        username = p.getName();
        try {
            appointments = loadAppointments();
        } catch (SQLException ex) {
            Logger.getLogger(AppointmentBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Course> getDesiredCourses() {
        return desiredCourses;
    }

    public void setDesiredCourses(List<Course> desiredCourses) {
        this.desiredCourses = desiredCourses;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public List<Appointment> getScheduledAppointments() {
        return scheduledAppointments;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    private String formatMonth(String month) {
        String formattedMonth = "00";

        if (month.equals("Jan")) {
            formattedMonth = "01";
        } else if (month.equals("Feb")) {
            formattedMonth = "02";
        } else if (month.equals("Mar")) {
            formattedMonth = "03";
        } else if (month.equals("Apr")) {
            formattedMonth = "04";
        } else if (month.equals("May")) {
            formattedMonth = "05";
        } else if (month.equals("Jun")) {
            formattedMonth = "06";
        } else if (month.equals("Jul")) {
            formattedMonth = "07";
        } else if (month.equals("Aug")) {
            formattedMonth = "08";
        } else if (month.equals("Sep")) {
            formattedMonth = "09";
        } else if (month.equals("Oct")) {
            formattedMonth = "10";
        } else if (month.equals("Nov")) {
            formattedMonth = "11";
        } else if (month.equals("Dec")) {
            formattedMonth = "12";
        }
        return formattedMonth;
    }

    public String toSignUp(String key, Appointment appointment) throws IOException, SQLException {
        this.appointment = appointment;
        this.signupBean.setEdit(false);
        this.updateAppointment(key, appointment, false);
        return "/customerFolder/signup.xhtml";
    }

    public String editDesired(String key, Appointment appointment) throws IOException, SQLException {
        this.appointment = appointment;
        this.signupBean.setEdit(true);
        return "/customerFolder/signup.xhtml";
    }

    public void insertAppointment() throws SQLException {
        Connection conn1 = ds.getConnection();

        boolean execute = true;

        String month = appointment.getDate().substring(4, 7);
        month = formatMonth(month);

        String year = appointment.getDate().substring(23);
        String day = appointment.getDate().substring(8, 10);

        String mDate = year + "-" + month + "-" + day;

        /*
                here is the annoying format that the calanedr returns:
                Sat Apr 01 00:00:00 CDT 2017
            
                this is why i have to use substrings to get the date right. 
                please do not change.
         */
        String mTime = appointment.getTime().substring(11, 19);

        appointment.setDate(mDate);
        appointment.setTime(mTime);

        for (int i = 0; i < appointments.size(); i++) {
            Appointment appointment1 = appointments.get(i);
            if (appointment1.getDate().trim().equals(mDate.trim()) && appointment1.getTime().trim().equals(mTime.trim())) {
                execute = false;

                FacesContext.getCurrentInstance().addMessage("datePick:create",
                        new FacesMessage(FacesMessage.SEVERITY_FATAL,
                                "Appointment already exists!", null));
                return;
            }
        }

        if (execute) {
            if (conn1 == null) {
                throw new SQLException("conn is null; Can't get db connection");
            }

            try {
                PreparedStatement ps = conn1.prepareStatement(
                        "insert into APPOINTMENT(sdate, stime) VALUES(?,?)"
                );
                /*
                here is the annoying format that the calanedr returns:
                Sat Apr 01 00:00:00 CDT 2017
                 */

                ps.setString(1, appointment.getDate());
                ps.setString(2, appointment.getTime());
                ps.execute();
            } finally {
                conn1.close();
            }

            appointments = loadAppointments();
        }

    }

    public void deleteBook(Appointment appointment) throws SQLException {

        String emailAddress = "";

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            PreparedStatement getSid = conn.prepareStatement(
                    "select email from student join appointment on appointment.sid = student.stuid where appointment.id =" + appointment.getaID()
            );

            ResultSet result = getSid.executeQuery();
            while (result.next()) {

                emailAddress = result.getString("email");

            }

            try {
                Email email = new HtmlEmail();
                email.setHostName("smtp.googlemail.com");
                email.setSmtpPort(465);
                email.setAuthenticator(new DefaultAuthenticator("uco.faculty.advisement", "!@#$1234"));
                email.setSSLOnConnect(true);
                email.setFrom("uco.faculty.advisement@gmail.com");
                email.setSubject("Appointment Cancelled - UCO Faculty Advisment");
                email.setMsg(
                        "<font size=\"3\" style=\"font-family:verdana\"> Dear Student, \n"
                        + "Your advisor has cancelled your appointment. Please schedule a new appointment. \n"
                        + "Thank You."
                        + "\n<p align=\"center\">UCO Faculty Advisement</p></font>"
                );
                email.addTo(emailAddress);
                email.send();
            } catch (EmailException ex) {
                Logger.getLogger(VerificationBean.class.getName()).log(Level.SEVERE, null, ex);
            }

            PreparedStatement ps = conn.prepareStatement(
                    "delete FROM appointment where ID =" + appointment.getaID()
            );

            ps.execute();

        } finally {
            conn.close();
        }

        appointments = loadAppointments();

    }

    public List<Appointment> loadAppointments() throws SQLException {

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        List<Appointment> list = new ArrayList<>();

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "select * from Appointment"
            );

            // retrieve customer data from database
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                Appointment a = new Appointment();

                a.setaID(result.getLong("ID"));
                a.setSid(result.getString("sid"));
                a.setDate(result.getString("sdate"));
                a.setTime(result.getString("stime"));
                a.setComment(result.getString("comment"));
                a.setDatetime(a.getDate() + " " + a.getTime());

                list.add(a);
                Collections.sort(list, new AppointmentCompare());
            }

        } finally {
            conn.close();
        }

        return list;

    }

    // Hung's methods for student advisement
    public void getScheduledAppointment(String studentID) {
        this.scheduledAppointments = new ArrayList();
        appointments.stream().filter((appointment) -> (appointment.sid != null && appointment.sid.equals(studentID))).forEachOrdered((appointment) -> {
            this.scheduledAppointments.add(appointment);
        });
    }

    public List<Appointment> getAvailableAppointment() {
        List<Appointment> availableAppointments = new ArrayList<>();
        appointments.stream().filter((appointment) -> (!appointment.isTaken())).forEachOrdered((appointment) -> {
            availableAppointments.add(appointment);
        });
        return availableAppointments;
    }

    public void updateAppointment(String studentID, Appointment appoinment, boolean isCancel) throws SQLException {

        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE appointment SET SID = ?, comment = ? WHERE stime = ? AND sdate = ?"
            );

            if (!isCancel) {
                ps.setString(1, String.valueOf(studentID));
                ps.setString(2, appoinment.comment);
                ps.setString(3, appoinment.time);
                ps.setString(4, appoinment.date);
            } else if (isCancel) {
                ps.setNull(1, java.sql.Types.VARCHAR);
                ps.setNull(2, java.sql.Types.VARCHAR);
                ps.setString(3, appoinment.time);
                ps.setString(4, appoinment.date);
            }

            ps.execute();

            if (isCancel) {
                ps = conn.prepareStatement("delete FROM desired where ID =?");
                ps.setString(1, Long.toString(appointment.aID));
                ps.execute();
            }

        } finally {
            conn.close();
        }
        appointments = loadAppointments();
        getScheduledAppointment(studentID);
    }

    public String goToViewAppointment(Appointment appointment) throws SQLException {
        this.appointment = appointment;
        String id = Long.toString(this.appointment.aID);
        desiredCourses = DesiredCourseRepository.readDesiredCourses2(ds, id);
        this.student = StudentRepository.readById(ds, appointment.getSid());
        return "/adminFolder/viewAppointment";
    }
    /*
    public void markStudentAsAdvised() throws SQLException{
        this.student.setAdvised(true);
        StudentRepository.adminUpdate(ds, student, student.getUsername());
        
        
        
    }
     */
}
