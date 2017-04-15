/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FacultyAdvisement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 *
 * @author abilb
 */
public class DesiredCourseRepository {
    
      public static List<Course> readDesiredCourses (DataSource ds, String key) throws SQLException
    {
         if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        List<Course> list = new ArrayList<Course>(); 
            
        try
        {
           PreparedStatement ps = conn.prepareStatement(
                  "SELECT * FROM Course JOIN Desired ON Course.course_number = Desired.course_number and subject = subject where ID = ?"
                   
           ); 
           
           ps.setString(1, key);
           ResultSet result = ps.executeQuery();

            while (result.next()) {
                Course course = new Course(); 
                course.setName(result.getString("course_name"));
                course.setCredits(result.getInt("credits"));
                course.setNumber(result.getString("course_number"));
                course.setSubject(result.getString("subject"));
                list.add(course);
            }
           
        }
        finally
        {
            conn.close();
        }       
        
        return list;
    }
    
      public static void createDesiredCourses(DataSource ds, List<Course> list, String key ) throws SQLException
      {
           if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
            
        
    try {
         
        PreparedStatement ps = conn.prepareStatement(
                  "INSERT INTO Desired(course_number, course_subject, ID) values (?, ? , ?)");
                  
            for (Course course : list) {
            
                ps.setString(1, course.getNumber());
                  ps.setString(2, course.getSubject());
                  ps.setString(3, key);
                  
            
            ps.addBatch();
             

             
           
           
        }
    
          ps.executeBatch(); 
        
        }
        finally
            {
            conn.close();
            }           
      
      }
    
}
