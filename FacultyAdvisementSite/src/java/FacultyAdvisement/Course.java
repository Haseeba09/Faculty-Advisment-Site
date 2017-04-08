/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FacultyAdvisement;

/**
 *
 * @author abilb
 */
public class Course {
    String name; 
    String number; 
    String subject; 
    int credits;
    
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String numnber) {
        this.number = numnber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
    
    public boolean compare(Course course)
    {
        if((this.subject == null ? course.subject == null : this.subject.equals(course.subject)) && (this.number == null ? course.number == null : this.number.equals(course.number)))
        {
            return true;
        }
        return false; 
    }
    
    
}
