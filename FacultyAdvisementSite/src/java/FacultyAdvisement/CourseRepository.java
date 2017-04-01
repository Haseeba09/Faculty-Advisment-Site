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
public class CourseRepository {
    
    
    private static void handleListResult(ResultSet result, List<Course> list) throws SQLException
    {
    
         while (result.next()) {
                Course course = new Course(); 
                course.setName(result.getString("course_name"));
                course.setCredits(result.getInt("credits"));
                course.setNumber(result.getString("course_number"));
                course.setSubject(result.getString("subject"));
                list.add(course);
            }
    }
    
    public static CourseWithRequisites readCourseWithRequisites (DataSource ds, Course course) throws SQLException
    {
        CourseWithRequisites c = new CourseWithRequisites(); 
        c.setCourse(course); 
        if (ds == null) {
            throw new SQLException("ds is null; Can't get data source");
        }

        Connection conn = ds.getConnection();

        if (conn == null) {
            throw new SQLException("conn is null; Can't get db connection");
        }
        
         List<Course> prerequisites = new ArrayList<Course>();
         List<Course> corequisites = new ArrayList<Course>();
         List<Course> suggested = new ArrayList<Course>();
        try {
            PreparedStatement ps = conn.prepareStatement(
                  "select course_number, course_name, subject, credits FROM Course \n" +
"            Join(select prereq_subject, prereq_number from Course as c \n" +
"            JOIN prerequisite as p on subject = p.course_subject and c.course_number = p.course_number \n" +
"            where c.subject= ? and c.course_number = ?) as req\n" +
"            on subject = prereq_subject and course_number = prereq_number;"
 
            );

            // retrieve customer data from database
            ps.setString(1, course.getSubject());
            ps.setString(2, course.getNumber());
           
            ResultSet result = ps.executeQuery();
            
            handleListResult(result, prerequisites); 
            
           ps = conn.prepareStatement(
                    "select course_number, course_name, subject, credits FROM Course \n" +
"            Join(select coreq_subject, coreq_number from Course as c \n" +
"            JOIN corequisite as p on subject = p.course_subject and c.course_number = p.course_number \n" +
"            where c.subject= ? and c.course_number = ?) as req\n" +
"            on subject = coreq_subject and course_number = coreq_number;"
            );

            // retrieve customer data from database
            ps.setString(1, course.getSubject());
            ps.setString(2, course.getNumber());
            result = ps.executeQuery();
            
            handleListResult(result, corequisites); 
           
             ps = conn.prepareStatement(
                       "select course_number, course_name, subject, credits FROM Course \n" +
"            Join(select suggested_subject, suggested_number from Course as c \n" +
"            JOIN suggested as p on subject = p.course_subject and c.course_number = p.course_number \n" +
"            where c.subject= ? and c.course_number = ?) as req\n" +
"            on subject = suggested_subject and course_number = suggested_number;"
            );

            // retrieve customer data from database
            ps.setString(1, course.getSubject());
            ps.setString(2, course.getNumber());
            result = ps.executeQuery();
            
            handleListResult(result, suggested); 

        } finally {
            conn.close();
        } 
        c.setPrerequisites(prerequisites);
        c.setCorequisite(corequisites);
        c.setSuggested(suggested);
        
        return c;
    }
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
                course.setNumber(result.getString("course_number"));
                course.setSubject(result.getString("subject"));
                list.add(course);
            }

        } finally {
            conn.close();
        }

        return list;
        
    }
   
    public static List<Course> readAvailableCourses(DataSource ds, String stuid) throws SQLException
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
                  "SELECT subject, p.course_number, course_name, credits FROM PREREQUISITE as p" +
                   "LEFT JOIN  " +
                  "(SELECT c.* FROM Course as c JOIN "
                + "Completed on STUID = STUID WHERE STUID = ?) as a "
                +  "on a.course_number = prereq_number "
                          + "AND a.subject = prereq_subject");
                                        
                   
          
           
           ps.setString(1, stuid);
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
    
    
    public static List<Course> readAllCourses (DataSource ds) throws SQLException
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
                  "SELECT * FROM Course"
                   
           ); 
           
           
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
    
   public static void createCompletedCourse(DataSource ds, String stuid, String subject, String courseNumber) throws SQLException
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
                  "Insert into Completed(STUID, subject, course_number) values (?,?,?) "
                   
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
    
   public static void deleteCompleted(DataSource ds, String stuid, String subject, String courseNumber) throws SQLException
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
                  "Delete from Completed where STUID=? AND subject=? AND course_number=?"
                   
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
   // Update Not Necessary at this Point
   
    
}
