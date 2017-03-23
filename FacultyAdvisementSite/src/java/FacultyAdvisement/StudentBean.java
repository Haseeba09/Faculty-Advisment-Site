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
import javax.sql.DataSource;

/**
 *
 * @author violentsushi
 */
@Named(value = "studentBean")
@SessionScoped
public class StudentBean implements Serializable, CRUDHandler {

    // resource injection
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;
    
    private HashMap<String, StudentPOJO> students = new HashMap<>();
    
    @PostConstruct
    public void init() {
        try {
            students = (HashMap<String, StudentPOJO>) readAll(ds);
        } catch (SQLException ex) {
            Logger.getLogger(StudentBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public HashMap<String, StudentPOJO> getStudents() {
        return students;
    }
    
    @Override
    public void create(DataSource ds) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void read(DataSource ds, String key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map readAll(DataSource ds) throws SQLException {
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

    @Override
    public void update(DataSource ds) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(DataSource ds, String key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
