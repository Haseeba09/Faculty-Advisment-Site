/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FacultyAdvisement;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Dictionary;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 *
 * @author abilb
 */
public class Student implements CRUDHandler {
    //the username takes the format of an email
    private String username; 
    private String password; 
    private int id; 
    private int majorCode;
    private String phoneNumber; 
    private boolean edit = false; 

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(int majorCode) {
        this.majorCode = majorCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
   
    @Override
    public void create(DataSource ds) throws SQLException {
        String studentSQL = "INSERT INTO STUDENT(STUID, EMAIL, MAJORCODE, PHONE) "
                + "VALUES (?, ?, ?, ?)";
        String userSQL = "INSERT INTO USERTABLE(PASSWORD, EMAIL)) VALUES (?,?)";
        
        String groupSQL = "INSERT INTO GROUPTABLE(GROUPNAME, USERNAME) VALUES (CUSTOMERGROUP, ?)";
       
            if (ds == null) {
                throw new SQLException("ds is null; Can't get data source");
            }

            Connection conn = ds.getConnection();

            if (conn == null) {
                throw new SQLException("conn is null; Can't get db connection");
            }

            try {
          
           //Here we execute three SQL statements
            //Student Information
            PreparedStatement sqlStatement = conn.prepareStatement(studentSQL);
            //sqlStatement.setString(1, Integer.toString(books.size() + 1)); //Integer.toString(this.getBooks().size() + 1));
            sqlStatement.setString(1, Integer.toString(this.id));
            sqlStatement.setString(2, this.username);
            sqlStatement.setString(3, Integer.toString(this.majorCode));
            sqlStatement.setString(4, this.phoneNumber);
           
          
            sqlStatement.executeUpdate();

            //user credentials
            sqlStatement = conn.prepareStatement(userSQL);
            
            try {
                //Encrypt the pssword into SHA-256
                sqlStatement.setString(1, Encrypt.encrypt(this.password));
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            sqlStatement.setString(2, this.username);
            sqlStatement.execute();
            
            //Group Table
            sqlStatement = conn.prepareStatement(groupSQL);
            sqlStatement.setString(1, this.username);
            sqlStatement.execute();
            } finally {
            conn.close();
               }
            
    }

      @Override
    public Dictionary readAll(DataSource ds) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update(DataSource ds) throws SQLException {
        String studentSQL = "UPDATE Book SET STUID= ?, MAJORCODE = ?, PASSWORD = ?, PHONENUMBER = ?, WHERE STUID = ?";
        String userSQL = "UPDATE USERTABLE SET PASSWORD = ?";
        
       
            if (ds == null) {
                throw new SQLException("ds is null; Can't get data source");
            }

            Connection conn = ds.getConnection();

            if (conn == null) {
                throw new SQLException("conn is null; Can't get db connection");
            }

            try {
          
           //Here we execute three SQL statements
            //Student Information
            PreparedStatement sqlStatement = conn.prepareStatement(studentSQL);
            //sqlStatement.setString(1, Integer.toString(books.size() + 1)); //Integer.toString(this.getBooks().size() + 1));
            sqlStatement.setString(1, Integer.toString(this.id));
            sqlStatement.setString(3, Integer.toString(this.majorCode));
            sqlStatement.setString(4, this.phoneNumber);
            sqlStatement.setString(5, Integer.toString(this.id));
          
            sqlStatement.executeUpdate();

            //user credentials
            sqlStatement = conn.prepareStatement(userSQL);
            
            try {
                //Encrypt the pssword into SHA-256
                sqlStatement.setString(1, Encrypt.encrypt(this.password));
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
            }
            
          
            sqlStatement.execute();
            
            
            } finally {
            conn.close();
               }
    }

    
    @Override
    public void delete(DataSource ds, String key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void read(DataSource ds, String key) throws SQLException {
        String studentSQL = "SElECT * FROM STUDENT WHERE EMAIL = ?";
        this.edit = false; 
       
            if (ds == null) {
                throw new SQLException("ds is null; Can't get data source");
            }

            Connection conn = ds.getConnection();

            if (conn == null) {
                throw new SQLException("conn is null; Can't get db connection");
            }

            try {
          
     
            PreparedStatement sqlStatement = conn.prepareStatement(studentSQL);
  
            sqlStatement.setString(1, key);
          
            ResultSet result = sqlStatement.executeQuery();
 
             //this.id = Integer.parseInt(result.getString("STUID"));
            // this.majorCode = Integer.parseInt(result.getString("majorcode"));
            // this.password = result.getString("password");
             this.username = key;
            // this.phoneNumber = result.getString("phone");
            
            } finally {
            conn.close();
               }
    
            
    }

   
    
    
}
