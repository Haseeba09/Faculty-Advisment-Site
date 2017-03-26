package FacultyAdvisement;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.sql.DataSource;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Named(value = "registrationBean")
@SessionScoped
public class RegistrationBean implements Serializable, ICRUDHanlder {
    
    @PostConstruct
    public void init() {
        try {
            students = readAll();
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(RegistrationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void create() throws SQLException {
        
        String studentSQL = "INSERT INTO STUDENT(STUID, email, majorcode, phone) VALUES(?, ?, ?, ?)";
        String userSQL = "INSERT INTO USERTABLE(USERNAME, PASSWORD) VALUES(?, ?)";
        String groupSQL = "INSERT INTO GROUPTABLE(groupname, username) VALUES(?, ?)";

        if (dataSource == null) {
            throw new SQLException("datasource is null; Can't get data source");
        }

        Connection datasourceConnection = dataSource.getConnection();

        if (datasourceConnection == null) {
            throw new SQLException("connection is null, Can't get database connection");
        }

        try {
            PreparedStatement studentSQLStatement = datasourceConnection.prepareStatement(studentSQL);
            PreparedStatement userSQLStatement = datasourceConnection.prepareStatement(userSQL);
            PreparedStatement groupSQLStatement = datasourceConnection.prepareStatement(groupSQL);

            studentSQLStatement.setString(1, String.valueOf(this.studentID));
            studentSQLStatement.setString(2, this.username);
            studentSQLStatement.setString(3, String.valueOf(this.majorCode));
            studentSQLStatement.setString(4, this.phone);
            studentSQLStatement.execute();
            
            userSQLStatement.setString(1, this.username);
            userSQLStatement.setString(2, SHA256Encrypt.encrypt(this.password));
            userSQLStatement.execute();
            
            groupSQLStatement.setString(1, "customergroup");
            groupSQLStatement.setString(2, this.username);
            groupSQLStatement.execute();
        } finally {
            datasourceConnection.close();
            this.username = "";
            this.password = "";
            this.studentID = 0;
            this.majorCode = 0;
            this.phone = "";
            refresh();
        }
    }

    @Override
    public void read() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public Map readAll() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("datasource is null; Can't get data source");
        }

        Connection datasourceConnection = dataSource.getConnection();

        if (datasourceConnection == null) {
            throw new SQLException("connection is null, Can't get database connection");
        }

        Map<String, Student> temporaryStudents = new HashMap<>();

        try {
            PreparedStatement preparedStatement = datasourceConnection.prepareStatement(
                    "SELECT * FROM STUDENT"
            );

            // retrieve customer data from database
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                Student student = new Student();
                student.setStudentID(Integer.parseInt(result.getString("STUID")));
                student.setUsername(result.getString("email"));
                student.setMajorCode(Integer.parseInt(result.getString("majorcode")));
                student.setPhone(result.getString("phone"));
                temporaryStudents.put(student.getUsername(), student);
            }
        } finally {
            datasourceConnection.close();
        }

        return temporaryStudents;
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public void validate() {
        try {
            if (students.containsKey(this.username)) {
                FacesContext.getCurrentInstance().addMessage("signUp:username",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Username has already been taken", null));
            } else {
                create();
            }
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(RegistrationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String returnHome() {
        return "index";
    }
    private void refresh() {
        try {
            students = readAll();
        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(RegistrationBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public int getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(int majorCode) {
        this.majorCode = majorCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    private Map<String, Student> students;

    @Resource(name = "jdbc/ds_wsp")
    private DataSource dataSource;
    
    @Pattern(regexp = ".{3,}@uco.edu$", message = "Username should be in the format xxx@uco.edu (where xxx is minimum 3 characters).")
    private String username;
    
    @Size(min = 3, message = "Minimum of 3 characters is required.")
    private String password;
    
    @Max(99999999)
    @Min(10000000)
    private int studentID;
    
    private int majorCode;
    
    @Pattern(regexp = "\\d{10}", message = "Phone should be in the form dddddddddd (where d is digit).")
    private String phone; 
}