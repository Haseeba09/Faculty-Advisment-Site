package FacultyAdvisement;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.model.SelectItem;
import javax.sql.DataSource;

/**
 *
 * @author violentsushi
 */
@Named(value = "studentBean")
@SessionScoped
public class StudentBean implements Serializable {

    // resource injection
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;

    private HashMap<String, StudentPOJO> students = new HashMap<>();
    private List<SelectItem> majors = new ArrayList<>();
    
    @PostConstruct
    public void init() {
        try {
            students = (HashMap<String, StudentPOJO>) readAll();
            majors = getAvailableMajors();
        } catch (SQLException ex) {
            Logger.getLogger(StudentBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<SelectItem> getAvailableMajors() throws SQLException {
        
        List<SelectItem> list = new ArrayList<>();
        
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "select * from AVAILABLEMAJOR"
            );

            // retrieve customer data from database
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                String s = result.getString("MAJORCODE");
                list.add(new SelectItem(s));
            }

        } finally {
            conn.close();
        }
        
        return list;
        
    }
    
    public HashMap<String, StudentPOJO> getStudents() {
        return students;
    }

    public void create(DataSource ds) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void read(DataSource ds, String key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Map readAll() throws SQLException {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        HashMap<String, StudentPOJO> list = new HashMap<>();

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "select * from STUDENT"
            );

            // retrieve customer data from database
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                StudentPOJO s = new StudentPOJO();
                s.setSid(result.getString("STUID"));
                s.setEmail(result.getString("EMAIL"));
                s.setMajor(result.getString("MAJORCODE"));
                s.setPhone(result.getString("PHONE"));
                list.put(s.getSid(), s);
            }

        } finally {
            conn.close();
        }

        return list;
    }

    public void update(String key) throws SQLException {
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {
            StudentPOJO s = students.get(key);
            PreparedStatement ps;
            ps = conn.prepareStatement(
                    "Update STUDENT set EMAIL=?, MAJORCODE=?, PHONE=? "
                    + "where STUID=?"
            );
            ps.setString(1, s.getEmail());
            ps.setString(2, s.getMajor());
            ps.setString(3, s.getPhone());
            ps.setString(4, s.getSid());
            ps.executeUpdate();
            if (s.isResetPassword()) {
                String defaultPassword = "password";
                defaultPassword = SHA256Encrypt.encrypt(defaultPassword);
                ps = conn.prepareStatement(
                        "Update USERTABLE set PASSWORD=? where USERNAME=?"
                );
                ps.setString(1, defaultPassword);
                ps.setString(2, s.getEmail());
                ps.executeUpdate();
            }
        } finally {
            conn.close();
        }

        students = (HashMap<String, StudentPOJO>) readAll(); // reload the updated info
    }

    public void delete(String key) throws SQLException {
        Connection conn = ds.getConnection();
        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        try {
            StudentPOJO s = students.get(key);
            PreparedStatement ps;
            ps = conn.prepareStatement(
                    "Delete from STUDENT where EMAIL=?"
            );
            ps.setString(1, s.getEmail());
            ps.executeUpdate();
            ps = conn.prepareStatement(
                    "Delete from USERTABLE where USERNAME=?"
            );
            ps.setString(1, s.getEmail());
            ps.executeUpdate();
            ps = conn.prepareStatement(
                    "Delete from GROUPTABLE where USERNAME=?"
            );
            ps.setString(1, s.getEmail());
            ps.executeUpdate();
        } finally {
            conn.close();
        }

        students = (HashMap<String, StudentPOJO>) readAll(); // reload the updated info
    }

}
