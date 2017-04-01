/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FacultyAdvisement;

import java.util.Comparator;

public class AppointmentCompare implements Comparator<Appointment>{
    @Override
    public int compare(Appointment o1, Appointment o2) {
        // write comparison logic here like below , it's just a sample
        return o1.getDatetime().compareTo(o2.getDatetime());
    }
}