/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FacultyAdvisement;

import java.util.Dictionary;
import javax.sql.DataSource;

/**
 *
 * @author abilb
 */
public interface CRUDHandler {
    
    void create(DataSource ds, Object object);
    Object read (DataSource ds, String key); 
    Dictionary readAll (DataSource ds);
    void update (DataSource ds, Object object);
    void delete (DataSource ds, String key); 
}
