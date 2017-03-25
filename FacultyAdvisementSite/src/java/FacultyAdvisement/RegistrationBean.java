/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FacultyAdvisement;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 *
 * @author Hasee
 */
@Named(value = "registrationBean")
@SessionScoped
public class RegistrationBean implements Serializable {

    
    @Resource(name = "jdbc/ds_wsp")
    private DataSource ds;
    
    private Student student = new Student();
    private String majorCodeToEdit;
    private String idToEdit;

    public String getIdToEdit() {
        return idToEdit;
    }

    public void setIdToEdit(String idToEdit) {
        this.idToEdit = idToEdit;
    }
    
    

    public String getMajorCodeToEdit() {
        return majorCodeToEdit;
    }

    public void setMajorCodeToEdit(String majorCodeToEdit) {
        this.majorCodeToEdit = majorCodeToEdit;
    }
    
    

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
    
    public String insertStudent() throws SQLException{
        student.setMajorCode(Integer.parseInt(majorCodeToEdit));
        student.setId(Integer.parseInt(idToEdit));
        
        student.create(ds);
        return "/index";
    }
    
}
