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
 * @author Hasee
 */
public class CurrentCourseRepository {
    
    public static List<Course> readCurrentCourses(DataSource ds, String stuid) throws SQLException
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
                   "SELECT Course.`Course_Name`, Course.`Subject`, Course.`Course_Number` FROM "
                           + "Course JOIN CurrentCourse ON Course.`Subject` = currentcourse.subject and Course.`Course_Number` = currentcourse.course_number "
                           + "WHERE STUID = ?"
                   
           ); 
           
           ps.setString(1, stuid);
           ResultSet result = ps.executeQuery();

            while (result.next()) {
                Course course = new Course(); 
                course.setName(result.getString("course_name"));
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
    
    public static void createCurrentCourse(DataSource ds, String stuid, String subject, String courseNumber) throws SQLException
   {
         if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
          try
        {
           PreparedStatement ps = conn.prepareStatement(
                  "Insert into CurrentCourse(STUID, subject, course_number) values (?,?,?) "
                   
           ); 
           
           ps.setString(1, stuid);
           ps.setString(2, subject);
           ps.setString(3, courseNumber);
           ps.execute();

            
           
        }
        finally
        {
            conn.close();
        }       
   }
    
   public static void deleteCurrentCourse(DataSource ds, String stuid, String subject, String courseNumber) throws SQLException
   {
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
          try
        {
           PreparedStatement ps = conn.prepareStatement(
                  "Delete from CurrentCourse where STUID=? AND subject=? AND course_number=?"
                   
           ); 
           
           ps.setString(1, stuid);
           ps.setString(2, subject);
           ps.setString(3, courseNumber);
           ps.execute();

            
           
        }
        finally
        {
            conn.close();
        }       
   
   }
    
}
