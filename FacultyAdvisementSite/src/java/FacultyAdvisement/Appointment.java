/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FacultyAdvisement;

import java.util.Comparator;

/**
 *
 * @author Hasee
 */
public class Appointment{
    String date;
    String sid;
    String time;
    String datetime;
    long aID;
    boolean taken = true;

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public long getaID() {
        return aID;
    }

    public void setaID(long aID) {
        this.aID = aID;
    }
    
    
    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
        //String year = datetime.substring(23);
        
    }
    
    
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        
        if(sid == null){
            this.taken=false;
        }
        this.sid = sid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    
    
    
    
}


