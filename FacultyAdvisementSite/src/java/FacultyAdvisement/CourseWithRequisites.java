/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FacultyAdvisement;

import java.util.List;

/**
 *
 * @author abilb
 */
public class CourseWithRequisites {

    private Course course; 
    private List<Course> prerequisites;
    private List<Course> suggested;
    private List<Course> corequisite;
    
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public List<Course> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<Course> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public List<Course> getSuggested() {
        return suggested;
    }

    public void setSuggested(List<Course> suggested) {
        this.suggested = suggested;
    }

    public List<Course> getCorequisite() {
        return corequisite;
    }

    public void setCorequisite(List<Course> corequisite) {
        this.corequisite = corequisite;
    }
    
}
