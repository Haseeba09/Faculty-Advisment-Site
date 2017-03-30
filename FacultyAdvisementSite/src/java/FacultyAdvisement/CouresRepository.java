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
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;

/**
 *
 * @author abilb
 */
public class CouresRepository {
    
    
    public static List<Course> readCourses(DataSource ds) throws SQLException
    {
         if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }

        List<Course> list = new ArrayList<Course>(); 

        try {
            PreparedStatement ps = conn.prepareStatement(
                    "select * from Course"
            );

            // retrieve customer data from database
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                Course course = new Course(); 
                course.setName(result.getString("course_name"));
                course.setCredits(result.getInt("credits"));
                course.setName(result.getString("course_name"));
                course.setSubject(result.getString("subject"));
                list.add(course);
            }

        } finally {
            conn.close();
        }

        return list;
        
    }
    
    public static List<Course> readCompletedCourses(DataSource ds, String stuid) throws SQLException
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
                  "SELECT * FROM Course JOIN Completed ON STUID = STUID WHERE STUID = ?"
                   
           ); 
           
           ps.setString(1, stuid);
           ResultSet result = ps.executeQuery();

            while (result.next()) {
                Course course = new Course(); 
                course.setName(result.getString("course_name"));
                course.setCredits(result.getInt("credits"));
                course.setName(result.getString("course_name"));
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
    
   // Update Not Necessary at this Point
   // Create Not Necessary at this Point
   // Deleat Not Necessary at this Point  
    
}
