/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FacultyAdvisement;

import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author abilb
 */
public interface CRUDHandler {
    
    void create(DataSource ds) throws SQLException;
    void read (DataSource ds, String key) throws SQLException; 
    Map readAll (DataSource ds) throws SQLException;
    void update (DataSource ds) throws SQLException;
    void delete (DataSource ds, String key) throws SQLException; 
}
