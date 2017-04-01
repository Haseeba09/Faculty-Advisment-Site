/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FacultyAdvisement;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author Hasee
 */
@Named(value = "appointmentBean")
@SessionScoped
public class AppointmentBean implements Serializable {

    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;
    
    private List<Appointment> appointments;
    private Appointment appointment = new Appointment();
    
    @PostConstruct
    public void init() {
       try {
           appointments = loadAppointments();
        } catch (SQLException ex) {
            Logger.getLogger(AppointmentBean.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
    public String insertAppointment() throws SQLException{
         Connection conn1 = ds.getConnection();

        if (conn1 == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        
        try{
            PreparedStatement ps = conn1.prepareStatement(
                    "insert into APPOINTMENT(sdate, stime) VALUES(?,?)"
            );
            appointment.setDate(appointment.getDatetime());
            
            ps.setString(1, appointment.getDate());
            ps.setString(2, appointment.getTime());
            ps.execute();
        }
        finally{
            conn1.close();
        }
        
        
        
        appointments = loadAppointments();
        
        return null;
    }
    
    public String deleteBook(Appointment appointment) throws SQLException{
        
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        
        try{
            PreparedStatement ps = conn.prepareStatement(
                    "delete FROM appointment where ID ="+appointment.getaID()
            );
            
            ps.execute();
        } finally {
            conn.close();
        }
        appointments = loadAppointments(); 
        return null;
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
                a.setDatetime(a.getDate() + " " + a.getTime());
                
                           
                list.add(a);
                Collections.sort(list,new AppointmentCompare());
            }

        } finally {
            conn.close();
        }
        
        return list;
        
    }
    
}
